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
        private fun calculateSeedFingerprint(
            senderId: String,
            payload: String,
            createdAt: Instant,
            expiresAt: Instant,
            maxHops: Int
        ): String {
            val ttlMillis = expiresAt.toEpochMilli() - createdAt.toEpochMilli()
            val raw = "$senderId|$payload|${createdAt.toEpochMilli()}|$ttlMillis|$maxHops"
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val bytes = digest.digest(raw.toByteArray(Charsets.UTF_8))
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun defaultSeedMessages(): List<EmergencyMessage> {
            val now = Instant.now()
            val created1 = now.minusSeconds(120) // 2 min ago
            val expires1 = now.plusSeconds(86400)
            val created2 = now.minusSeconds(900) // 15 min ago
            val expires2 = now.plusSeconds(86400)
            val created3 = now.minusSeconds(3600) // 1 hr ago
            val expires3 = now.plusSeconds(86400)

            val payload1 = "Need medical supplies for our community. Water and basic medications would help. Staying safe together. - LifeOs User"
            val payload2 = "All safe here. Community is strong. Thank you."
            val payload3 = "Status check-in: Base camp logistics intact."

            return listOf(
                EmergencyMessage(
                    messageId = "msg_seed_01",
                    senderId = "NODE-7F4A",
                    type = MessageType.MEDICAL,
                    priority = MessagePriority.HIGH,
                    status = MessageStatus.RELAYING,
                    payload = payload1,
                    createdAt = created1,
                    expiresAt = expires1,
                    hopCount = 1,
                    maxHops = 5,
                    transportType = TransportType.BLUETOOTH_LE,
                    fingerprintSha256 = calculateSeedFingerprint("NODE-7F4A", payload1, created1, expires1, 5)
                ),
                EmergencyMessage(
                    messageId = "msg_seed_02",
                    senderId = "NODE-7F4A",
                    type = MessageType.STATUS_UPDATE,
                    priority = MessagePriority.NORMAL,
                    status = MessageStatus.SENT,
                    payload = payload2,
                    createdAt = created2,
                    expiresAt = expires2,
                    hopCount = 3,
                    maxHops = 5,
                    transportType = TransportType.LOCAL_ONLY,
                    fingerprintSha256 = calculateSeedFingerprint("NODE-7F4A", payload2, created2, expires2, 5)
                ),
                EmergencyMessage(
                    messageId = "msg_seed_03",
                    senderId = "NODE-7F4A",
                    type = MessageType.STATUS_UPDATE,
                    priority = MessagePriority.NORMAL,
                    status = MessageStatus.DELIVERED,
                    payload = payload3,
                    createdAt = created3,
                    expiresAt = expires3,
                    hopCount = 2,
                    maxHops = 5,
                    transportType = TransportType.LOCAL_ONLY,
                    fingerprintSha256 = calculateSeedFingerprint("NODE-7F4A", payload3, created3, expires3, 5)
                )
            )
        }
    }
}
