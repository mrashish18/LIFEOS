package com.mrashish18.lifeos.data.local

import com.mrashish18.lifeos.core.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing a Room-ready local data access object (DAO) abstraction.
 */
interface TaskLocalDataSource {
    fun getAllTasks(): Flow<List<Task>>
    fun getTaskById(id: String): Flow<Task?>
    suspend fun insertOrUpdate(task: Task)
    suspend fun deleteById(id: String)
}
