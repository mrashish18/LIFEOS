package com.mrashish18.lifeos.core.model

import com.mrashish18.lifeos.data.repository.RoomBehaviorEventRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class UserBehaviorModelTest {

    private val testZone = ZoneId.of("UTC")

    @Test
    fun computeModel_insufficientData_returnsNullRates() {
        // Only 1 completed and 0 abandoned (total 1 < 3 minimum)
        val events = listOf(
            BehaviorEvent(
                id = "1",
                type = BehaviorEventType.TASK_CREATED,
                timestamp = Instant.parse("2026-09-14T09:00:00Z")
            ),
            BehaviorEvent(
                id = "2",
                type = BehaviorEventType.TASK_COMPLETED,
                timestamp = Instant.parse("2026-09-14T09:30:00Z")
            )
        )

        val model = RoomBehaviorEventRepository.computeModel(events, testZone)

        assertEquals(1, model.totalTasksCreated)
        assertEquals(1, model.totalTasksCompleted)
        assertFalse(model.hasSufficientData)
        assertNull("Completion rate must be null when insufficient data", model.completionRate)
        assertNull("Postponement rate must be null when insufficient data", model.postponementRate)
        assertNull("Abandonment rate must be null when insufficient data", model.abandonmentRate)
    }

    @Test
    fun computeModel_sufficientData_calculatesRatesAndCategoriesAccurately() {
        val events = listOf(
            // 5 tasks created
            BehaviorEvent(id = "c1", type = BehaviorEventType.TASK_CREATED),
            BehaviorEvent(id = "c2", type = BehaviorEventType.TASK_CREATED),
            BehaviorEvent(id = "c3", type = BehaviorEventType.TASK_CREATED),
            BehaviorEvent(id = "c4", type = BehaviorEventType.TASK_CREATED),
            BehaviorEvent(id = "c5", type = BehaviorEventType.TASK_CREATED),

            // 1 postponed
            BehaviorEvent(id = "p1", type = BehaviorEventType.TASK_POSTPONED),

            // 3 completed (2 in WORK at 10:00 UTC = MORNING, 1 in HEALTH at 14:00 UTC = AFTERNOON)
            BehaviorEvent(
                id = "comp1",
                type = BehaviorEventType.TASK_COMPLETED,
                timestamp = Instant.parse("2026-09-14T10:00:00Z"),
                metadata = mapOf("category" to "WORK", "durationMinutes" to "30")
            ),
            BehaviorEvent(
                id = "comp2",
                type = BehaviorEventType.TASK_COMPLETED,
                timestamp = Instant.parse("2026-09-14T11:00:00Z"),
                metadata = mapOf("category" to "WORK", "durationMinutes" to "45")
            ),
            BehaviorEvent(
                id = "comp3",
                type = BehaviorEventType.TASK_COMPLETED,
                timestamp = Instant.parse("2026-09-14T14:00:00Z"),
                metadata = mapOf("category" to "HEALTH", "durationMinutes" to "15")
            ),

            // 1 abandoned
            BehaviorEvent(id = "ab1", type = BehaviorEventType.TASK_ABANDONED)
        )

        val model = RoomBehaviorEventRepository.computeModel(events, testZone)

        assertTrue(model.hasSufficientData)
        assertEquals(5, model.totalTasksCreated)
        assertEquals(3, model.totalTasksCompleted)
        assertEquals(1, model.totalTasksPostponed)
        assertEquals(1, model.totalTasksAbandoned)

        // 3 completed / (3 completed + 1 abandoned) = 0.75
        assertEquals(0.75, model.completionRate!!, 0.001)

        // 1 abandoned / (3 completed + 1 abandoned) = 0.25
        assertEquals(0.25, model.abandonmentRate!!, 0.001)

        // 1 postponed / 5 created = 0.20
        assertEquals(0.20, model.postponementRate!!, 0.001)

        // Average duration: (30 + 45 + 15) / 3 = 30.0
        assertEquals(30.0, model.averageCompletedDurationMinutes!!, 0.001)

        // Category breakdown: WORK=2, HEALTH=1
        assertEquals(2, model.preferredCategories[TaskCategory.WORK])
        assertEquals(1, model.preferredCategories[TaskCategory.HEALTH])

        // Time of day buckets: MORNING=2, AFTERNOON=1
        assertEquals(2, model.completionsByTimeOfDay[TimeOfDayBucket.MORNING])
        assertEquals(1, model.completionsByTimeOfDay[TimeOfDayBucket.AFTERNOON])
    }
}
