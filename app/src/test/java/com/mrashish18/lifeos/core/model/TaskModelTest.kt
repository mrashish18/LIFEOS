package com.mrashish18.lifeos.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class TaskModelTest {

    @Test
    fun task_creation_usesDefaultValuesCorrectly() {
        val task = Task(
            id = "test-1",
            title = "Test Task"
        )

        assertEquals("test-1", task.id)
        assertEquals("Test Task", task.title)
        assertEquals("", task.description)
        assertEquals(TaskPriority.MEDIUM, task.priority)
        assertEquals(TaskStatus.PENDING, task.status)
        assertEquals(TaskCategory.GENERAL, task.category)
        assertNotNull(task.createdAt)
        assertNotNull(task.updatedAt)
    }

    @Test
    fun task_statusTransition_updatesCorrectly() {
        val original = Task(
            id = "test-2",
            title = "In Progress Task",
            status = TaskStatus.PENDING
        )

        val updated = original.copy(
            status = TaskStatus.IN_PROGRESS,
            updatedAt = Instant.now()
        )

        assertEquals(TaskStatus.IN_PROGRESS, updated.status)
        assertTrue(updated.updatedAt >= original.updatedAt)
    }

    @Test
    fun recommendation_validConfidence_succeeds() {
        val rec = Recommendation(
            id = "rec-1",
            type = RecommendationType.TASK_FOCUS,
            title = "Focus",
            reason = "Reason",
            confidence = 0.85
        )
        assertEquals(0.85, rec.confidence, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun recommendation_invalidConfidence_throwsException() {
        Recommendation(
            id = "rec-invalid",
            type = RecommendationType.SYSTEM_NOTICE,
            title = "Invalid",
            reason = "Invalid score",
            confidence = 1.5
        )
    }
}
