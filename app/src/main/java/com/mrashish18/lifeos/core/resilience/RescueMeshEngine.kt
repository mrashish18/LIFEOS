package com.mrashish18.lifeos.core.resilience

import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessagePriority
import com.mrashish18.lifeos.core.model.MessageStatus
import com.mrashish18.lifeos.core.model.MessageType
import com.mrashish18.lifeos.core.model.RelayHop
import com.mrashish18.lifeos.core.model.TransportType
import java.security.MessageDigest
import java.time.Duration
import java.time.Instant
import java.util.UUID

class RescueMeshEngine(
    private val clock: java.time.Clock = java.time.Clock.systemUTC()
) {

    companion object {
        val DEFAULT_TTL_DURATION: Duration = Duration.ofHours(48)
        const val DEFAULT_MAX_HOPS: Int = 5
    }

    fun now(): Instant = clock.instant()

    fun computeSha256(raw: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(raw.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Deterministic SHA-256 fingerprint representing the canonical identity of an emergency message.
     */
    fun calculateFingerprint(
        senderId: String,
        payload: String,
        createdAt: Instant,
        ttl: Duration,
        hops: Int
    ): String {
        val raw = "$senderId|$payload|${createdAt.toEpochMilli()}|${ttl.toMillis()}|$hops"
        return computeSha256(raw)
    }

    fun calculateFingerprint(
        senderId: String,
        payload: String,
        createdAtEpochMillis: Long,
        expiresAtEpochMillis: Long,
        hops: Int
    ): String {
        val raw = "$senderId|$payload|$createdAtEpochMillis|$expiresAtEpochMillis|$hops"
        return computeSha256(raw)
    }

    fun calculateFingerprint(message: EmergencyMessage): String {
        val ttl = Duration.between(message.createdAt, message.expiresAt)
        return calculateFingerprint(
            senderId = message.senderId,
            payload = message.payload,
            createdAt = message.createdAt,
            ttl = ttl,
            hops = message.maxHops
        )
    }

    fun createMessage(
        senderId: String,
        payload: String,
        type: MessageType = MessageType.EMERGENCY,
        priority: MessagePriority = MessagePriority.NORMAL,
        recipientId: String? = null,
        createdAt: Instant = now(),
        ttlDuration: Duration = DEFAULT_TTL_DURATION,
        maxHops: Int = DEFAULT_MAX_HOPS
    ): EmergencyMessage {
        val messageId = UUID.randomUUID().toString()
        val expiresAt = createdAt.plus(ttlDuration)
        val fingerprint = calculateFingerprint(senderId, payload, createdAt, ttlDuration, maxHops)

        return EmergencyMessage(
            messageId = messageId,
            senderId = senderId,
            recipientId = recipientId,
            type = type,
            priority = priority,
            status = MessageStatus.DRAFT,
            payload = payload,
            createdAt = createdAt,
            expiresAt = expiresAt,
            hopCount = 0,
            maxHops = maxHops,
            transportType = TransportType.LOCAL_ONLY,
            fingerprintSha256 = fingerprint,
            relayHistory = emptyList()
        )
    }

    fun isExpired(message: EmergencyMessage, referenceTime: Instant = now()): Boolean {
        return referenceTime.isAfter(message.expiresAt)
    }

    fun isHopLimitReached(message: EmergencyMessage): Boolean {
        return message.hopCount >= message.maxHops
    }

    fun isDuplicate(message: EmergencyMessage, existingPool: List<EmergencyMessage>): Boolean {
        return existingPool.any {
            it.messageId == message.messageId || it.fingerprintSha256 == message.fingerprintSha256
        }
    }

    fun validateTransition(current: MessageStatus, target: MessageStatus): Boolean {
        if (current == target) return true

        return when (current) {
            MessageStatus.DRAFT -> target == MessageStatus.QUEUED || target == MessageStatus.FAILED
            MessageStatus.QUEUED -> target in setOf(
                MessageStatus.RELAYING,
                MessageStatus.SENT,
                MessageStatus.DELIVERED,
                MessageStatus.EXPIRED,
                MessageStatus.FAILED
            )
            MessageStatus.RELAYING -> target in setOf(
                MessageStatus.RELAYING,
                MessageStatus.SENT,
                MessageStatus.DELIVERED,
                MessageStatus.EXPIRED,
                MessageStatus.FAILED
            )
            MessageStatus.SENT -> target in setOf(
                MessageStatus.DELIVERED,
                MessageStatus.FAILED,
                MessageStatus.EXPIRED
            )
            MessageStatus.DELIVERED -> false
            MessageStatus.FAILED -> false
            MessageStatus.EXPIRED -> false
            MessageStatus.DUPLICATE -> false
        }
    }

    fun processRelay(
        message: EmergencyMessage,
        relayerNodeId: String,
        transportUsed: TransportType = TransportType.LOCAL_ONLY,
        relayedAt: Instant = now()
    ): Result<EmergencyMessage> {
        if (isExpired(message, relayedAt)) {
            return Result.failure(IllegalStateException("Cannot relay: message has expired"))
        }

        if (isHopLimitReached(message)) {
            return Result.failure(IllegalStateException("Cannot relay: maximum hop count (${message.maxHops}) exceeded"))
        }

        if (!validateTransition(message.status, MessageStatus.RELAYING)) {
            return Result.failure(IllegalStateException("Cannot relay: invalid status transition from ${message.status} to RELAYING"))
        }

        val nextHopNumber = message.hopCount + 1
        val newHop = RelayHop(
            hopNumber = nextHopNumber,
            relayedBy = relayerNodeId,
            relayedAt = relayedAt,
            transportUsed = transportUsed
        )

        val updated = message.copy(
            hopCount = nextHopNumber,
            status = MessageStatus.RELAYING,
            transportType = transportUsed,
            relayHistory = message.relayHistory + newHop
        )

        return Result.success(updated)
    }
}
