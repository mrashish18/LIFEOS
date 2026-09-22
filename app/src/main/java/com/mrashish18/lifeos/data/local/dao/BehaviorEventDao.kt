package com.mrashish18.lifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mrashish18.lifeos.data.local.entity.BehaviorEventEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for behavior_events table.
 */
@Dao
interface BehaviorEventDao {

    @Query("SELECT * FROM behavior_events ORDER BY timestampEpochMillis DESC LIMIT :limit")
    fun observeRecentEvents(limit: Int = 100): Flow<List<BehaviorEventEntity>>

    @Query("SELECT * FROM behavior_events ORDER BY timestampEpochMillis ASC")
    fun observeAllEvents(): Flow<List<BehaviorEventEntity>>

    @Query("SELECT * FROM behavior_events ORDER BY timestampEpochMillis ASC")
    suspend fun getAllEvents(): List<BehaviorEventEntity>

    @Query("SELECT * FROM behavior_events ORDER BY timestampEpochMillis DESC LIMIT :limit")
    suspend fun getRecentEvents(limit: Int = 500): List<BehaviorEventEntity>

    @Query("SELECT * FROM behavior_events WHERE type = :type ORDER BY timestampEpochMillis DESC")
    suspend fun getEventsByType(type: String): List<BehaviorEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: BehaviorEventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<BehaviorEventEntity>): List<Long>

    @Query("SELECT COUNT(*) FROM behavior_events WHERE type = :type")
    suspend fun countEventsByType(type: String): Int

    @Query("SELECT COUNT(*) FROM behavior_events")
    fun observeTotalCount(): Flow<Int>

    @Query("DELETE FROM behavior_events")
    suspend fun clearAll(): Int
}
