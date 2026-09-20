package com.mrashish18.lifeos.domain.repository

import com.mrashish18.lifeos.core.model.LifeOsNotification
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for observing and managing in-app notifications.
 */
interface NotificationRepository {

    fun observeNotifications(): Flow<List<LifeOsNotification>>

    fun observeUnreadCount(): Flow<Int>

    suspend fun getAllNotifications(): List<LifeOsNotification>

    suspend fun getUnreadCount(): Int

    suspend fun addNotification(notification: LifeOsNotification)

    suspend fun addNotifications(notifications: List<LifeOsNotification>)

    suspend fun markAsRead(id: String)

    suspend fun markAllAsRead()

    suspend fun deleteById(id: String)

    suspend fun getNotificationById(id: String): LifeOsNotification?
}
