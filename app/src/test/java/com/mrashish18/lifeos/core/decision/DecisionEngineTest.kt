package com.mrashish18.lifeos.core.decision

import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.RecommendationType
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.core.model.UserAvailability
import com.mrashish18.lifeos.core.model.WorkloadLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.Instant

class DecisionEngineTest {

    private val decisionEngine = DeterministicDecisionEngine(
        idGenerator = { "test-rec-id" },
        clock = { Instant.parse("2026-09-14T10:00:00Z") }
    )

    @Test
    fun evaluate_criticalWorkload_triggersBreakRecommendation() {
        val snapshot = ContextSnapshot(
            currentTime = Instant.now(),
            dayOfWeek = DayOfWeek.MONDAY,
            networkState = NetworkState.CONNECTED_WIFI,
            workloadLevel = WorkloadLevel.CRITICAL
        )

        val recommendations = decisionEngine.evaluate(snapshot)

        val breakRec = recommendations.find { it.type == RecommendationType.BREAK_SUGGESTION }
        assertTrue("Expected a break recommendation under critical workload", breakRec != null)
        assertEquals("Workload Pace Alert", breakRec?.title)
        assertTrue(breakRec!!.reason.contains("critical", ignoreCase = true))
        assertEquals(0.95, breakRec.confidence, 0.001)
    }

    @Test
    fun evaluate_disconnectedNetwork_triggersResilienceAlert() {
        val snapshot = ContextSnapshot(
            currentTime = Instant.now(),
            dayOfWeek = DayOfWeek.TUESDAY,
            networkState = NetworkState.DISCONNECTED,
            workloadLevel = WorkloadLevel.LOW
        )

        val recommendations = decisionEngine.evaluate(snapshot)

        val offlineRec = recommendations.find { it.type == RecommendationType.RESILIENCE_ALERT }
        assertTrue("Expected offline resilience alert when disconnected", offlineRec != null)
        assertEquals("Offline Resilience Mode", offlineRec?.title)
        assertTrue(offlineRec!!.reason.contains("offline", ignoreCase = true))
    }

    @Test
    fun evaluate_activeTask_triggersTaskFocusRecommendation() {
        val activeTask = Task(
            id = "t-100",
            title = "Finalize Milestone 1",
            priority = TaskPriority.URGENT,
            status = TaskStatus.IN_PROGRESS
        )
        val snapshot = ContextSnapshot(
            currentTime = Instant.now(),
            dayOfWeek = DayOfWeek.WEDNESDAY,
            networkState = NetworkState.CONNECTED,
            activeTask = activeTask,
            workloadLevel = WorkloadLevel.LOW
        )

        val recommendations = decisionEngine.evaluate(snapshot)

        val focusRec = recommendations.find { it.type == RecommendationType.TASK_FOCUS }
        assertTrue("Expected task focus recommendation for active task", focusRec != null)
        assertTrue(focusRec!!.title.contains("Finalize Milestone 1"))
        assertTrue(focusRec.reason.contains("Finalize Milestone 1"))
    }

    @Test
    fun evaluate_availableWithoutActiveTask_promptsForNextTask() {
        val snapshot = ContextSnapshot(
            currentTime = Instant.now(),
            dayOfWeek = DayOfWeek.THURSDAY,
            networkState = NetworkState.CONNECTED,
            activeTask = null,
            userAvailability = UserAvailability.AVAILABLE,
            workloadLevel = WorkloadLevel.LOW
        )

        val recommendations = decisionEngine.evaluate(snapshot)

        val selectTaskRec = recommendations.find { it.type == RecommendationType.TASK_FOCUS }
        assertTrue("Expected prompt to select next task when available", selectTaskRec != null)
        assertEquals("Select Next Priority Task", selectTaskRec?.title)
    }
}
