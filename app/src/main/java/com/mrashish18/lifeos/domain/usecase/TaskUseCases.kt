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

private val CONTROL_CHAR_REGEX = Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]")
private const val MAX_TITLE_LENGTH = 200
private const val MAX_DESCRIPTION_LENGTH = 2000
private const val MAX_ESTIMATED_MINUTES = 10080

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
        val sanitizedTitle = title.replace(CONTROL_CHAR_REGEX, "").trim()
        require(sanitizedTitle.isNotBlank()) { "Task title cannot be blank" }
        require(sanitizedTitle.length <= MAX_TITLE_LENGTH) { "Task title cannot exceed $MAX_TITLE_LENGTH characters" }

        val sanitizedDescription = description.replace(CONTROL_CHAR_REGEX, "").trim()
        require(sanitizedDescription.length <= MAX_DESCRIPTION_LENGTH) { "Task description cannot exceed $MAX_DESCRIPTION_LENGTH characters" }

        if (estimatedMinutes != null) {
            require(estimatedMinutes in 1..MAX_ESTIMATED_MINUTES) {
                "Estimated duration must be between 1 and $MAX_ESTIMATED_MINUTES minutes"
            }
        }

        val task = Task(
            id = UUID.randomUUID().toString(),
            title = sanitizedTitle,
            description = sanitizedDescription,
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
        val sanitizedTitle = task.title.replace(CONTROL_CHAR_REGEX, "").trim()
        require(sanitizedTitle.isNotBlank()) { "Task title cannot be blank" }
        require(sanitizedTitle.length <= MAX_TITLE_LENGTH) { "Task title cannot exceed $MAX_TITLE_LENGTH characters" }

        val sanitizedDescription = task.description.replace(CONTROL_CHAR_REGEX, "").trim()
        require(sanitizedDescription.length <= MAX_DESCRIPTION_LENGTH) { "Task description cannot exceed $MAX_DESCRIPTION_LENGTH characters" }

        if (task.estimatedMinutes != null) {
            require(task.estimatedMinutes in 1..MAX_ESTIMATED_MINUTES) {
                "Estimated duration must be between 1 and $MAX_ESTIMATED_MINUTES minutes"
            }
        }

        taskRepository.updateTask(
            task.copy(
                title = sanitizedTitle,
                description = sanitizedDescription,
                updatedAt = Instant.now()
            )
        )
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
