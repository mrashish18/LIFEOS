package com.mrashish18.lifeos.core.model

import java.time.DayOfWeek
import java.time.Instant

/**
 * Observed connectivity status of the device.
 */
enum class NetworkState {
    CONNECTED_WIFI,
    CONNECTED_CELLULAR,
    CONNECTED,
    DISCONNECTED,
    UNKNOWN
}

/**
 * Current estimated cognitive/task workload level.
 */
enum class WorkloadLevel {
    LOW,
    MODERATE,
    HIGH,
    CRITICAL
}

/**
 * Observed or scheduled user availability.
 */
enum class UserAvailability {
    AVAILABLE,
    BUSY,
    FOCUS,
    DO_NOT_DISTURB
}

/**
 * Immutable snapshot of contextual signals captured at a specific point in time.
 */
data class ContextSnapshot(
    val currentTime: Instant = Instant.now(),
    val dayOfWeek: DayOfWeek,
    val networkState: NetworkState = NetworkState.UNKNOWN,
    val activeTask: Task? = null,
    val workloadLevel: WorkloadLevel = WorkloadLevel.LOW,
    val userAvailability: UserAvailability = UserAvailability.AVAILABLE
)
