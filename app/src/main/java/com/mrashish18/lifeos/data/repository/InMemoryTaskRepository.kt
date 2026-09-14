package com.mrashish18.lifeos.data.repository

import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskCategory
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.Instant

/**
 * Thread-safe in-memory repository implementation of [TaskRepository].
 * Seeded with foundational tasks to demonstrate working reactive data flow.
 */
class InMemoryTaskRepository(
    initialTasks: List<Task> = defaultSeedTasks()
) : TaskRepository {

    private val tasksFlow = MutableStateFlow(initialTasks)

    override fun getTasks(): Flow<List<Task>> = tasksFlow

    override fun getTaskById(id: String): Flow<Task?> {
        return tasksFlow.map { tasks -> tasks.find { it.id == id } }
    }

    override fun getActiveTask(): Flow<Task?> {
        return tasksFlow.map { tasks ->
            tasks.find { it.status == TaskStatus.IN_PROGRESS }
        }
    }

    override suspend fun insertTask(task: Task) {
        tasksFlow.update { current ->
            current.filterNot { it.id == task.id } + task
        }
    }

    override suspend fun updateTask(task: Task) {
        tasksFlow.update { current ->
            current.map { if (it.id == task.id) task.copy(updatedAt = Instant.now()) else it }
        }
    }

    override suspend fun deleteTask(id: String) {
        tasksFlow.update { current ->
            current.filterNot { it.id == id }
        }
    }

    companion object {
        fun defaultSeedTasks(): List<Task> = listOf(
            Task(
                id = "task-arch-01",
                title = "Establish LIFEOS Architecture",
                description = "Build clean MVVM, repository layer, and domain models",
                priority = TaskPriority.HIGH,
                status = TaskStatus.IN_PROGRESS,
                estimatedMinutes = 60,
                category = TaskCategory.WORK
            ),
            Task(
                id = "task-context-02",
                title = "Calibrate Context Engine",
                description = "Monitor local time, day of week, and network status",
                priority = TaskPriority.MEDIUM,
                status = TaskStatus.PENDING,
                estimatedMinutes = 30,
                category = TaskCategory.WORK
            )
        )
    }
}
