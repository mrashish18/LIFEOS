package com.mrashish18.lifeos.domain.repository

import com.mrashish18.lifeos.core.model.UserBehaviorModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for observing the deterministic user behavior model.
 */
interface UserBehaviorRepository {
    fun observeUserBehaviorModel(): Flow<UserBehaviorModel>
    suspend fun getUserBehaviorModel(): UserBehaviorModel
}
