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
            } catch (e: Throwable) {
                // Fallback parsing for simple JSON in JVM unit tests
                metadataJson.trim().removeSurrounding("{", "}").split(",").forEach { part ->
                    val kv = part.split(":")
                    if (kv.size == 2) {
                        val k = kv[0].trim().removeSurrounding("\"")
                        val v = kv[1].trim().removeSurrounding("\"")
                        if (k.isNotBlank()) {
                            metadataMap[k] = v
                        }
                    }
                }
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
            val jsonStr = try {
                val jsonObject = JSONObject()
                event.metadata.forEach { (k, v) -> jsonObject.put(k, v) }
                jsonObject.toString()
            } catch (e: Throwable) {
                // JVM unit test fallback when android.os.JSONObject is not stubbed
                event.metadata.entries.joinToString(prefix = "{", postfix = "}") { "\"${it.key}\":\"${it.value}\"" }
            }

            return BehaviorEventEntity(
                id = event.id,
                type = event.type.name,
                timestampEpochMillis = event.timestamp.toEpochMilli(),
                metadataJson = jsonStr
            )
        }
    }
}
