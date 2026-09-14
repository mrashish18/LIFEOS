package com.mrashish18.lifeos.core.model

import java.time.Instant

/**
 * Category or intent of an explainable recommendation.
 */
enum class RecommendationType {
    TASK_FOCUS,
    BREAK_SUGGESTION,
    GOAL_ALIGNMENT,
    SYSTEM_NOTICE,
    RESILIENCE_ALERT
}

/**
 * Explainable recommendation produced by the Decision Engine.
 */
data class Recommendation(
    val id: String,
    val type: RecommendationType,
    val title: String,
    val reason: String,
    val confidence: Double,
    val createdAt: Instant = Instant.now()
) {
    init {
        require(confidence in 0.0..1.0) {
            "Confidence score must be between 0.0 and 1.0, was: $confidence"
        }
    }
}
