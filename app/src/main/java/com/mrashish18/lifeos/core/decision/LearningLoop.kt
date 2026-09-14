package com.mrashish18.lifeos.core.decision

import com.mrashish18.lifeos.core.model.BehaviorEvent
import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.Recommendation
import java.time.Instant

/**
 * User action taken in response to a presented recommendation.
 */
enum class UserResponseAction {
    ACCEPTED,
    REJECTED,
    DISMISSED,
    TIMED_OUT
}

/**
 * Represents the observed user response to a specific recommendation.
 */
data class UserFeedback(
    val recommendationId: String,
    val action: UserResponseAction,
    val timestamp: Instant = Instant.now(),
    val notes: String? = null
)

/**
 * Quantitative or qualitative measurement of an outcome resulting from a recommendation.
 */
data class OutcomeResult(
    val recommendationId: String,
    val wasSuccessful: Boolean,
    val completionDurationSeconds: Long? = null,
    val metricDelta: Map<String, Double> = emptyMap(),
    val timestamp: Instant = Instant.now()
)

/**
 * Abstraction for updating the internal user model / preference profile based on measured outcomes.
 */
interface UserModelUpdater {
    suspend fun applyOutcome(feedback: UserFeedback, outcome: OutcomeResult)
}

/**
 * High-level orchestration contract for the LIFEOS adaptive learning loop:
 * Observe → Understand → Recommend → User Response → Measure Outcome → Update User Model.
 */
interface LearningLoop {
    /**
     * Ingests an observed contextual snapshot.
     */
    suspend fun observe(snapshot: ContextSnapshot)

    /**
     * Ingests an observed behavioral event.
     */
    suspend fun recordBehavior(event: BehaviorEvent)

    /**
     * Processes user feedback against a recommendation.
     */
    suspend fun onUserResponse(feedback: UserFeedback)

    /**
     * Measures the objective outcome of a user action and triggers model update.
     */
    suspend fun evaluateOutcome(recommendation: Recommendation, feedback: UserFeedback): OutcomeResult
}
