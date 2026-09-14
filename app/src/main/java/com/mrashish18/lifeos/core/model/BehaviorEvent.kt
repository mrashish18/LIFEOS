package com.mrashish18.lifeos.core.model

import java.time.Instant

/**
 * Extensible event types across LIFEOS intelligence domains:
 * - Personal Intelligence (Tasks, Sessions, Recommendations)
 * - Trust Intelligence (RealityCheck claims)
 * - Resilience Intelligence (RescueMesh emergency events)
 */
enum class BehaviorEventType {
    // Task Lifecycle Events
    TASK_CREATED,
    TASK_STARTED,
    TASK_COMPLETED,
    TASK_POSTPONED,
    TASK_ABANDONED,

    // Recommendation Events
    RECOMMENDATION_SHOWN,
    RECOMMENDATION_ACCEPTED,
    RECOMMENDATION_REJECTED,

    // Session Tracking Events
    SESSION_STARTED,
    SESSION_COMPLETED,
    SESSION_INTERRUPTED,

    // Trust Intelligence (RealityCheck) Events
    CLAIM_SUBMITTED,
    CLAIM_VERIFIED,

    // Resilience Intelligence (RescueMesh) Events
    EMERGENCY_STARTED,
    EMERGENCY_MESSAGE_SENT,
    EMERGENCY_MESSAGE_RECEIVED
}

/**
 * Domain model representing an observed behavioral event in the learning loop.
 */
data class BehaviorEvent(
    val id: String,
    val type: BehaviorEventType,
    val timestamp: Instant = Instant.now(),
    val metadata: Map<String, String> = emptyMap()
)
