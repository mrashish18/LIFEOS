package com.mrashish18.lifeos.domain.usecase

import com.mrashish18.lifeos.core.model.BehaviorEvent
import com.mrashish18.lifeos.core.model.BehaviorEventType
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskCategory
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.domain.repository.BehaviorEventRepository
import com.mrashish18.lifeos.domain.repository.TaskRepository
import kotlinx.coroutines.flow.firstOrNull
import java.time.Duration
import java.time.Instant
import java.util.UUID

class CreateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val behaviorEventRepository: BehaviorEventRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String = "",
        priority: TaskPriority = TaskPriority.MEDIUM,
        category: TaskCategory = TaskCategory.GENERAL,
        estimatedMinutes: Int? = null,
        dueAt: Instant? = null
    ): Task {
        require(title.isNotBlank()) { "Task title cannot be blank" }

        val task = Task(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            description = description.trim(),
            priority = priority,
            status = TaskStatus.PENDING,
            category = category,
            estimatedMinutes = estimatedMinutes,
            dueAt = dueAt,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        taskRepository.insertTask(task)

        behaviorEventRepository.recordEvent(
            BehaviorEvent(
                id = UUID.randomUUID().toString(),
                type = BehaviorEventType.TASK_CREATED,
                timestamp = Instant.now(),
                metadata = mapOf(
                    "taskId" to task.id,
                    "title" to task.title,
                    "priority" to task.priority.name,
                    "category" to task.category.name,
                    "estimatedMinutes" to (task.estimatedMinutes?.toString() ?: "unknown")
                )
            )
        )

        return task
    }
}

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        taskRepository.updateTask(task.copy(updatedAt = Instant.now()))
    }
}

class TransitionTaskStatusUseCase(
    private val taskRepository: TaskRepository,
    private val behaviorEventRepository: BehaviorEventRepository
) {
    suspend operator fun invoke(taskId: String, newStatus: TaskStatus): Task? {
        val existingTask = taskRepository.getTaskById(taskId).firstOrNull() ?: return null
        if (existingTask.status == newStatus) return existingTask

        val updatedTask = existingTask.copy(
            status = newStatus,
            updatedAt = Instant.now()
        )
        taskRepository.updateTask(updatedTask)

        val eventType = when (newStatus) {
            TaskStatus.IN_PROGRESS -> BehaviorEventType.TASK_STARTED
            TaskStatus.COMPLETED -> BehaviorEventType.TASK_COMPLETED
            TaskStatus.POSTPONED -> BehaviorEventType.TASK_POSTPONED
            TaskStatus.ABANDONED -> BehaviorEventType.TASK_ABANDONED
            TaskStatus.PENDING -> null
        }

        if (eventType != null) {
            val metadata = mutableMapOf(
                "taskId" to updatedTask.id,
                "title" to updatedTask.title,
                "priority" to updatedTask.priority.name,
                "category" to updatedTask.category.name
            )

            if (eventType == BehaviorEventType.TASK_COMPLETED) {
                val elapsedMinutes = Duration.between(updatedTask.createdAt, Instant.now()).toMinutes()
                metadata["durationMinutes"] = (if (elapsedMinutes > 0) elapsedMinutes else updatedTask.estimatedMinutes?.toLong() ?: 15).toString()
            }

            behaviorEventRepository.recordEvent(
                BehaviorEvent(
                    id = UUID.randomUUID().toString(),
                    type = eventType,
                    timestamp = Instant.now(),
                    metadata = metadata
                )
            )
        }

        return updatedTask
    }
}

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String) {
        taskRepository.deleteTask(taskId)
    }
}
