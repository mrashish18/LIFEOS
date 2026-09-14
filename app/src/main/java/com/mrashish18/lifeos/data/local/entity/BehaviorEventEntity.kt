package com.mrashish18.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrashish18.lifeos.core.model.BehaviorEvent
import com.mrashish18.lifeos.core.model.BehaviorEventType
import org.json.JSONObject
import java.time.Instant

/**
 * Room database entity representing a recorded user or system behavioral event.
 */
@Entity(tableName = "behavior_events")
data class BehaviorEventEntity(
    @PrimaryKey
    val id: String,
    val type: String,
    val timestampEpochMillis: Long,
    val metadataJson: String
) {
    fun toDomain(): BehaviorEvent {
        val metadataMap = mutableMapOf<String, String>()
        if (metadataJson.isNotBlank()) {
            try {
                val jsonObject = JSONObject(metadataJson)
                val keys = jsonObject.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    metadataMap[key] = jsonObject.optString(key, "")
                }
            } catch (e: Exception) {
                // Fallback to empty map on parsing failure
            }
        }

        val eventType = try {
            BehaviorEventType.valueOf(type)
        } catch (e: Exception) {
            BehaviorEventType.TASK_CREATED
        }

        return BehaviorEvent(
            id = id,
            type = eventType,
            timestamp = Instant.ofEpochMilli(timestampEpochMillis),
            metadata = metadataMap
        )
    }

    companion object {
        fun fromDomain(event: BehaviorEvent): BehaviorEventEntity {
            val jsonObject = JSONObject()
            event.metadata.forEach { (k, v) -> jsonObject.put(k, v) }

            return BehaviorEventEntity(
                id = event.id,
                type = event.type.name,
                timestampEpochMillis = event.timestamp.toEpochMilli(),
                metadataJson = jsonObject.toString()
            )
        }
    }
}
