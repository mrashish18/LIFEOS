package com.mrashish18.lifeos.domain.repository

import com.mrashish18.lifeos.core.model.EmergencyMessage
import kotlinx.coroutines.flow.Flow

interface EmergencyMessageRepository {
    fun observeAllMessages(): Flow<List<EmergencyMessage>>
    fun observeQueue(): Flow<List<EmergencyMessage>>
    suspend fun getAllMessages(): List<EmergencyMessage>
    suspend fun getQueuedMessages(): List<EmergencyMessage>
    suspend fun getMessageById(id: String): EmergencyMessage?
    suspend fun getMessageByFingerprint(sha256: String): EmergencyMessage?
    suspend fun insertMessage(message: EmergencyMessage)
    suspend fun updateMessage(message: EmergencyMessage)
    suspend fun deleteMessage(id: String)
    suspend fun hasMessage(messageId: String): Boolean
    suspend fun clearAll()
}
