package com.mrashish18.lifeos.core.model

import com.mrashish18.lifeos.ui.navigation.LifeOsDestination

/**
 * Functional categories for LIFEOS notifications.
 */
enum class NotificationCategory {
    PERSONAL,
    TRUTH,
    MESH,
    LEARNING,
    EMERGENCY
}

/**
 * Domain model representing a genuine in-app notification in LIFEOS.
 */
data class LifeOsNotification(
    val id: String,
    val category: NotificationCategory,
    val title: String,
    val message: String,
    val timestampEpochMillis: Long,
    val isRead: Boolean = false,
    val destination: LifeOsDestination? = null,
    val entityId: String? = null
)
