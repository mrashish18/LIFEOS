package com.mrashish18.lifeos.core.decision

import com.mrashish18.lifeos.core.model.BehaviorEvent
import com.mrashish18.lifeos.core.model.BehaviorEventType
import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.Recommendation
import com.mrashish18.lifeos.core.model.RecommendationType
import com.mrashish18.lifeos.core.model.UserAvailability
import com.mrashish18.lifeos.core.model.UserBehaviorModel
import com.mrashish18.lifeos.core.model.WorkloadLevel
import com.mrashish18.lifeos.domain.repository.BehaviorEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.Instant

class LearningLoopTest {

    private class FakeBehaviorEventRepository : BehaviorEventRepository {
        val recordedEvents = mutableListOf<BehaviorEvent>()

        override suspend fun recordEvent(event: BehaviorEvent) {
            recordedEvents.add(event)
        }

        override fun observeRecentEvents(limit: Int): Flow<List<BehaviorEvent>> {
            return flowOf(recordedEvents.takeLast(limit))
        }

        override suspend fun getAllEvents(): List<BehaviorEvent> {
            return recordedEvents.toList()
        }
    }

    private val fakeRepo = FakeBehaviorEventRepository()
    private var currentTime = Instant.parse("2026-09-14T09:00:00Z")
    private val learningLoop = DefaultLearningLoop(
        behaviorEventRepository = fakeRepo,
        clock = { currentTime },
        idGenerator = { "test-evt-${fakeRepo.recordedEvents.size + 1}" }
    )

    @Test
    fun observe_updatesLatestSnapshotState() = runBlocking {
        val snapshot = ContextSnapshot(
            currentTime = currentTime,
            dayOfWeek = DayOfWeek.MONDAY,
            networkState = NetworkState.CONNECTED,
            workloadLevel = WorkloadLevel.LOW,
            userAvailability = UserAvailability.AVAILABLE
        )

        learningLoop.observe(snapshot)

        assertEquals(snapshot, learningLoop.latestSnapshot.value)
    }

    @Test
    fun onUserResponse_accepted_createsRecommendationAcceptedAndStartsSession() = runBlocking {
        val feedback = UserFeedback(
            recommendationId = "rec-42",
            action = UserResponseAction.ACCEPTED,
            timestamp = currentTime,
            notes = "Deep Work Session"
        )

        learningLoop.onUserResponse(feedback)

        assertEquals(2, fakeRepo.recordedEvents.size)

        val acceptedEvent = fakeRepo.recordedEvents[0]
        assertEquals(BehaviorEventType.RECOMMENDATION_ACCEPTED, acceptedEvent.type)
        assertEquals("rec-42", acceptedEvent.metadata["recommendationId"])
        assertEquals("ACCEPTED", acceptedEvent.metadata["action"])

        val sessionEvent = fakeRepo.recordedEvents[1]
        assertEquals(BehaviorEventType.SESSION_STARTED, sessionEvent.type)
        assertEquals("rec-42", sessionEvent.metadata["recommendationId"])

        val activeSession = learningLoop.getActiveSession("rec-42")
        assertNotNull(activeSession)
        assertEquals("rec-42", activeSession?.recommendationId)
        assertEquals(currentTime, activeSession?.startTime)
    }

    @Test
    fun onUserResponse_dismissed_createsRecommendationRejected() = runBlocking {
        val feedback = UserFeedback(
            recommendationId = "rec-99",
            action = UserResponseAction.DISMISSED,
            timestamp = currentTime
        )

        learningLoop.onUserResponse(feedback)

        assertEquals(1, fakeRepo.recordedEvents.size)
        val event = fakeRepo.recordedEvents[0]
        assertEquals(BehaviorEventType.RECOMMENDATION_REJECTED, event.type)
        assertEquals("DISMISSED", event.metadata["action"])
        assertNull(learningLoop.getActiveSession("rec-99"))
    }

    @Test
    fun evaluateOutcome_acceptedSession_logsCompletionWithRealDuration() = runBlocking {
        // 1. Accept recommendation at T0 (09:00:00)
        learningLoop.onUserResponse(
            UserFeedback(
                recommendationId = "rec-101",
                action = UserResponseAction.ACCEPTED,
                timestamp = currentTime,
                notes = "task-alpha"
            )
        )

        // 2. Advance time by 25 minutes (1500 seconds) to 09:25:00
        currentTime = currentTime.plusSeconds(1500)

        val recommendation = Recommendation(
            id = "rec-101",
            type = RecommendationType.TASK_FOCUS,
            title = "Focus: Task Alpha",
            reason = "High priority",
            confidence = 0.90,
            targetTaskId = "task-alpha",
            createdAt = currentTime.minusSeconds(1500)
        )

        val outcome = learningLoop.evaluateOutcome(
            recommendation = recommendation,
            feedback = UserFeedback(
                recommendationId = "rec-101",
                action = UserResponseAction.ACCEPTED,
                timestamp = currentTime
            )
        )

        assertTrue(outcome.wasSuccessful)
        assertEquals(1500L, outcome.completionDurationSeconds)
        assertEquals(25.0, outcome.metricDelta["durationMinutes"]!!, 0.001)

        // Events: ACCEPTED (0), SESSION_STARTED (1), SESSION_COMPLETED (2)
        assertEquals(3, fakeRepo.recordedEvents.size)
        val completionEvent = fakeRepo.recordedEvents[2]
        assertEquals(BehaviorEventType.SESSION_COMPLETED, completionEvent.type)
        assertEquals("1500", completionEvent.metadata["durationSeconds"])
        assertEquals("task-alpha", completionEvent.metadata["targetTaskId"])

        // Active session removed
        assertNull(learningLoop.getActiveSession("rec-101"))
    }

    @Test
    fun evaluateOutcome_rejectedOrInterrupted_logsInterruptedSession() = runBlocking {
        val recommendation = Recommendation(
            id = "rec-202",
            type = RecommendationType.TASK_FOCUS,
            title = "Focus: Task Beta",
            reason = "Upcoming deadline",
            confidence = 0.85,
            targetTaskId = "task-beta",
            createdAt = currentTime
        )

        val outcome = learningLoop.evaluateOutcome(
            recommendation = recommendation,
            feedback = UserFeedback(
                recommendationId = "rec-202",
                action = UserResponseAction.REJECTED,
                timestamp = currentTime
            )
        )

        assertFalse(outcome.wasSuccessful)
        assertEquals(1, fakeRepo.recordedEvents.size)
        val event = fakeRepo.recordedEvents[0]
        assertEquals(BehaviorEventType.SESSION_INTERRUPTED, event.type)
        assertEquals("REJECTED", event.metadata["reason"])
    }
}
