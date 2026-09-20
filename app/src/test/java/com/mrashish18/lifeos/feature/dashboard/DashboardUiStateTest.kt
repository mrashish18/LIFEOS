package com.mrashish18.lifeos.feature.dashboard

import com.mrashish18.lifeos.core.model.ClaimType
import com.mrashish18.lifeos.core.model.DomainCategory
import com.mrashish18.lifeos.core.model.InvestigationRecord
import com.mrashish18.lifeos.core.model.Recommendation
import com.mrashish18.lifeos.core.model.RecommendationFactor
import com.mrashish18.lifeos.core.model.RecommendationType
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskCategory
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskSizePreference
import com.mrashish18.lifeos.core.model.TimeOfDayBucket
import com.mrashish18.lifeos.core.model.UserBehaviorModel
import com.mrashish18.lifeos.core.model.Verdict
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class DashboardUiStateTest {

    @Test
    fun defaultStateValues_haveSafeHonestDefaults() {
        val state = DashboardUiState()
        assertTrue(state.isLoading)
        assertEquals("Active • Operational", state.systemStatus)
        assertNull(state.contextSnapshot)
        assertTrue(state.recommendations.isEmpty())
        assertTrue(state.tasks.isEmpty())
        assertEquals(0, state.pendingCount)
        assertEquals(0, state.completedCount)
        assertNull(state.behaviorModel)
        assertNull(state.lastFeedbackMessage)
        assertEquals(0, state.investigationCount)
        assertNull(state.latestInvestigation)
        assertEquals(0, state.emergencyQueuedCount)
        assertEquals(0, state.emergencyRelayingCount)
        assertFalse(state.hasCriticalEmergency)
    }

    @Test
    fun crossPillarState_reflectsTruthAndResilienceIntelligence() {
        val now = Instant.now()
        val mockRecord = InvestigationRecord(
            id = "inv-1",
            originalClaim = "Earth orbits the Sun",
            normalizedClaim = "earth orbits the sun",
            claimType = ClaimType.FACTUAL,
            domainCategory = DomainCategory.ASTRONOMY,
            verdict = Verdict.SUPPORTED,
            confidenceScore = 0.94,
            confidencePercentage = 94,
            reasoning = "Corroborated by astronomical observations",
            evidenceCount = 3,
            topSourceNames = listOf("NASA", "ESA"),
            timestampEpochMillis = now.toEpochMilli()
        )

        val state = DashboardUiState(
            isLoading = false,
            investigationCount = 4,
            latestInvestigation = mockRecord,
            emergencyQueuedCount = 2,
            emergencyRelayingCount = 1,
            hasCriticalEmergency = true
        )

        assertEquals(4, state.investigationCount)
        assertNotNull(state.latestInvestigation)
        assertEquals(Verdict.SUPPORTED, state.latestInvestigation?.verdict)
        assertEquals(94, state.latestInvestigation?.confidencePercentage)
        assertEquals(2, state.emergencyQueuedCount)
        assertEquals(1, state.emergencyRelayingCount)
        assertTrue(state.hasCriticalEmergency)
    }

    @Test
    fun behaviorModel_honestLowDataState() {
        val lowDataModel = UserBehaviorModel(
            totalTasksCreated = 1,
            totalTasksCompleted = 1,
            hasSufficientData = false
        )

        val state = DashboardUiState(behaviorModel = lowDataModel)
        assertNotNull(state.behaviorModel)
        assertFalse(state.behaviorModel?.hasSufficientData ?: true)
        assertNull(state.behaviorModel?.completionRate)
        assertNull(state.behaviorModel?.preferredTaskSize)
        assertNull(state.behaviorModel?.peakProductivityTimeOfDay)
    }

    @Test
    fun behaviorModel_calibratedCognitiveProfile() {
        val calibratedModel = UserBehaviorModel(
            totalTasksCreated = 5,
            totalTasksCompleted = 4,
            totalTasksPostponed = 1,
            completionRate = 0.80,
            postponementRate = 0.20,
            averageCompletedDurationMinutes = 25.0,
            preferredTaskSize = TaskSizePreference.STANDARD,
            peakProductivityTimeOfDay = TimeOfDayBucket.MORNING,
            preferredCategories = mapOf(TaskCategory.WORK to 3),
            hasSufficientData = true
        )

        val state = DashboardUiState(behaviorModel = calibratedModel)
        assertTrue(state.behaviorModel?.hasSufficientData ?: false)
        assertEquals(0.80, state.behaviorModel?.completionRate ?: 0.0, 0.01)
        assertEquals(TaskSizePreference.STANDARD, state.behaviorModel?.preferredTaskSize)
        assertEquals(TimeOfDayBucket.MORNING, state.behaviorModel?.peakProductivityTimeOfDay)
    }

    @Test
    fun recommendation_explainableFactorsStructure() {
        val factors = listOf(
            RecommendationFactor("Priority", "Urgent priority task", 0.40),
            RecommendationFactor("Overdue", "Task past target deadline", 0.30),
            RecommendationFactor("Effort Fit", "Matches preferred focus duration", 0.15)
        )

        val recommendation = Recommendation(
            id = "rec-1",
            type = RecommendationType.TASK_FOCUS,
            title = "Finalize Presentation",
            reason = "Urgent priority task • Task past target deadline",
            confidence = 0.85,
            factors = factors,
            targetTaskId = "task-101",
            createdAt = Instant.now()
        )

        val state = DashboardUiState(recommendations = listOf(recommendation))
        assertEquals(1, state.recommendations.size)
        val rec = state.recommendations.first()
        assertEquals(3, rec.factors.size)
        assertEquals("Priority", rec.factors[0].name)
        assertEquals(0.40, rec.factors[0].scoreContribution, 0.01)
        assertEquals(0.85, rec.confidence, 0.01)
    }
}
