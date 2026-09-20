package com.mrashish18.lifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mrashish18.lifeos.data.local.entity.InvestigationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for persisting and querying RealityCheck investigations.
 */
@Dao
interface InvestigationDao {

    @Query("SELECT * FROM investigation_records ORDER BY timestampEpochMillis DESC")
    fun observeAll(): Flow<List<InvestigationEntity>>

    @Query("SELECT * FROM investigation_records ORDER BY timestampEpochMillis DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<InvestigationEntity>>

    @Query("SELECT * FROM investigation_records WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): InvestigationEntity?

    @Query("SELECT * FROM investigation_records ORDER BY timestampEpochMillis DESC")
    suspend fun getAll(): List<InvestigationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: InvestigationEntity): Long

    @Query("DELETE FROM investigation_records WHERE id = :id")
    suspend fun delete(id: String): Int

    @Query("SELECT COUNT(*) FROM investigation_records")
    fun observeTotalCount(): Flow<Int>

    @Query("DELETE FROM investigation_records")
    suspend fun clearAll(): Int
}
