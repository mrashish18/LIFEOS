package com.mrashish18.lifeos.core.model

import java.time.Instant

/**
 * Priority levels for tasks.
 */
enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

/**
 * Lifecycle states of a task.
 */
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    POSTPONED,
    ABANDONED
}

/**
 * Domain category for tasks.
 */
enum class TaskCategory {
    WORK,
    PERSONAL,
    HEALTH,
    LEARNING,
    GENERAL
}

/**
 * Domain model representing an actionable item or task.
 */
data class Task(
    val id: String,
    val title: String,
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.PENDING,
    val dueAt: Instant? = null,
    val estimatedMinutes: Int? = null,
    val category: TaskCategory = TaskCategory.GENERAL,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
