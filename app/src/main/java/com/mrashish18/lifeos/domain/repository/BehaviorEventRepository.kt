package com.mrashish18.lifeos.domain.repository

import com.mrashish18.lifeos.core.model.BehaviorEvent
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for logging and retrieving behavior events.
 */
interface BehaviorEventRepository {
    suspend fun recordEvent(event: BehaviorEvent)
    fun observeRecentEvents(limit: Int = 100): Flow<List<BehaviorEvent>>
    suspend fun getAllEvents(): List<BehaviorEvent>
}
