package com.mrashish18.lifeos.data.repository

import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.data.local.dao.TaskDao
import com.mrashish18.lifeos.data.local.entity.TaskEntity
import com.mrashish18.lifeos.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-backed implementation of [TaskRepository] providing true local SQLite persistence.
 */
class RoomTaskRepository(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> {
        return taskDao.observeAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTaskById(id: String): Flow<Task?> {
        return taskDao.observeTaskById(id).map { it?.toDomain() }
    }

    override fun getActiveTask(): Flow<Task?> {
        return taskDao.observeActiveTask().map { it?.toDomain() }
    }

    override suspend fun insertTask(task: Task) {
        taskDao.insertOrUpdate(TaskEntity.fromDomain(task))
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(TaskEntity.fromDomain(task))
    }

    override suspend fun deleteTask(id: String) {
        taskDao.deleteById(id)
    }
}
