package com.mrashish18.lifeos.data.repository

import com.mrashish18.lifeos.core.model.BehaviorEvent
import com.mrashish18.lifeos.core.model.BehaviorEventType
import com.mrashish18.lifeos.core.model.TaskCategory
import com.mrashish18.lifeos.core.model.TimeOfDayBucket
import com.mrashish18.lifeos.core.model.UserBehaviorModel
import com.mrashish18.lifeos.data.local.dao.BehaviorEventDao
import com.mrashish18.lifeos.data.local.entity.BehaviorEventEntity
import com.mrashish18.lifeos.domain.repository.BehaviorEventRepository
import com.mrashish18.lifeos.domain.repository.UserBehaviorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZoneId

/**
 * Room-backed implementation of [BehaviorEventRepository] and [UserBehaviorRepository].
 * Derives [UserBehaviorModel] deterministically from observed event history.
 */
class RoomBehaviorEventRepository(
    private val behaviorEventDao: BehaviorEventDao,
    private val zoneId: ZoneId = ZoneId.systemDefault()
) : BehaviorEventRepository, UserBehaviorRepository {

    override suspend fun recordEvent(event: BehaviorEvent) {
        behaviorEventDao.insert(BehaviorEventEntity.fromDomain(event))
    }

    override fun observeRecentEvents(limit: Int): Flow<List<BehaviorEvent>> {
        return behaviorEventDao.observeRecentEvents(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAllEvents(): List<BehaviorEvent> {
        return behaviorEventDao.getAllEvents().map { it.toDomain() }
    }

    override fun observeUserBehaviorModel(): Flow<UserBehaviorModel> {
        return behaviorEventDao.observeAllEvents().map { entities ->
            computeModel(entities.map { it.toDomain() }, zoneId)
        }
    }

    override suspend fun getUserBehaviorModel(): UserBehaviorModel {
        return computeModel(getAllEvents(), zoneId)
    }

    companion object {
        const val MINIMUM_OBSERVATIONS_FOR_CONFIDENCE = 3

        fun computeModel(events: List<BehaviorEvent>, zoneId: ZoneId = ZoneId.systemDefault()): UserBehaviorModel {
            val createdCount = events.count { it.type == BehaviorEventType.TASK_CREATED }
            val completedEvents = events.filter { it.type == BehaviorEventType.TASK_COMPLETED }
            val completedCount = completedEvents.size
            val postponedCount = events.count { it.type == BehaviorEventType.TASK_POSTPONED }
            val abandonedCount = events.count { it.type == BehaviorEventType.TASK_ABANDONED }

            val terminalCount = completedCount + abandonedCount
            val hasSufficientData = terminalCount >= MINIMUM_OBSERVATIONS_FOR_CONFIDENCE

            val completionRate: Double? = if (hasSufficientData && terminalCount > 0) {
                completedCount.toDouble() / terminalCount.toDouble()
            } else null

            val abandonmentRate: Double? = if (hasSufficientData && terminalCount > 0) {
                abandonedCount.toDouble() / terminalCount.toDouble()
            } else null

            val postponementRate: Double? = if (hasSufficientData && createdCount > 0) {
                postponedCount.toDouble() / createdCount.toDouble()
            } else null

            val durations = completedEvents.mapNotNull { it.metadata["durationMinutes"]?.toDoubleOrNull() }
            val avgDuration = if (durations.isNotEmpty()) durations.average() else null

            val preferredCategories = mutableMapOf<TaskCategory, Int>()
            completedEvents.forEach { event ->
                val catName = event.metadata["category"]
                if (catName != null) {
                    val category = try { TaskCategory.valueOf(catName) } catch (e: Exception) { null }
                    if (category != null) {
                        preferredCategories[category] = (preferredCategories[category] ?: 0) + 1
                    }
                }
            }

            val completionsByTime = mutableMapOf<TimeOfDayBucket, Int>()
            completedEvents.forEach { event ->
                val hour = event.timestamp.atZone(zoneId).hour
                val bucket = when (hour) {
                    in 5..11 -> TimeOfDayBucket.MORNING
                    in 12..16 -> TimeOfDayBucket.AFTERNOON
                    in 17..21 -> TimeOfDayBucket.EVENING
                    else -> TimeOfDayBucket.NIGHT
                }
                completionsByTime[bucket] = (completionsByTime[bucket] ?: 0) + 1
            }

            return UserBehaviorModel(
                totalTasksCreated = createdCount,
                totalTasksCompleted = completedCount,
                totalTasksPostponed = postponedCount,
                totalTasksAbandoned = abandonedCount,
                completionRate = completionRate,
                postponementRate = postponementRate,
                abandonmentRate = abandonmentRate,
                averageCompletedDurationMinutes = avgDuration,
                preferredCategories = preferredCategories,
                completionsByTimeOfDay = completionsByTime,
                hasSufficientData = hasSufficientData
            )
        }
    }
}
