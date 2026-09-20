package com.mrashish18.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mrashish18.lifeos.core.model.LifeOsNotification
import com.mrashish18.lifeos.core.model.NotificationCategory
import com.mrashish18.lifeos.ui.navigation.LifeOsDestination

/**
 * Room entity representing an in-app notification in LIFEOS.
 */
@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["timestampEpochMillis"]),
        Index(value = ["isRead"])
    ]
)
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val category: String,
    val title: String,
    val message: String,
    val timestampEpochMillis: Long,
    val isRead: Boolean,
    val destination: String?,
    val entityId: String?
) {
    fun toDomain(): LifeOsNotification {
        val cat = try {
            NotificationCategory.valueOf(category)
        } catch (e: Exception) {
            NotificationCategory.PERSONAL
        }
        val dest = destination?.let {
            try {
                LifeOsDestination.valueOf(it)
            } catch (e: Exception) {
                null
            }
        }
        return LifeOsNotification(
            id = id,
            category = cat,
            title = title,
            message = message,
            timestampEpochMillis = timestampEpochMillis,
            isRead = isRead,
            destination = dest,
            entityId = entityId
        )
    }

    companion object {
        fun fromDomain(domain: LifeOsNotification): NotificationEntity {
            return NotificationEntity(
                id = domain.id,
                category = domain.category.name,
                title = domain.title,
                message = domain.message,
                timestampEpochMillis = domain.timestampEpochMillis,
                isRead = domain.isRead,
                destination = domain.destination?.name,
                entityId = domain.entityId
            )
        }
    }
}
