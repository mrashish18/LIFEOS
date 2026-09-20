package com.mrashish18.lifeos.data.repository

import com.mrashish18.lifeos.core.common.DefaultDispatcherProvider
import com.mrashish18.lifeos.core.common.DispatcherProvider
import com.mrashish18.lifeos.core.model.LifeOsNotification
import com.mrashish18.lifeos.data.local.dao.NotificationDao
import com.mrashish18.lifeos.data.local.entity.NotificationEntity
import com.mrashish18.lifeos.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Room-backed implementation of [NotificationRepository].
 */
class RoomNotificationRepository(
    private val notificationDao: NotificationDao,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) : NotificationRepository {

    override fun observeNotifications(): Flow<List<LifeOsNotification>> {
        return notificationDao.observeNotifications()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(dispatcherProvider.io)
    }

    override fun observeUnreadCount(): Flow<Int> {
        return notificationDao.observeUnreadCount()
            .flowOn(dispatcherProvider.io)
    }

    override suspend fun getAllNotifications(): List<LifeOsNotification> = withContext(dispatcherProvider.io) {
        notificationDao.getAllNotifications().map { it.toDomain() }
    }

    override suspend fun getUnreadCount(): Int = withContext(dispatcherProvider.io) {
        notificationDao.getUnreadCount()
    }

    override suspend fun addNotification(notification: LifeOsNotification) = withContext(dispatcherProvider.io) {
        notificationDao.insert(NotificationEntity.fromDomain(notification))
        Unit
    }

    override suspend fun addNotifications(notifications: List<LifeOsNotification>) = withContext(dispatcherProvider.io) {
        notificationDao.insertAll(notifications.map { NotificationEntity.fromDomain(it) })
        Unit
    }

    override suspend fun markAsRead(id: String) = withContext(dispatcherProvider.io) {
        notificationDao.markAsRead(id)
        Unit
    }

    override suspend fun markAllAsRead() = withContext(dispatcherProvider.io) {
        notificationDao.markAllAsRead()
        Unit
    }

    override suspend fun deleteById(id: String) = withContext(dispatcherProvider.io) {
        notificationDao.deleteById(id)
        Unit
    }

    override suspend fun getNotificationById(id: String): LifeOsNotification? = withContext(dispatcherProvider.io) {
        notificationDao.getNotificationById(id)?.toDomain()
    }
}
