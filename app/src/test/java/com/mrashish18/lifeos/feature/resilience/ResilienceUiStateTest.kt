package com.mrashish18.lifeos.feature.resilience

import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessagePriority
import com.mrashish18.lifeos.core.model.MessageStatus
import com.mrashish18.lifeos.core.model.MessageType
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.resilience.RescueMeshEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.time.Instant

class ResilienceUiStateTest {

    private val engine = RescueMeshEngine()

    @Test
    fun testDefaultStateValues() {
        val state = ResilienceUiState()
        assertEquals(NetworkState.UNKNOWN, state.networkState)
        assertEquals("NODE-7F4A", state.localNodeId)
        assertTrue(state.messages.isEmpty())
        assertEquals(0, state.queuedCount)
        assertEquals(0, state.relayingCount)
        assertEquals(0, state.deliveredCount)
        assertFalse(state.hasCriticalEmergency)
        assertTrue(state.filteredMessages.isEmpty())
    }

    @Test
    fun testFilteredMessagesWithActiveFilter() {
        val now = Instant.now()
        val queuedMsg = engine.createMessage(
            senderId = "NODE-1",
            payload = "First aid supply needed",
            priority = MessagePriority.CRITICAL,
            createdAt = now
        ).copy(status = MessageStatus.QUEUED)

        val sentMsg = engine.createMessage(
            senderId = "NODE-2",
            payload = "All safe at perimeter",
            priority = MessagePriority.NORMAL,
            createdAt = now
        ).copy(status = MessageStatus.SENT)

        val failedMsg = engine.createMessage(
            senderId = "NODE-3",
            payload = "Hop limit reached",
            priority = MessagePriority.HIGH,
            createdAt = now
        ).copy(status = MessageStatus.FAILED)

        val stateAll = ResilienceUiState(
            messages = listOf(queuedMsg, sentMsg, failedMsg),
            activeFilter = null
        )
        assertEquals(3, stateAll.filteredMessages.size)

        val stateQueued = stateAll.copy(activeFilter = MessageStatus.QUEUED)
        assertEquals(1, stateQueued.filteredMessages.size)
        assertEquals(queuedMsg.messageId, stateQueued.filteredMessages.first().messageId)

        val stateSent = stateAll.copy(activeFilter = MessageStatus.SENT)
        assertEquals(1, stateSent.filteredMessages.size)
        assertEquals(sentMsg.messageId, stateSent.filteredMessages.first().messageId)

        val stateFailed = stateAll.copy(activeFilter = MessageStatus.FAILED)
        assertEquals(1, stateFailed.filteredMessages.size)
        assertEquals(failedMsg.messageId, stateFailed.filteredMessages.first().messageId)
    }

    @Test
    fun testHasCriticalEmergencyDetection() {
        val normalQueued = engine.createMessage(
            senderId = "NODE-A",
            payload = "Water request",
            priority = MessagePriority.NORMAL
        ).copy(status = MessageStatus.QUEUED)

        val state1 = ResilienceUiState(messages = listOf(normalQueued))
        assertFalse(state1.hasCriticalEmergency)

        val criticalDelivered = engine.createMessage(
            senderId = "NODE-B",
            payload = "Medical resolved",
            priority = MessagePriority.CRITICAL
        ).copy(status = MessageStatus.DELIVERED)

        val state2 = ResilienceUiState(messages = listOf(criticalDelivered))
        assertFalse("Delivered critical messages should not trigger active emergency banner", state2.hasCriticalEmergency)

        val criticalQueued = engine.createMessage(
            senderId = "NODE-C",
            payload = "Active trauma triage",
            priority = MessagePriority.CRITICAL
        ).copy(status = MessageStatus.QUEUED)

        val state3 = ResilienceUiState(messages = listOf(normalQueued, criticalQueued))
        assertTrue(state3.hasCriticalEmergency)

        val criticalRelaying = engine.createMessage(
            senderId = "NODE-D",
            payload = "Trauma relaying",
            priority = MessagePriority.CRITICAL
        ).copy(status = MessageStatus.RELAYING)

        val state4 = ResilienceUiState(messages = listOf(criticalRelaying))
        assertTrue(state4.hasCriticalEmergency)
    }

    @Test
    fun testFeedbackMessageAndRelayStatusUpdates() {
        val stateWithFeedback = ResilienceUiState(
            feedbackMessage = "Relayed to PEER-HOP-1234 (Hop 1/3)"
        )
        assertEquals("Relayed to PEER-HOP-1234 (Hop 1/3)", stateWithFeedback.feedbackMessage)
        assertFalse(stateWithFeedback.hasCriticalEmergency)
    }
}

