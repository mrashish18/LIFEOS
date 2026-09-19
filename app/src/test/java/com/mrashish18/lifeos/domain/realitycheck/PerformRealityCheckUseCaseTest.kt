package com.mrashish18.lifeos.domain.realitycheck

import com.mrashish18.lifeos.core.model.BehaviorEvent
import com.mrashish18.lifeos.core.model.BehaviorEventType
import com.mrashish18.lifeos.core.model.InvestigationRecord
import com.mrashish18.lifeos.core.model.RealityCheckInput
import com.mrashish18.lifeos.core.model.Verdict
import com.mrashish18.lifeos.core.realitycheck.RealityCheckEngine
import com.mrashish18.lifeos.data.repository.DeterministicEvidenceRepository
import com.mrashish18.lifeos.domain.repository.BehaviorEventRepository
import com.mrashish18.lifeos.domain.repository.EvidenceRepository
import com.mrashish18.lifeos.domain.repository.InvestigationRepository
import com.mrashish18.lifeos.domain.usecase.PerformRealityCheckUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PerformRealityCheckUseCaseTest {

    private lateinit var behaviorEventRepository: FakeBehaviorEventRepository
    private lateinit var investigationRepository: FakeInvestigationRepository
    private lateinit var evidenceRepository: DeterministicEvidenceRepository
    private lateinit var realityCheckEngine: RealityCheckEngine
    private lateinit var useCase: PerformRealityCheckUseCase

    @Before
    fun setUp() {
        behaviorEventRepository = FakeBehaviorEventRepository()
        investigationRepository = FakeInvestigationRepository()
        evidenceRepository = DeterministicEvidenceRepository()
        realityCheckEngine = RealityCheckEngine(evidenceRepository)
        useCase = PerformRealityCheckUseCase(
            realityCheckEngine = realityCheckEngine,
            behaviorEventRepository = behaviorEventRepository,
            investigationRepository = investigationRepository
        )
    }

    @Test
    fun `blank claim input returns failure without querying engine`() {
        runBlocking {
            val result = useCase(RealityCheckInput(text = "   "))
            assertTrue(result.isFailure)
            assertTrue(behaviorEventRepository.events.isEmpty())
            assertTrue(investigationRepository.investigations.isEmpty())
        }
    }

    @Test
    fun `valid claim logs events and saves investigation record`() {
        runBlocking {
            val input = RealityCheckInput(text = "Earth orbits the Sun in 365 days")
            val result = useCase(input)

            assertTrue(result.isSuccess)
            val checkResult = result.getOrThrow()
            assertEquals(Verdict.SUPPORTED, checkResult.verdict)

            // Verify both behavior events logged
            assertEquals(2, behaviorEventRepository.events.size)
            assertEquals(BehaviorEventType.CLAIM_SUBMITTED, behaviorEventRepository.events[0].type)
            assertEquals(BehaviorEventType.CLAIM_VERIFIED, behaviorEventRepository.events[1].type)

            // Verify metadata recorded
            assertEquals("SUPPORTED", behaviorEventRepository.events[1].metadata["verdict"])

            // Verify investigation persisted
            assertEquals(1, investigationRepository.investigations.size)
            val record = investigationRepository.investigations.first()
            assertEquals(checkResult.claim.rawText, record.claimText)
            assertEquals(Verdict.SUPPORTED, record.verdict)
            assertEquals(checkResult.confidence.percentage, record.confidencePercentage)
            assertEquals(checkResult.confidence.score, record.confidenceScore, 0.001)
            assertEquals(checkResult.analyzedEvidence.size, record.sourcesCount)
        }
    }

    @Test
    fun `interrogative claim normalizes and resolves contradicted correctly`() {
        runBlocking {
            val input = RealityCheckInput(text = "Antibiotics cure viral infections?")
            val result = useCase(input)

            assertTrue(result.isSuccess)
            val checkResult = result.getOrThrow()
            assertEquals(Verdict.CONTRADICTED, checkResult.verdict)
            assertTrue(checkResult.confidence.score >= 0.70)
            assertTrue(checkResult.claim.normalizedText.contains("Antibiotics"))
            assertTrue(!checkResult.claim.normalizedText.endsWith("?"))
        }
    }

    @Test
    fun `mixed evidence claim resolves to MIXED with balanced interpretation`() {
        runBlocking {
            val input = RealityCheckInput(text = "Moderate coffee consumption protects against cardiovascular disease")
            val result = useCase(input)

            assertTrue(result.isSuccess)
            val checkResult = result.getOrThrow()
            assertEquals(Verdict.MIXED, checkResult.verdict)
            assertTrue(checkResult.analyzedEvidence.isNotEmpty())
            assertTrue(checkResult.interpretation.isNotBlank())
        }
    }

    @Test
    fun `claim with no matching evidence returns INSUFFICIENT_EVIDENCE`() {
        runBlocking {
            val input = RealityCheckInput(text = "Extraterrestrial spacecraft landed secretly in Atlantis in 1200 BC")
            val result = useCase(input)

            assertTrue(result.isSuccess)
            val checkResult = result.getOrThrow()
            assertEquals(Verdict.INSUFFICIENT_EVIDENCE, checkResult.verdict)
            // Insufficient evidence should also be persisted to history for auditable trails
            assertEquals(1, investigationRepository.investigations.size)
            assertEquals(Verdict.INSUFFICIENT_EVIDENCE, investigationRepository.investigations.first().verdict)
        }
    }

    @Test
    fun `repository failure is safely handled and returns failure result`() {
        val failingRepo = object : EvidenceRepository {
            override suspend fun search(query: String): Result<List<com.mrashish18.lifeos.core.model.Evidence>> {
                return Result.failure(RuntimeException("Network failure simulation"))
            }
        }
        val failingEngine = RealityCheckEngine(failingRepo)
        val failingUseCase = PerformRealityCheckUseCase(failingEngine, behaviorEventRepository, investigationRepository)

        runBlocking {
            val result = failingUseCase(RealityCheckInput(text = "Earth orbits the Sun"))
            assertTrue(result.isSuccess)
            assertEquals(Verdict.INSUFFICIENT_EVIDENCE, result.getOrThrow().verdict)
        }
    }

    private class FakeBehaviorEventRepository : BehaviorEventRepository {
        val events = mutableListOf<BehaviorEvent>()

        override suspend fun recordEvent(event: BehaviorEvent) {
            events.add(event)
        }

        override fun observeRecentEvents(limit: Int): Flow<List<BehaviorEvent>> {
            return flowOf(events.takeLast(limit))
        }

        override suspend fun getAllEvents(): List<BehaviorEvent> {
            return events.toList()
        }
    }

    private class FakeInvestigationRepository : InvestigationRepository {
        val investigations = mutableListOf<InvestigationRecord>()

        override fun observeRecentInvestigations(limit: Int): Flow<List<InvestigationRecord>> {
            return flowOf(investigations.takeLast(limit).reversed())
        }

        override suspend fun getAllInvestigations(): List<InvestigationRecord> {
            return investigations.toList()
        }

        override suspend fun getInvestigationById(id: String): InvestigationRecord? {
            return investigations.find { it.id == id }
        }

        override suspend fun saveInvestigation(record: InvestigationRecord) {
            investigations.add(record)
        }
    }
}
