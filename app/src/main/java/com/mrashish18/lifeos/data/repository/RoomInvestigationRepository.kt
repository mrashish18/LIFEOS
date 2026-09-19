package com.mrashish18.lifeos.data.repository

import com.mrashish18.lifeos.core.common.DefaultDispatcherProvider
import com.mrashish18.lifeos.core.common.DispatcherProvider
import com.mrashish18.lifeos.core.model.InvestigationRecord
import com.mrashish18.lifeos.data.local.dao.InvestigationDao
import com.mrashish18.lifeos.data.local.entity.InvestigationEntity
import com.mrashish18.lifeos.domain.repository.InvestigationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Room-backed implementation of [InvestigationRepository].
 */
class RoomInvestigationRepository(
    private val investigationDao: InvestigationDao,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) : InvestigationRepository {

    override fun observeRecentInvestigations(limit: Int): Flow<List<InvestigationRecord>> {
        return investigationDao.observeRecent(limit).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(dispatcherProvider.default)
    }

    override suspend fun getAllInvestigations(): List<InvestigationRecord> = withContext(dispatcherProvider.io) {
        investigationDao.getAll().map { it.toDomain() }
    }

    override suspend fun getInvestigationById(id: String): InvestigationRecord? = withContext(dispatcherProvider.io) {
        investigationDao.getById(id)?.toDomain()
    }

    override suspend fun saveInvestigation(record: InvestigationRecord): Unit = withContext(dispatcherProvider.io) {
        investigationDao.insert(InvestigationEntity.fromDomain(record))
        Unit
    }
}
