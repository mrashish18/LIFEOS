package com.mrashish18.lifeos.domain.realitycheck

import com.mrashish18.lifeos.core.common.DispatcherProvider
import com.mrashish18.lifeos.core.model.ClaimType
import com.mrashish18.lifeos.core.model.DomainCategory
import com.mrashish18.lifeos.core.model.InvestigationRecord
import com.mrashish18.lifeos.core.model.Verdict
import com.mrashish18.lifeos.data.local.dao.InvestigationDao
import com.mrashish18.lifeos.data.local.entity.InvestigationEntity
import com.mrashish18.lifeos.data.repository.RoomInvestigationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.util.UUID

class RealityCheckPersistenceTest {

    private lateinit var fakeDao: FakeInvestigationDao
    private lateinit var repository: RoomInvestigationRepository

    private val testDispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher = Dispatchers.Unconfined
        override val io: CoroutineDispatcher = Dispatchers.Unconfined
        override val default: CoroutineDispatcher = Dispatchers.Unconfined
        override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
    }

    @Before
    fun setUp() {
        fakeDao = FakeInvestigationDao()
        repository = RoomInvestigationRepository(fakeDao, testDispatcherProvider)
    }

    @Test
    fun `saving investigation persists and emits newest first via observeRecent`() = runBlocking {
        val record1 = InvestigationRecord(
            id = UUID.randomUUID().toString(),
            originalClaim = "Earth orbits the Sun in 365 days",
            normalizedClaim = "Earth orbits the Sun in 365 days",
            claimType = ClaimType.FACTUAL,
            domainCategory = DomainCategory.ASTRONOMY,
            verdict = Verdict.SUPPORTED,
            confidenceScore = 0.92,
            confidencePercentage = 92,
            reasoning = "Orbits in 365.25 solar days.",
            evidenceCount = 2,
            topSourceNames = listOf("NASA JPL", "IAU"),
            timestampEpochMillis = 1000L
        )

        val record2 = InvestigationRecord(
            id = UUID.randomUUID().toString(),
            originalClaim = "Antibiotics kill cold viruses",
            normalizedClaim = "Antibiotics kill cold viruses",
            claimType = ClaimType.FACTUAL,
            domainCategory = DomainCategory.MEDICINE,
            verdict = Verdict.CONTRADICTED,
            confidenceScore = 0.88,
            confidencePercentage = 88,
            reasoning = "Antibiotics treat bacterial infections only.",
            evidenceCount = 2,
            topSourceNames = listOf("CDC", "WHO"),
            timestampEpochMillis = 2000L
        )

        repository.saveInvestigation(record1)
        repository.saveInvestigation(record2)

        val recent = repository.observeRecentInvestigations(5).first()
        assertEquals(2, recent.size)
        // Newest (timestamp 2000L) must be first
        assertEquals(record2.id, recent[0].id)
        assertEquals(Verdict.CONTRADICTED, recent[0].verdict)
        assertEquals(record1.id, recent[1].id)
        assertEquals(Verdict.SUPPORTED, recent[1].verdict)

        // Verify entity to domain mapping preserves fields
        val fetched = repository.getInvestigationById(record2.id)
        assertNotNull(fetched)
        assertEquals("Antibiotics kill cold viruses", fetched!!.originalClaim)
        assertEquals(88, fetched.confidencePercentage)
        assertEquals(DomainCategory.MEDICINE, fetched.domainCategory)
        assertEquals(2, fetched.topSourceNames.size)
    }

    private class FakeInvestigationDao : InvestigationDao {
        private val state = MutableStateFlow<List<InvestigationEntity>>(emptyList())

        override fun observeAll(): Flow<List<InvestigationEntity>> {
            return state.map { list -> list.sortedByDescending { it.timestampEpochMillis } }
        }

        override fun observeRecent(limit: Int): Flow<List<InvestigationEntity>> {
            return state.map { list ->
                list.sortedByDescending { it.timestampEpochMillis }.take(limit)
            }
        }

        override suspend fun getAll(): List<InvestigationEntity> {
            return state.value.sortedByDescending { it.timestampEpochMillis }
        }

        override suspend fun getById(id: String): InvestigationEntity? {
            return state.value.find { it.id == id }
        }

        override suspend fun insert(record: InvestigationEntity): Long {
            val current = state.value.filterNot { it.id == record.id }
            state.value = current + record
            return 1L
        }

        override suspend fun delete(id: String): Int {
            val before = state.value.size
            state.value = state.value.filterNot { it.id == id }
            return before - state.value.size
        }
    }
}
