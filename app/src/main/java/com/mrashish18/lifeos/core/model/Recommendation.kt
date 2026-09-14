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
 * A structured, transparent factor contributing to an explainable recommendation.
 */
data class RecommendationFactor(
    val name: String,
    val description: String,
    val scoreContribution: Double = 0.0
)

/**
 * Explainable recommendation produced by the Decision Engine.
 */
data class Recommendation(
    val id: String,
    val type: RecommendationType,
    val title: String,
    val reason: String,
    val confidence: Double,
    val factors: List<RecommendationFactor> = emptyList(),
    val targetTaskId: String? = null,
    val createdAt: Instant = Instant.now()
) {
    init {
        require(confidence in 0.0..1.0) {
            "Confidence score must be between 0.0 and 1.0, was: $confidence"
        }
    }
}
