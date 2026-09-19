package com.mrashish18.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessagePriority
import com.mrashish18.lifeos.core.model.MessageStatus
import com.mrashish18.lifeos.core.model.MessageType
import com.mrashish18.lifeos.core.model.RelayHop
import com.mrashish18.lifeos.core.model.TransportType
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
        val history = parseRelayHistoryJson(relayHistoryJson)

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
        fun formatRelayHistoryJson(history: List<RelayHop>): String {
            if (history.isEmpty()) return "[]"
            return history.joinToString(separator = ",", prefix = "[", postfix = "]") { hop ->
                val escapedRelayer = hop.relayedBy.replace("\"", "\\\"")
                """{"hopNumber":${hop.hopNumber},"relayedBy":"$escapedRelayer","relayedAt":${hop.relayedAt.toEpochMilli()},"transportUsed":"${hop.transportUsed.name}"}"""
            }
        }

        fun parseRelayHistoryJson(json: String): List<RelayHop> {
            if (json.isBlank() || json.trim() == "[]") return emptyList()
            val list = mutableListOf<RelayHop>()
            val objectPattern = Regex("""\{[^}]*\}""")
            val hopNumberPattern = Regex(""""hopNumber"\s*:\s*(\d+)""")
            val relayedByPattern = Regex(""""relayedBy"\s*:\s*"([^"]*)"""")
            val relayedAtPattern = Regex(""""relayedAt"\s*:\s*(\d+)""")
            val transportUsedPattern = Regex(""""transportUsed"\s*:\s*"([^"]*)"""")

            for (match in objectPattern.findAll(json)) {
                val objStr = match.value
                val hopNumber = hopNumberPattern.find(objStr)?.groupValues?.get(1)?.toIntOrNull() ?: 0
                val relayedBy = relayedByPattern.find(objStr)?.groupValues?.get(1) ?: "UNKNOWN"
                val relayedAtEpoch = relayedAtPattern.find(objStr)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
                val transportName = transportUsedPattern.find(objStr)?.groupValues?.get(1) ?: "UNKNOWN"
                val transport = try { TransportType.valueOf(transportName) } catch (e: Exception) { TransportType.UNKNOWN }

                list.add(
                    RelayHop(
                        hopNumber = hopNumber,
                        relayedBy = relayedBy,
                        relayedAt = Instant.ofEpochMilli(relayedAtEpoch),
                        transportUsed = transport
                    )
                )
            }
            return list
        }

        fun fromDomain(msg: EmergencyMessage): EmergencyMessageEntity {
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
                relayHistoryJson = formatRelayHistoryJson(msg.relayHistory)
            )
        }
    }
}
