package com.mrashish18.lifeos.core.decision

import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.RecommendationType
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskCategory
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.core.model.UserAvailability
import com.mrashish18.lifeos.core.model.UserBehaviorModel
import com.mrashish18.lifeos.core.model.WorkloadLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.Instant

class DecisionEngineTest {

    private val fixedNow = Instant.parse("2026-09-14T10:00:00Z")

    private val decisionEngine = DeterministicDecisionEngine(
        idGenerator = { "test-rec-id" },
        clock = { fixedNow }
    )

    @Test
    fun evaluate_criticalWorkload_triggersBreakRecommendation() {
        val snapshot = ContextSnapshot(
            currentTime = fixedNow,
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
        assertTrue("Factors must be present", breakRec.factors.isNotEmpty())
    }

    @Test
    fun evaluate_disconnectedNetwork_triggersResilienceAlert() {
        val snapshot = ContextSnapshot(
            currentTime = fixedNow,
            dayOfWeek = DayOfWeek.TUESDAY,
            networkState = NetworkState.DISCONNECTED,
            workloadLevel = WorkloadLevel.LOW
        )

        val recommendations = decisionEngine.evaluate(snapshot)

        val offlineRec = recommendations.find { it.type == RecommendationType.RESILIENCE_ALERT }
        assertTrue("Expected offline resilience alert when disconnected", offlineRec != null)
        assertEquals("Offline Resilience Mode", offlineRec?.title)
        assertTrue(offlineRec!!.reason.contains("offline", ignoreCase = true))
        assertTrue("Factors must be present", offlineRec.factors.isNotEmpty())
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
            currentTime = fixedNow,
            dayOfWeek = DayOfWeek.WEDNESDAY,
            networkState = NetworkState.CONNECTED,
            activeTask = activeTask,
            workloadLevel = WorkloadLevel.LOW
        )

        val recommendations = decisionEngine.evaluate(snapshot, candidateTasks = listOf(activeTask))

        val focusRec = recommendations.find { it.type == RecommendationType.TASK_FOCUS }
        assertTrue("Expected task focus recommendation for active task", focusRec != null)
        assertTrue(focusRec!!.title.contains("Finalize Milestone 1"))
        assertEquals("t-100", focusRec.targetTaskId)
    }

    @Test
    fun evaluate_candidateTasks_ranksUrgentDueSoonAboveBacklog() {
        val urgentTask = Task(
            id = "task-urgent",
            title = "Ship Critical Patch",
            priority = TaskPriority.URGENT,
            status = TaskStatus.PENDING,
            dueAt = fixedNow.plusSeconds(3600), // due in 1 hour
            estimatedMinutes = 20
        )

        val lowTask = Task(
            id = "task-low",
            title = "Someday Read Article",
            priority = TaskPriority.LOW,
            status = TaskStatus.PENDING,
            dueAt = null,
            estimatedMinutes = 120
        )

        val snapshot = ContextSnapshot(
            currentTime = fixedNow,
            dayOfWeek = DayOfWeek.MONDAY,
            networkState = NetworkState.CONNECTED,
            activeTask = null,
            workloadLevel = WorkloadLevel.LOW
        )

        val recommendations = decisionEngine.evaluate(
            snapshot = snapshot,
            candidateTasks = listOf(lowTask, urgentTask)
        )

        val topRec = recommendations.find { it.type == RecommendationType.TASK_FOCUS }
        assertNotNull(topRec)
        assertEquals("task-urgent", topRec?.targetTaskId)
        assertTrue(topRec!!.title.contains("Ship Critical Patch"))

        // Verify explainable factors
        val factorNames = topRec.factors.map { it.name }
        assertTrue(factorNames.contains("Priority"))
        assertTrue(factorNames.contains("Due Soon"))
        assertTrue(factorNames.contains("Quick Win"))
    }

    @Test
    fun evaluate_withBehaviorModel_incorporatesCategoryMomentum() {
        val workTask = Task(
            id = "task-work",
            title = "Write Architecture Spec",
            priority = TaskPriority.MEDIUM,
            status = TaskStatus.PENDING,
            category = TaskCategory.WORK
        )

        val personalTask = Task(
            id = "task-pers",
            title = "Buy Groceries",
            priority = TaskPriority.MEDIUM,
            status = TaskStatus.PENDING,
            category = TaskCategory.PERSONAL
        )

        val behaviorModel = UserBehaviorModel(
            totalTasksCreated = 10,
            totalTasksCompleted = 8,
            completionRate = 0.80,
            preferredCategories = mapOf(TaskCategory.WORK to 6, TaskCategory.PERSONAL to 1),
            hasSufficientData = true
        )

        val snapshot = ContextSnapshot(
            currentTime = fixedNow,
            dayOfWeek = DayOfWeek.MONDAY,
            networkState = NetworkState.CONNECTED,
            activeTask = null,
            workloadLevel = WorkloadLevel.LOW
        )

        val recommendations = decisionEngine.evaluate(
            snapshot = snapshot,
            candidateTasks = listOf(personalTask, workTask),
            behaviorModel = behaviorModel
        )

        val topRec = recommendations.find { it.type == RecommendationType.TASK_FOCUS }
        assertNotNull(topRec)
        // Work task should win because of category momentum and completion rate
        assertEquals("task-work", topRec?.targetTaskId)
        assertTrue(topRec!!.factors.any { it.name == "Category Habit" })
    }
}
