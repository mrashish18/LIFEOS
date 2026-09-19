package com.mrashish18.lifeos.data.repository

import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessagePriority
import com.mrashish18.lifeos.core.model.MessageStatus
import com.mrashish18.lifeos.core.model.MessageType
import com.mrashish18.lifeos.core.model.RelayHop
import com.mrashish18.lifeos.core.model.TransportType
import com.mrashish18.lifeos.core.resilience.RescueMeshEngine
import com.mrashish18.lifeos.data.local.entity.EmergencyMessageEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.time.Instant

class EmergencyMessagePersistenceTest {

    private val engine = RescueMeshEngine()

    @Test
    fun testEntityDomainRoundTripWithRelayHistory() {
        val now = Instant.ofEpochMilli(1726740000000L)
        val original = EmergencyMessage(
            messageId = "test-msg-uuid-001",
            senderId = "NODE-ORIGIN-99",
            recipientId = "NODE-DEST-11",
            type = MessageType.MEDICAL,
            priority = MessagePriority.CRITICAL,
            status = MessageStatus.RELAYING,
            payload = "Urgent: insulin and hydration pack needed",
            createdAt = now,
            expiresAt = now.plus(Duration.ofHours(24)),
            hopCount = 2,
            maxHops = 5,
            transportType = TransportType.BLUETOOTH_LE,
            fingerprintSha256 = engine.calculateFingerprint(
                senderId = "NODE-ORIGIN-99",
                payload = "Urgent: insulin and hydration pack needed",
                createdAt = now,
                ttl = Duration.ofHours(24),
                hops = 5
            ),
            relayHistory = listOf(
                RelayHop(
                    hopNumber = 1,
                    relayedBy = "NODE-HOP-A",
                    relayedAt = now.plusSeconds(300),
                    transportUsed = TransportType.BLUETOOTH_LE
                ),
                RelayHop(
                    hopNumber = 2,
                    relayedBy = "NODE-HOP-B",
                    relayedAt = now.plusSeconds(600),
                    transportUsed = TransportType.WIFI_DIRECT
                )
            )
        )

        // Convert to entity
        val entity = EmergencyMessageEntity.fromDomain(original)
        assertEquals(original.messageId, entity.messageId)
        assertEquals(original.senderId, entity.senderId)
        assertEquals(original.recipientId, entity.recipientId)
        assertEquals(original.type.name, entity.type)
        assertEquals(original.priority.name, entity.priority)
        assertEquals(original.status.name, entity.status)
        assertEquals(original.payload, entity.payload)
        assertEquals(original.createdAt.toEpochMilli(), entity.createdAtEpochMillis)
        assertEquals(original.expiresAt.toEpochMilli(), entity.expiresAtEpochMillis)
        assertEquals(original.hopCount, entity.hopCount)
        assertEquals(original.maxHops, entity.maxHops)
        assertEquals(original.transportType.name, entity.transportType)
        assertEquals(original.fingerprintSha256, entity.fingerprintSha256)

        // Convert back to domain
        val restored = entity.toDomain()
        assertEquals(original.messageId, restored.messageId)
        assertEquals(original.senderId, restored.senderId)
        assertEquals(original.recipientId, restored.recipientId)
        assertEquals(original.type, restored.type)
        assertEquals(original.priority, restored.priority)
        assertEquals(original.status, restored.status)
        assertEquals(original.payload, restored.payload)
        assertEquals(original.createdAt, restored.createdAt)
        assertEquals(original.expiresAt, restored.expiresAt)
        assertEquals(original.hopCount, restored.hopCount)
        assertEquals(original.maxHops, restored.maxHops)
        assertEquals(original.transportType, restored.transportType)
        assertEquals(original.fingerprintSha256, restored.fingerprintSha256)

        // Relay history preserved
        assertEquals(2, restored.relayHistory.size)
        assertEquals(1, restored.relayHistory[0].hopNumber)
        assertEquals("NODE-HOP-A", restored.relayHistory[0].relayedBy)
        assertEquals(now.plusSeconds(300), restored.relayHistory[0].relayedAt)
        assertEquals(TransportType.BLUETOOTH_LE, restored.relayHistory[0].transportUsed)

        assertEquals(2, restored.relayHistory[1].hopNumber)
        assertEquals("NODE-HOP-B", restored.relayHistory[1].relayedBy)
        assertEquals(now.plusSeconds(600), restored.relayHistory[1].relayedAt)
        assertEquals(TransportType.WIFI_DIRECT, restored.relayHistory[1].transportUsed)
    }

    @Test
    fun testEmptyRelayHistorySerialization() {
        val emptyHistoryJson = EmergencyMessageEntity.formatRelayHistoryJson(emptyList())
        assertEquals("[]", emptyHistoryJson)

        val parsed = EmergencyMessageEntity.parseRelayHistoryJson("[]")
        assertTrue(parsed.isEmpty())

        val parsedBlank = EmergencyMessageEntity.parseRelayHistoryJson("")
        assertTrue(parsedBlank.isEmpty())
    }

    @Test
    fun testDefaultSeedMessagesHaveValidSha256() {
        val seeds = EmergencyMessage.defaultSeedMessages()
        assertEquals(3, seeds.size)
        for (seed in seeds) {
            assertNotNull(seed.messageId)
            assertEquals(64, seed.fingerprintSha256.length)
            assertTrue("Fingerprint must be hexadecimal", seed.fingerprintSha256.all { it in "0123456789abcdef" })
        }
    }
}
