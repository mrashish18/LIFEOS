package com.mrashish18.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessagePriority
import com.mrashish18.lifeos.core.model.MessageStatus
import com.mrashish18.lifeos.core.model.MessageType
import com.mrashish18.lifeos.core.model.RelayHop
import com.mrashish18.lifeos.core.model.TransportType
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

@Entity(tableName = "emergency_messages")
data class EmergencyMessageEntity(
    @PrimaryKey
    val messageId: String,
    val senderId: String,
    val recipientId: String?,
    val type: String,
    val priority: String,
    val status: String,
    val payload: String,
    val createdAtEpochMillis: Long,
    val expiresAtEpochMillis: Long,
    val hopCount: Int,
    val maxHops: Int,
    val transportType: String,
    val fingerprintSha256: String,
    val relayHistoryJson: String
) {
    fun toDomain(): EmergencyMessage {
        val history = mutableListOf<RelayHop>()
        try {
            val jsonArray = JSONArray(relayHistoryJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                history.add(
                    RelayHop(
                        hopNumber = obj.getInt("hopNumber"),
                        relayedBy = obj.getString("relayedBy"),
                        relayedAt = Instant.ofEpochMilli(obj.getLong("relayedAt")),
                        transportUsed = try {
                            TransportType.valueOf(obj.getString("transportUsed"))
                        } catch (e: Exception) {
                            TransportType.UNKNOWN
                        }
                    )
                )
            }
        } catch (e: Exception) {
            // ignore malformed history
        }

        return EmergencyMessage(
            messageId = messageId,
            senderId = senderId,
            recipientId = recipientId,
            type = try { MessageType.valueOf(type) } catch (e: Exception) { MessageType.EMERGENCY },
            priority = try { MessagePriority.valueOf(priority) } catch (e: Exception) { MessagePriority.NORMAL },
            status = try { MessageStatus.valueOf(status) } catch (e: Exception) { MessageStatus.DRAFT },
            payload = payload,
            createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
            expiresAt = Instant.ofEpochMilli(expiresAtEpochMillis),
            hopCount = hopCount,
            maxHops = maxHops,
            transportType = try { TransportType.valueOf(transportType) } catch (e: Exception) { TransportType.LOCAL_ONLY },
            fingerprintSha256 = fingerprintSha256,
            relayHistory = history
        )
    }

    companion object {
        fun fromDomain(msg: EmergencyMessage): EmergencyMessageEntity {
            val jsonArray = JSONArray()
            msg.relayHistory.forEach { hop ->
                val obj = JSONObject().apply {
                    put("hopNumber", hop.hopNumber)
                    put("relayedBy", hop.relayedBy)
                    put("relayedAt", hop.relayedAt.toEpochMilli())
                    put("transportUsed", hop.transportUsed.name)
                }
                jsonArray.put(obj)
            }

            return EmergencyMessageEntity(
                messageId = msg.messageId,
                senderId = msg.senderId,
                recipientId = msg.recipientId,
                type = msg.type.name,
                priority = msg.priority.name,
                status = msg.status.name,
                payload = msg.payload,
                createdAtEpochMillis = msg.createdAt.toEpochMilli(),
                expiresAtEpochMillis = msg.expiresAt.toEpochMilli(),
                hopCount = msg.hopCount,
                maxHops = msg.maxHops,
                transportType = msg.transportType.name,
                fingerprintSha256 = msg.fingerprintSha256,
                relayHistoryJson = jsonArray.toString()
            )
        }
    }
}
