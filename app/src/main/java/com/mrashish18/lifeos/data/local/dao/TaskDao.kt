package com.mrashish18.lifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mrashish18.lifeos.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for tasks table.
 */
@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY createdAtEpochMillis DESC")
    fun observeAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    fun observeTaskById(id: String): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE status = 'IN_PROGRESS' LIMIT 1")
    fun observeActiveTask(): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: String): TaskEntity?

    @Query("SELECT * FROM tasks ORDER BY createdAtEpochMillis DESC")
    suspend fun getAllTasks(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity): Int

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE status = :status")
    fun observeTaskCountByStatus(status: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks")
    fun observeTotalTaskCount(): Flow<Int>

    @Query("DELETE FROM tasks")
    suspend fun clearAll(): Int
}
