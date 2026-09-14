package com.mrashish18.lifeos.core.context

import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.core.model.UserAvailability
import com.mrashish18.lifeos.core.model.WorkloadLevel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId

class ContextEngineTest {

    @Test
    fun captureSnapshot_returnsAggregatedContextAccurately() {
        // 2026-09-14 is a Monday
        val fixedInstant = Instant.parse("2026-09-14T10:00:00Z")
        val fixedZone = ZoneId.of("UTC")
        val mockNetworkProvider = object : ContextProvider<NetworkState> {
            override fun provide(): NetworkState = NetworkState.CONNECTED_WIFI
        }
        val activeTask = Task(
            id = "t1",
            title = "Code Review",
            priority = TaskPriority.HIGH,
            status = TaskStatus.IN_PROGRESS
        )

        val engine = DefaultContextEngine(
            networkContextProvider = mockNetworkProvider,
            timeProvider = { fixedInstant },
            zoneId = fixedZone,
            activeTaskProvider = { activeTask },
            workloadProvider = { WorkloadLevel.HIGH },
            availabilityProvider = { UserAvailability.FOCUS }
        )

        val snapshot = engine.captureSnapshot()

        assertEquals(fixedInstant, snapshot.currentTime)
        assertEquals(DayOfWeek.MONDAY, snapshot.dayOfWeek)
        assertEquals(NetworkState.CONNECTED_WIFI, snapshot.networkState)
        assertNotNull(snapshot.activeTask)
        assertEquals("Code Review", snapshot.activeTask?.title)
        assertEquals(WorkloadLevel.HIGH, snapshot.workloadLevel)
        assertEquals(UserAvailability.FOCUS, snapshot.userAvailability)
    }

    @Test
    fun observeSnapshot_emitsLatestSnapshot() = runBlocking {
        val fixedInstant = Instant.parse("2026-09-14T12:00:00Z")
        val engine = DefaultContextEngine(
            timeProvider = { fixedInstant },
            zoneId = ZoneId.of("UTC")
        )

        val emitted = engine.observeSnapshot().first()
        assertEquals(fixedInstant, emitted.currentTime)
    }
}
