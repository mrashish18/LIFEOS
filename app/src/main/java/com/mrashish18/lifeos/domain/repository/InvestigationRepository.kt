package com.mrashish18.lifeos.domain.repository

import com.mrashish18.lifeos.core.model.InvestigationRecord
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository contract for persisting and retrieving RealityCheck investigations.
 */
interface InvestigationRepository {
    fun observeRecentInvestigations(limit: Int = 10): Flow<List<InvestigationRecord>>
    suspend fun getAllInvestigations(): List<InvestigationRecord>
    suspend fun getInvestigationById(id: String): InvestigationRecord?
    suspend fun saveInvestigation(record: InvestigationRecord)
}
