package com.mrashish18.lifeos.core.context

import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.UserAvailability
import com.mrashish18.lifeos.core.model.WorkloadLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.ZoneId

/**
 * Default implementation of [ContextEngine] that aggregates local context safely:
 * - Current timestamp and DayOfWeek
 * - Basic device network status
 * - Active task and cognitive workload level
 * - User availability
 */
class DefaultContextEngine(
    private val networkContextProvider: ContextProvider<NetworkState> = object : ContextProvider<NetworkState> {
        override fun provide(): NetworkState = NetworkState.UNKNOWN
    },
    private val timeProvider: () -> Instant = { Instant.now() },
    private val zoneId: ZoneId = ZoneId.systemDefault(),
    private val activeTaskProvider: () -> Task? = { null },
    private val workloadProvider: () -> WorkloadLevel = { WorkloadLevel.LOW },
    private val availabilityProvider: () -> UserAvailability = { UserAvailability.AVAILABLE }
) : ContextEngine {

    override fun captureSnapshot(): ContextSnapshot {
        val now = timeProvider()
        val dayOfWeek = now.atZone(zoneId).dayOfWeek
        val network = networkContextProvider.provide()
        val activeTask = activeTaskProvider()
        val workload = workloadProvider()
        val availability = availabilityProvider()

        return ContextSnapshot(
            currentTime = now,
            dayOfWeek = dayOfWeek,
            networkState = network,
            activeTask = activeTask,
            workloadLevel = workload,
            userAvailability = availability
        )
    }

    override fun observeSnapshot(): Flow<ContextSnapshot> = flow {
        emit(captureSnapshot())
    }
}
