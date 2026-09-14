package com.mrashish18.lifeos.core.model

import java.time.Instant

/**
 * Status representation for long-term and short-term goals.
 */
enum class GoalStatus {
    NOT_STARTED,
    IN_PROGRESS,
    ACHIEVED,
    PAUSED,
    ABANDONED
}

/**
 * Domain model representing a user or system goal.
 */
data class Goal(
    val id: String,
    val title: String,
    val description: String = "",
    val status: GoalStatus = GoalStatus.NOT_STARTED,
    val targetDate: Instant? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
