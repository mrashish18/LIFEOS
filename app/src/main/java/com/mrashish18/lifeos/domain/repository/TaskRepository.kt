package com.mrashish18.lifeos.domain.repository

import com.mrashish18.lifeos.core.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing [Task] entities.
 * Decouples domain logic from Room, remote endpoints, or in-memory stores.
 */
interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    fun getTaskById(id: String): Flow<Task?>
    fun getActiveTask(): Flow<Task?>
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: String)
}
