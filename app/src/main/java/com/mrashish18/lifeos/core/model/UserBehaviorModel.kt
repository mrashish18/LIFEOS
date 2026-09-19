package com.mrashish18.lifeos.core.model

/**
 * Time of day bucket for behavioral analysis.
 */
enum class TimeOfDayBucket {
    MORNING,    // 05:00 - 11:59
    AFTERNOON,  // 12:00 - 16:59
    EVENING,    // 17:00 - 21:59
    NIGHT;      // 22:00 - 04:59

    companion object {
        fun fromHour(hour: Int): TimeOfDayBucket = when (hour) {
            in 5..11 -> MORNING
            in 12..16 -> AFTERNOON
            in 17..21 -> EVENING
            else -> NIGHT
        }

        fun fromInstant(instant: java.time.Instant, zoneId: java.time.ZoneId = java.time.ZoneId.systemDefault()): TimeOfDayBucket =
            fromHour(instant.atZone(zoneId).hour)
    }
}

enum class TaskSizePreference(val label: String, val typicalMinutes: Int) {
    MICRO("Micro (≤ 20m)", 15),
    STANDARD("Standard (20-45m)", 30),
    DEEP("Deep Focus (> 45m)", 60)
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
    val preferredTaskSize: TaskSizePreference? = null,
    val peakProductivityTimeOfDay: TimeOfDayBucket? = null,
    val categoryCompletionRates: Map<TaskCategory, Double> = emptyMap(),
    val totalSessionsStarted: Int = 0,
    val totalSessionsCompleted: Int = 0,
    val averageSessionDurationMinutes: Double? = null,
    val hasSufficientData: Boolean = false
)
