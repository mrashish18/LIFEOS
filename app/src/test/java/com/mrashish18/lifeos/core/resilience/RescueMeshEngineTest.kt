package com.mrashish18.lifeos.core.resilience

import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessagePriority
import com.mrashish18.lifeos.core.model.MessageStatus
import com.mrashish18.lifeos.core.model.MessageType
import com.mrashish18.lifeos.core.model.RelayHop
import com.mrashish18.lifeos.core.model.TransportType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.security.MessageDigest
import java.time.Duration
import java.time.Instant

class RescueMeshEngineTest {

    private lateinit var engine: RescueMeshEngine

    @Before
    fun setUp() {
        engine = RescueMeshEngine()
    }

    @Test
    fun testComputeSha256CalculatesGenuine64CharHex() {
        val input = "TEST_PAYLOAD_12345"
        val engineHash = engine.computeSha256(input)

        val digest = MessageDigest.getInstance("SHA-256")
        val expectedBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        val expectedHex = expectedBytes.joinToString("") { "%02x".format(it) }

        assertEquals(64, engineHash.length)
        assertEquals(expectedHex, engineHash)
    }

    @Test
    fun testCreateMessageInitializesValidMessage() {
        val sender = "NODE-ALPHA"
        val payload = "Medical assistance required at Sector 4"
        val now = Instant.now()

        val msg = engine.createMessage(
            senderId = sender,
            payload = payload,
            type = MessageType.MEDICAL,
            priority = MessagePriority.CRITICAL,
            createdAt = now
        )

        assertNotNull(msg.messageId)
        assertEquals(sender, msg.senderId)
        assertEquals(payload, msg.payload)
        assertEquals(MessageType.MEDICAL, msg.type)
        assertEquals(MessagePriority.CRITICAL, msg.priority)
        assertEquals(MessageStatus.DRAFT, msg.status)
        assertEquals(0, msg.hopCount)
        assertEquals(RescueMeshEngine.DEFAULT_MAX_HOPS, msg.maxHops)
        assertEquals(now.plus(RescueMeshEngine.DEFAULT_TTL_DURATION), msg.expiresAt)

        val expectedRaw = "${msg.messageId}:$sender:$payload:${now.toEpochMilli()}"
        val expectedSha = engine.computeSha256(expectedRaw)
        assertEquals(expectedSha, msg.fingerprintSha256)
    }

    @Test
    fun testIsExpiredIdentifiesExpiredMessages() {
        val now = Instant.now()
        val validMsg = engine.createMessage(
            senderId = "NODE-A",
            payload = "Alive",
            createdAt = now,
            ttlDuration = Duration.ofHours(1)
        )

        assertFalse(engine.isExpired(validMsg, now.plus(Duration.ofMinutes(30))))
        assertTrue(engine.isExpired(validMsg, now.plus(Duration.ofHours(2))))
    }

    @Test
    fun testIsHopLimitReached() {
        val baseMsg = engine.createMessage(
            senderId = "NODE-A",
            payload = "Relay test",
            maxHops = 3
        )

        assertFalse(engine.isHopLimitReached(baseMsg))

        val hop1 = baseMsg.copy(hopCount = 1)
        assertFalse(engine.isHopLimitReached(hop1))

        val hop2 = baseMsg.copy(hopCount = 2)
        assertFalse(engine.isHopLimitReached(hop2))

        val hop3 = baseMsg.copy(hopCount = 3)
        assertTrue(engine.isHopLimitReached(hop3))

        val hop4 = baseMsg.copy(hopCount = 4)
        assertTrue(engine.isHopLimitReached(hop4))
    }

