package com.mrashish18.lifeos.data.repository

import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.data.local.dao.EmergencyMessageDao
import com.mrashish18.lifeos.data.local.entity.EmergencyMessageEntity
import com.mrashish18.lifeos.domain.repository.EmergencyMessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomEmergencyMessageRepository(
    private val emergencyMessageDao: EmergencyMessageDao
) : EmergencyMessageRepository {

    override fun observeAllMessages(): Flow<List<EmergencyMessage>> {
        return emergencyMessageDao.observeAllMessages().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeQueue(): Flow<List<EmergencyMessage>> {
        return emergencyMessageDao.observeQueue().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeMessagesByStatus(status: com.mrashish18.lifeos.core.model.MessageStatus): Flow<List<EmergencyMessage>> {
        return emergencyMessageDao.observeMessagesByStatus(status.name).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getAllMessages(): List<EmergencyMessage> {
        return emergencyMessageDao.getAllMessages().map { it.toDomain() }
    }

    override suspend fun getQueuedMessages(): List<EmergencyMessage> {
        return emergencyMessageDao.getQueuedMessages().map { it.toDomain() }
    }

    override suspend fun getQueuedMessagesBounded(limit: Int): List<EmergencyMessage> {
        return emergencyMessageDao.getQueuedMessagesBounded(limit).map { it.toDomain() }
    }

    override suspend fun getMessagesByStatus(status: com.mrashish18.lifeos.core.model.MessageStatus): List<EmergencyMessage> {
        return emergencyMessageDao.getMessagesByStatus(status.name).map { it.toDomain() }
    }

    override suspend fun getMessageById(id: String): EmergencyMessage? {
        return emergencyMessageDao.getMessageById(id)?.toDomain()
    }

    override suspend fun getMessageByFingerprint(sha256: String): EmergencyMessage? {
        return emergencyMessageDao.getMessageByFingerprint(sha256)?.toDomain()
    }

    override suspend fun insertMessage(message: EmergencyMessage) {
        emergencyMessageDao.insertMessage(EmergencyMessageEntity.fromDomain(message))
    }

    override suspend fun updateMessage(message: EmergencyMessage) {
        emergencyMessageDao.updateMessage(EmergencyMessageEntity.fromDomain(message))
    }

    override suspend fun updateMessages(messages: List<EmergencyMessage>) {
        if (messages.isEmpty()) return
        emergencyMessageDao.updateAll(messages.map { EmergencyMessageEntity.fromDomain(it) })
    }

    override suspend fun deleteMessage(id: String) {
        emergencyMessageDao.deleteMessage(id)
    }

    override suspend fun hasMessage(messageId: String): Boolean {
        return emergencyMessageDao.getMessageById(messageId) != null
    }

    override suspend fun clearAll() {
        emergencyMessageDao.clearAll()
    }
}
