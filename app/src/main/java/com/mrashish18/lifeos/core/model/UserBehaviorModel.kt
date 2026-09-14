package com.mrashish18.lifeos.core.model

/**
 * Time of day bucket for behavioral analysis.
 */
enum class TimeOfDayBucket {
    MORNING,    // 05:00 - 11:59
    AFTERNOON,  // 12:00 - 16:59
    EVENING,    // 17:00 - 21:59
    NIGHT       // 22:00 - 04:59
}

/**
 * Deterministic model of observed user behavior derived strictly from recorded events.
 *
 * When insufficient observations exist, rates and durations are explicitly null
 * rather than fabricated default numbers (like 0.0).
 */
data class UserBehaviorModel(
    val totalTasksCreated: Int = 0,
    val totalTasksCompleted: Int = 0,
    val totalTasksPostponed: Int = 0,
    val totalTasksAbandoned: Int = 0,
    val completionRate: Double? = null,
    val postponementRate: Double? = null,
    val abandonmentRate: Double? = null,
    val averageCompletedDurationMinutes: Double? = null,
    val preferredCategories: Map<TaskCategory, Int> = emptyMap(),
    val completionsByTimeOfDay: Map<TimeOfDayBucket, Int> = emptyMap(),
    val hasSufficientData: Boolean = false
)