    @Test
    fun testIsDuplicateMatchesOnIdOrFingerprint() {
        val msg1 = engine.createMessage(senderId = "A", payload = "Test 1")
        val msg2 = engine.createMessage(senderId = "B", payload = "Test 2")

        val pool = listOf(msg1, msg2)

        assertTrue(engine.isDuplicate(msg1, pool))

        val duplicateIdMsg = engine.createMessage(senderId = "C", payload = "Different").copy(messageId = msg1.messageId)
        assertTrue(engine.isDuplicate(duplicateIdMsg, pool))

        val duplicateFingerprintMsg = engine.createMessage(senderId = "D", payload = "Different").copy(fingerprintSha256 = msg2.fingerprintSha256)
        assertTrue(engine.isDuplicate(duplicateFingerprintMsg, pool))

        val novelMsg = engine.createMessage(senderId = "E", payload = "Novel payload")
        assertFalse(engine.isDuplicate(novelMsg, pool))
    }

    @Test
    fun testValidateTransition() {
        assertTrue(engine.validateTransition(MessageStatus.DRAFT, MessageStatus.QUEUED))
        assertTrue(engine.validateTransition(MessageStatus.QUEUED, MessageStatus.RELAYING))
        assertTrue(engine.validateTransition(MessageStatus.QUEUED, MessageStatus.SENT))
        assertTrue(engine.validateTransition(MessageStatus.RELAYING, MessageStatus.SENT))
        assertTrue(engine.validateTransition(MessageStatus.SENT, MessageStatus.DELIVERED))
        assertTrue(engine.validateTransition(MessageStatus.QUEUED, MessageStatus.EXPIRED))
        assertTrue(engine.validateTransition(MessageStatus.RELAYING, MessageStatus.FAILED))

        assertFalse(engine.validateTransition(MessageStatus.DELIVERED, MessageStatus.QUEUED))
        assertFalse(engine.validateTransition(MessageStatus.DELIVERED, MessageStatus.RELAYING))
        assertFalse(engine.validateTransition(MessageStatus.EXPIRED, MessageStatus.DELIVERED))
        assertFalse(engine.validateTransition(MessageStatus.FAILED, MessageStatus.SENT))
        assertFalse(engine.validateTransition(MessageStatus.SENT, MessageStatus.DRAFT))
        assertFalse(engine.validateTransition(MessageStatus.SENT, MessageStatus.QUEUED))
    }

    @Test
    fun testProcessRelayIncrementsHops() {
        val original = engine.createMessage(
            senderId = "NODE-ROOT",
            payload = "Evacuation route update"
        ).copy(status = MessageStatus.QUEUED)

        val relayTime = Instant.now()
        val result = engine.processRelay(
            message = original,
            relayerNodeId = "PEER-101",
            transportUsed = TransportType.BLUETOOTH_LE,
            relayedAt = relayTime
        )

        assertTrue(result.isSuccess)
        val relayed = result.getOrThrow()

        assertEquals(1, relayed.hopCount)
        assertEquals(MessageStatus.RELAYING, relayed.status)
        assertEquals(TransportType.BLUETOOTH_LE, relayed.transportType)
        assertEquals(1, relayed.relayHistory.size)

        val hop = relayed.relayHistory.first()
        assertEquals(1, hop.hopNumber)
        assertEquals("PEER-101", hop.relayedBy)
        assertEquals(TransportType.BLUETOOTH_LE, hop.transportUsed)
        assertEquals(relayTime, hop.relayedAt)
    }

    @Test
    fun testProcessRelayRejectsExceededHops() {
        val original = engine.createMessage(
            senderId = "NODE-ROOT",
            payload = "High hops test",
            maxHops = 2
        ).copy(status = MessageStatus.RELAYING, hopCount = 2)

        val result = engine.processRelay(
            message = original,
            relayerNodeId = "PEER-OVERLIMIT",
            transportUsed = TransportType.LOCAL_ONLY
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("exceeded") == true)
    }

    @Test
    fun testProcessRelayRejectsExpiredMessages() {
        val expired = engine.createMessage(
            senderId = "NODE-ROOT",
            payload = "Old message",
            createdAt = Instant.now().minus(Duration.ofHours(50)),
            ttlDuration = Duration.ofHours(48)
        )

        val result = engine.processRelay(
            message = expired,
            relayerNodeId = "PEER-AFTER-TTL",
            transportUsed = TransportType.LOCAL_ONLY
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("expired") == true)
    }
}
