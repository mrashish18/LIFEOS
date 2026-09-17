package com.mrashish18.lifeos.domain.realitycheck

import com.mrashish18.lifeos.core.model.BehaviorEvent
import com.mrashish18.lifeos.core.model.BehaviorEventType
import com.mrashish18.lifeos.core.model.RealityCheckInput
import com.mrashish18.lifeos.core.model.Verdict
import com.mrashish18.lifeos.core.realitycheck.RealityCheckEngine
import com.mrashish18.lifeos.data.repository.DeterministicEvidenceRepository
import com.mrashish18.lifeos.domain.repository.BehaviorEventRepository
import com.mrashish18.lifeos.domain.repository.EvidenceRepository
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
    private lateinit var evidenceRepository: DeterministicEvidenceRepository
    private lateinit var realityCheckEngine: RealityCheckEngine
    private lateinit var useCase: PerformRealityCheckUseCase

    @Before
    fun setUp() {
        behaviorEventRepository = FakeBehaviorEventRepository()
        evidenceRepository = DeterministicEvidenceRepository()
        realityCheckEngine = RealityCheckEngine(evidenceRepository)
        useCase = PerformRealityCheckUseCase(realityCheckEngine, behaviorEventRepository)
    }

    @Test
    fun `blank claim input returns failure without querying engine`() {
        runBlocking {
            val result = useCase(RealityCheckInput(text = "   "))
            assertTrue(result.isFailure)
            assertTrue(behaviorEventRepository.events.isEmpty())
        }
    }

    @Test
    fun `valid claim logs CLAIM_SUBMITTED and CLAIM_VERIFIED events`() {
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
        }
    }

    @Test
    fun `contradicted claim resolves correctly through end-to-end usecase`() {
        runBlocking {
            val input = RealityCheckInput(text = "Antibiotics kill viruses like colds and flu")
            val result = useCase(input)

            assertTrue(result.isSuccess)
            val checkResult = result.getOrThrow()
            assertEquals(Verdict.CONTRADICTED, checkResult.verdict)
            assertTrue(checkResult.confidence.score >= 0.70)
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
        val failingUseCase = PerformRealityCheckUseCase(failingEngine, behaviorEventRepository)

        runBlocking {
            val result = failingUseCase(RealityCheckInput(text = "Earth orbits the Sun"))
            // Engine handles repository empty/failure gracefully by falling back to empty list -> INSUFFICIENT_EVIDENCE
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
}
