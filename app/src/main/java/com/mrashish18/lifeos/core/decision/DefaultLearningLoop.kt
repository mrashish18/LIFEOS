package com.mrashish18.lifeos.core.decision

import com.mrashish18.lifeos.core.model.BehaviorEvent
import com.mrashish18.lifeos.core.model.BehaviorEventType
import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.Recommendation
import com.mrashish18.lifeos.domain.repository.BehaviorEventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Concrete, production implementation of [LearningLoop].
 *
 * Implements the full feedback-learning cycle:
 * 1. Ingests ContextSnapshots (observe)
 * 2. Ingests BehaviorEvents (recordBehavior)
 * 3. Records UserFeedback on recommendations (onUserResponse) -> launches focus session tracking
 * 4. Measures OutcomeResults (evaluateOutcome) -> records completed/interrupted session metrics
 */
class DefaultLearningLoop(
    private val behaviorEventRepository: BehaviorEventRepository,
    private val clock: () -> Instant = { Instant.now() },
    private val idGenerator: () -> String = { UUID.randomUUID().toString() }
) : LearningLoop {

    data class ActiveSession(
        val recommendationId: String,
        val targetTaskId: String?,
        val startTime: Instant,
        val title: String
    )

    private val _activeSessions = ConcurrentHashMap<String, ActiveSession>()
    private val _latestSnapshot = MutableStateFlow<ContextSnapshot?>(null)
    val latestSnapshot: StateFlow<ContextSnapshot?> = _latestSnapshot.asStateFlow()

    override suspend fun observe(snapshot: ContextSnapshot) {
        _latestSnapshot.value = snapshot
    }

    override suspend fun recordBehavior(event: BehaviorEvent) {
        behaviorEventRepository.recordEvent(event)
    }

    override suspend fun onUserResponse(feedback: UserFeedback) {
        val now = clock()
        val eventType = when (feedback.action) {
            UserResponseAction.ACCEPTED -> BehaviorEventType.RECOMMENDATION_ACCEPTED
            UserResponseAction.REJECTED,
            UserResponseAction.DISMISSED,
            UserResponseAction.TIMED_OUT -> BehaviorEventType.RECOMMENDATION_REJECTED
        }

        // Record the recommendation response event
        recordBehavior(
            BehaviorEvent(
                id = idGenerator(),
                type = eventType,
                timestamp = now,
                metadata = mapOf(
                    "recommendationId" to feedback.recommendationId,
                    "action" to feedback.action.name,
                    "notes" to (feedback.notes ?: "")
                )
            )
        )

        if (feedback.action == UserResponseAction.ACCEPTED) {
            val session = ActiveSession(
                recommendationId = feedback.recommendationId,
                targetTaskId = feedback.notes,
                startTime = now,
                title = feedback.notes ?: "Focus Session"
            )
            _activeSessions[feedback.recommendationId] = session

            recordBehavior(
                BehaviorEvent(
                    id = idGenerator(),
                    type = BehaviorEventType.SESSION_STARTED,
                    timestamp = now,
                    metadata = mapOf(
                        "recommendationId" to feedback.recommendationId,
                        "startTime" to now.toString()
                    )
                )
            )
        }
    }

    override suspend fun evaluateOutcome(
        recommendation: Recommendation,
        feedback: UserFeedback
    ): OutcomeResult {
        val now = clock()
        val session = _activeSessions.remove(recommendation.id)
        val startTime = session?.startTime ?: feedback.timestamp
        val durationSeconds = Duration.between(startTime, now).seconds
        val durationMinutes = durationSeconds.toDouble() / 60.0

        val isSuccess = feedback.action == UserResponseAction.ACCEPTED

        val outcome = OutcomeResult(
            recommendationId = recommendation.id,
            wasSuccessful = isSuccess,
            completionDurationSeconds = durationSeconds,
            metricDelta = mapOf(
                "durationMinutes" to durationMinutes,
                "confidenceScore" to recommendation.confidence
            ),
            timestamp = now
        )

        if (isSuccess) {
            recordBehavior(
                BehaviorEvent(
                    id = idGenerator(),
                    type = BehaviorEventType.SESSION_COMPLETED,
                    timestamp = now,
                    metadata = mapOf(
                        "recommendationId" to recommendation.id,
                        "durationSeconds" to durationSeconds.toString(),
                        "durationMinutes" to durationMinutes.toString(),
                        "targetTaskId" to (recommendation.targetTaskId ?: "none")
                    )
                )
            )
        } else {
            recordBehavior(
                BehaviorEvent(
                    id = idGenerator(),
                    type = BehaviorEventType.SESSION_INTERRUPTED,
                    timestamp = now,
                    metadata = mapOf(
                        "recommendationId" to recommendation.id,
                        "reason" to feedback.action.name
                    )
                )
            )
        }

        return outcome
    }

    fun getActiveSession(recommendationId: String): ActiveSession? = _activeSessions[recommendationId]
    fun getAllActiveSessions(): List<ActiveSession> = _activeSessions.values.toList()
}
