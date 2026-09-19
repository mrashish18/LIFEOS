package com.mrashish18.lifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mrashish18.lifeos.data.local.entity.EmergencyMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyMessageDao {

    @Query("SELECT * FROM emergency_messages ORDER BY createdAtEpochMillis DESC")
    fun observeAllMessages(): Flow<List<EmergencyMessageEntity>>

    @Query("SELECT * FROM emergency_messages WHERE status IN ('QUEUED', 'RELAYING') ORDER BY createdAtEpochMillis ASC")
    fun observeQueue(): Flow<List<EmergencyMessageEntity>>

    @Query("SELECT * FROM emergency_messages ORDER BY createdAtEpochMillis DESC")
    suspend fun getAllMessages(): List<EmergencyMessageEntity>

    @Query("SELECT * FROM emergency_messages WHERE status IN ('QUEUED', 'RELAYING') ORDER BY createdAtEpochMillis ASC")
    suspend fun getQueuedMessages(): List<EmergencyMessageEntity>

    @Query("SELECT * FROM emergency_messages WHERE messageId = :id LIMIT 1")
    suspend fun getMessageById(id: String): EmergencyMessageEntity?

    @Query("SELECT * FROM emergency_messages WHERE fingerprintSha256 = :fingerprint LIMIT 1")
    suspend fun getMessageByFingerprint(fingerprint: String): EmergencyMessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: EmergencyMessageEntity): Long

    @Update
    suspend fun updateMessage(message: EmergencyMessageEntity): Int

    @Query("DELETE FROM emergency_messages WHERE messageId = :id")
    suspend fun deleteMessage(id: String): Int

    @Query("DELETE FROM emergency_messages")
    suspend fun clearAll(): Int
}
