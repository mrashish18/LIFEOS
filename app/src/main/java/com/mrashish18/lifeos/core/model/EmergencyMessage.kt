package com.mrashish18.lifeos.core.model

import java.time.Instant

enum class MessageType {
    EMERGENCY,
    MEDICAL,
    RESCUE_REQUEST,
    STATUS_UPDATE,
    HEARTBEAT
}

enum class MessagePriority {
    CRITICAL,
    HIGH,
    NORMAL
}

enum class MessageStatus {
    DRAFT,
    QUEUED,
    RELAYING,
    SENT,
    DELIVERED,
    FAILED,
    EXPIRED,
    DUPLICATE
}

enum class TransportType {
    LOCAL_ONLY,
    BLUETOOTH_LE,
    WIFI_DIRECT,
    NETWORK,
    UNKNOWN
}

data class RelayHop(
    val hopNumber: Int,
    val relayedBy: String,
    val relayedAt: Instant,
    val transportUsed: TransportType
)

data class EmergencyMessage(
    val messageId: String,
    val senderId: String,
    val recipientId: String? = null,
    val type: MessageType = MessageType.EMERGENCY,
    val priority: MessagePriority = MessagePriority.NORMAL,
    val status: MessageStatus = MessageStatus.DRAFT,
    val payload: String,
    val createdAt: Instant = Instant.now(),
    val expiresAt: Instant,
    val hopCount: Int = 0,
    val maxHops: Int = 5,
    val transportType: TransportType = TransportType.LOCAL_ONLY,
    val fingerprintSha256: String,
    val relayHistory: List<RelayHop> = emptyList()
) {
    companion object {
        fun defaultSeedMessages(): List<EmergencyMessage> = listOf(
            EmergencyMessage(
                messageId = "msg_seed_01",
                senderId = "NODE-7F4A",
                type = MessageType.MEDICAL,
                priority = MessagePriority.CRITICAL,
                status = MessageStatus.QUEUED,
                payload = "Need medical assistance",
                createdAt = Instant.now().minusSeconds(1800),
                expiresAt = Instant.now().plusSeconds(18000),
                hopCount = 0,
                maxHops = 5,
                transportType = TransportType.LOCAL_ONLY,
                fingerprintSha256 = "c8f2a1b9e4d3c2b1"
            ),
            EmergencyMessage(
                messageId = "msg_seed_02",
                senderId = "NODE-7F4A",
                type = MessageType.RESCUE_REQUEST,
                priority = MessagePriority.HIGH,
                status = MessageStatus.QUEUED,
                payload = "Require rescue supplies",
                createdAt = Instant.now().minusSeconds(7200),
                expiresAt = Instant.now().plusSeconds(28800),
                hopCount = 0,
                maxHops = 5,
                transportType = TransportType.LOCAL_ONLY,
                fingerprintSha256 = "d4e5f6a7b8c9d0e1"
            ),
            EmergencyMessage(
                messageId = "msg_seed_03",
                senderId = "NODE-7F4A",
                type = MessageType.STATUS_UPDATE,
                priority = MessagePriority.NORMAL,
                status = MessageStatus.QUEUED,
                payload = "Status update from location",
                createdAt = Instant.now().minusSeconds(10800),
                expiresAt = Instant.now().plusSeconds(43200),
                hopCount = 0,
                maxHops = 5,
                transportType = TransportType.LOCAL_ONLY,
                fingerprintSha256 = "a1b2c3d4e5f6a7b8"
            )
        )
    }
}
