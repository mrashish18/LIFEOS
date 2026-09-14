package com.mrashish18.lifeos.domain.repository

import com.mrashish18.lifeos.core.model.Goal
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing [Goal] entities.
 */
interface GoalRepository {
    fun getGoals(): Flow<List<Goal>>
    fun getGoalById(id: String): Flow<Goal?>
    suspend fun insertGoal(goal: Goal)
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(id: String)
}
