package com.mrashish18.lifeos.domain.resilience

import com.mrashish18.lifeos.core.model.BehaviorEvent
import com.mrashish18.lifeos.core.model.BehaviorEventType
import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessagePriority
import com.mrashish18.lifeos.core.model.MessageStatus
import com.mrashish18.lifeos.core.model.MessageType
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.TransportType
import com.mrashish18.lifeos.core.resilience.RescueMeshEngine
import com.mrashish18.lifeos.core.resilience.transport.LocalStoreAndForwardTransport
import com.mrashish18.lifeos.core.resilience.transport.NetworkGatewayTransport
import com.mrashish18.lifeos.domain.repository.BehaviorEventRepository
import com.mrashish18.lifeos.domain.repository.EmergencyMessageRepository
import com.mrashish18.lifeos.domain.usecase.CreateEmergencyMessageUseCase
import com.mrashish18.lifeos.domain.usecase.GetEmergencyQueueUseCase
import com.mrashish18.lifeos.domain.usecase.ProcessIncomingMessageUseCase
import com.mrashish18.lifeos.domain.usecase.RelayEmergencyMessageUseCase
import com.mrashish18.lifeos.domain.usecase.SyncEmergencyQueueUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.Instant

class EmergencyUseCasesTest {

    private lateinit var emergencyRepository: FakeEmergencyMessageRepository
    private lateinit var behaviorEventRepository: FakeBehaviorEventRepository
    private lateinit var engine: RescueMeshEngine
    private lateinit var localTransport: LocalStoreAndForwardTransport
    private lateinit var networkTransport: NetworkGatewayTransport

    private lateinit var createEmergencyMessageUseCase: CreateEmergencyMessageUseCase
    private lateinit var getEmergencyQueueUseCase: GetEmergencyQueueUseCase
    private lateinit var relayEmergencyMessageUseCase: RelayEmergencyMessageUseCase
    private lateinit var processIncomingMessageUseCase: ProcessIncomingMessageUseCase
    private lateinit var syncEmergencyQueueUseCase: SyncEmergencyQueueUseCase

    @Before
    fun setUp() {
        emergencyRepository = FakeEmergencyMessageRepository()
        behaviorEventRepository = FakeBehaviorEventRepository()
        engine = RescueMeshEngine()
        localTransport = LocalStoreAndForwardTransport(emergencyRepository, engine)
        networkTransport = NetworkGatewayTransport(emergencyRepository, engine)

        createEmergencyMessageUseCase = CreateEmergencyMessageUseCase(
            emergencyRepository = emergencyRepository,
            engine = engine,
            behaviorEventRepository = behaviorEventRepository,
            networkTransport = networkTransport,
            localTransport = localTransport
        )
        getEmergencyQueueUseCase = GetEmergencyQueueUseCase(emergencyRepository)
        relayEmergencyMessageUseCase = RelayEmergencyMessageUseCase(
            emergencyRepository = emergencyRepository,
            engine = engine,
            behaviorEventRepository = behaviorEventRepository
        )
        processIncomingMessageUseCase = ProcessIncomingMessageUseCase(
            emergencyRepository = emergencyRepository,
            engine = engine,
            behaviorEventRepository = behaviorEventRepository
        )
        syncEmergencyQueueUseCase = SyncEmergencyQueueUseCase(
            emergencyRepository = emergencyRepository,
            networkTransport = networkTransport,
            engine = engine,
            behaviorEventRepository = behaviorEventRepository
        )
    }

    @Test
    fun testCreateEmergencyMessageOffline() = runBlocking {
        val payload = "CONFIDENTIAL_MEDICAL_PAYLOAD_999"
        val result = createEmergencyMessageUseCase(
            senderId = "NODE-ORIGIN",
            payload = payload,
            type = MessageType.MEDICAL,
            priority = MessagePriority.CRITICAL,
            networkState = NetworkState.DISCONNECTED
        )

        assertTrue(result.isSuccess)
        val created = result.getOrThrow()
        assertEquals(MessageStatus.QUEUED, created.status)
        assertEquals(TransportType.LOCAL_ONLY, created.transportType)

        val stored = emergencyRepository.getMessageById(created.messageId)
        assertNotNull(stored)
        assertEquals(payload, stored?.payload)

        val events = behaviorEventRepository.events
        assertTrue(events.isNotEmpty())
        for (event in events) {
            assertFalse(
                "Behavior event metadata must not leak payload text",
                event.metadata.values.any { it.contains("CONFIDENTIAL_MEDICAL_PAYLOAD_999") }
            )
        }

        assertEquals(BehaviorEventType.EMERGENCY_MESSAGE_CREATED, events[0].type)
        assertEquals(BehaviorEventType.EMERGENCY_MESSAGE_QUEUED, events[1].type)
    }

    @Test
    fun testCreateEmergencyMessageOnline() = runBlocking {
        val result = createEmergencyMessageUseCase(
            senderId = "NODE-ORIGIN",
            payload = "Network connected transmission",
            type = MessageType.EMERGENCY,
            priority = MessagePriority.HIGH,
            networkState = NetworkState.CONNECTED_WIFI
        )

        assertTrue(result.isSuccess)
        val created = result.getOrThrow()
        assertEquals(MessageStatus.SENT, created.status)
        assertEquals(TransportType.NETWORK, created.transportType)

        val events = behaviorEventRepository.events
        assertTrue(events.any { it.type == BehaviorEventType.EMERGENCY_MESSAGE_SENT })
        assertFalse(events.any { it.type == BehaviorEventType.EMERGENCY_MESSAGE_DELIVERED })
    }

    @Test
    fun testRelayEmergencyMessage() = runBlocking {
        val msg = engine.createMessage(
            senderId = "NODE-A",
            payload = "Relay test message"
        ).copy(status = MessageStatus.QUEUED)
        emergencyRepository.insertMessage(msg)

        val result = relayEmergencyMessageUseCase(
            messageId = msg.messageId,
            relayerNodeId = "PEER-HOP-1",
            transport = TransportType.LOCAL_ONLY
        )

        assertTrue(result.isSuccess)
        val relayed = result.getOrThrow()
        assertEquals(1, relayed.hopCount)
        assertEquals(MessageStatus.RELAYING, relayed.status)
        assertEquals(1, relayed.relayHistory.size)
        assertEquals("PEER-HOP-1", relayed.relayHistory.first().relayedBy)

        val relayEvent = behaviorEventRepository.events.last()
        assertEquals(BehaviorEventType.EMERGENCY_MESSAGE_RELAYED, relayEvent.type)
        assertEquals("1", relayEvent.metadata["hop"])
        assertEquals("PEER-HOP-1", relayEvent.metadata["relayer"])
    }

    @Test
    fun testRelayEmergencyMessageHopLimitExceeded() = runBlocking {
        val msg = engine.createMessage(
            senderId = "NODE-A",
            payload = "Hop limit test",
            maxHops = 3
        ).copy(status = MessageStatus.RELAYING, hopCount = 3)
        emergencyRepository.insertMessage(msg)

        val result = relayEmergencyMessageUseCase(
            messageId = msg.messageId,
            relayerNodeId = "PEER-HOP-4"
        )

        assertTrue(result.isFailure)
        val updatedInRepo = emergencyRepository.getMessageById(msg.messageId)
        assertEquals(MessageStatus.FAILED, updatedInRepo?.status)

        val failEvent = behaviorEventRepository.events.last()
        assertEquals(BehaviorEventType.EMERGENCY_MESSAGE_FAILED, failEvent.type)
    }

    @Test
    fun testProcessIncomingMessageRejectsDuplicate() = runBlocking {
        val msg = engine.createMessage(senderId = "NODE-X", payload = "Unique incoming")
        emergencyRepository.insertMessage(msg)

        val result = processIncomingMessageUseCase(msg)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Duplicate") == true)
    }

    @Test
    fun testProcessIncomingMessageMarksExpired() = runBlocking {
        val expired = engine.createMessage(
            senderId = "NODE-X",
            payload = "Expired incoming",
            createdAt = Instant.now().minus(Duration.ofHours(60)),
            ttlDuration = Duration.ofHours(48)
        )

        val result = processIncomingMessageUseCase(expired)
        assertTrue(result.isFailure)

        val stored = emergencyRepository.getMessageById(expired.messageId)
        assertEquals(MessageStatus.EXPIRED, stored?.status)
    }

    @Test
    fun testSyncEmergencyQueueSkipsWhenOffline() = runBlocking {
        val msg = engine.createMessage(senderId = "NODE-A", payload = "Queued").copy(status = MessageStatus.QUEUED)
        emergencyRepository.insertMessage(msg)

        val result = syncEmergencyQueueUseCase(NetworkState.DISCONNECTED)
        assertEquals(0, result.processedCount)
        assertEquals(0, result.syncedCount)
        assertTrue(result.explanation.contains("Network unavailable"))

        assertEquals(MessageStatus.QUEUED, emergencyRepository.getMessageById(msg.messageId)?.status)
    }

    @Test
    fun testSyncEmergencyQueueDrainsToSentWhenOnline() = runBlocking {
        val msg1 = engine.createMessage(senderId = "NODE-A", payload = "Queued 1").copy(status = MessageStatus.QUEUED)
        val msg2 = engine.createMessage(senderId = "NODE-B", payload = "Queued 2").copy(status = MessageStatus.QUEUED)
        emergencyRepository.insertMessage(msg1)
        emergencyRepository.insertMessage(msg2)

        val result = syncEmergencyQueueUseCase(NetworkState.CONNECTED_WIFI)
        assertEquals(2, result.processedCount)
        assertEquals(2, result.syncedCount)
        assertEquals(0, result.expiredCount)
        assertEquals(0, result.failedCount)

        assertEquals(MessageStatus.SENT, emergencyRepository.getMessageById(msg1.messageId)?.status)
        assertEquals(MessageStatus.SENT, emergencyRepository.getMessageById(msg2.messageId)?.status)
    }

    @Test
    fun testCreateEmergencyMessageRejectsDuplicateFingerprint() = runBlocking {
        val fixedInstant = Instant.parse("2026-09-19T10:00:00Z")
        val fixedClock = java.time.Clock.fixed(fixedInstant, java.time.ZoneOffset.UTC)
        val fixedEngine = RescueMeshEngine(fixedClock)
        val fixedCreateUseCase = CreateEmergencyMessageUseCase(
            emergencyRepository = emergencyRepository,
            engine = fixedEngine,
            behaviorEventRepository = behaviorEventRepository,
            networkTransport = networkTransport,
            localTransport = localTransport,
            clock = fixedClock
        )

        val firstResult = fixedCreateUseCase(
            senderId = "NODE-A",
            payload = "First message payload",
            networkState = NetworkState.DISCONNECTED
        )
        assertTrue(firstResult.isSuccess)

        // Attempting to create identical message at same fixed time results in identical fingerprint
        val duplicateResult = fixedCreateUseCase(
            senderId = "NODE-A",
            payload = "First message payload",
            networkState = NetworkState.DISCONNECTED
        )
        assertTrue(duplicateResult.isFailure)
        assertTrue(duplicateResult.exceptionOrNull()?.message?.contains("Duplicate message") == true)
    }

    @Test
    fun testProcessIncomingMessageRejectsFingerprintDuplicate() = runBlocking {
        val msg1 = engine.createMessage(senderId = "NODE-A", payload = "Original")
        emergencyRepository.insertMessage(msg1)

        // Different messageId but identical fingerprint
        val msg2 = msg1.copy(messageId = "DIFFERENT_ID_SAME_FINGERPRINT")
        val result = processIncomingMessageUseCase(msg2)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Duplicate") == true)
    }

    @Test
    fun testSyncEmergencyQueueOrdersByPriorityCriticalFirst() = runBlocking {
        val normalMsg = engine.createMessage(
            senderId = "NODE-N",
            payload = "Normal priority item",
            priority = MessagePriority.NORMAL
        ).copy(status = MessageStatus.QUEUED)

        val criticalMsg = engine.createMessage(
            senderId = "NODE-C",
            payload = "Critical urgent item",
            priority = MessagePriority.CRITICAL
        ).copy(status = MessageStatus.QUEUED)

        val highMsg = engine.createMessage(
            senderId = "NODE-H",
            payload = "High priority item",
            priority = MessagePriority.HIGH
        ).copy(status = MessageStatus.QUEUED)

        // Insert in non-priority order
        emergencyRepository.insertMessage(normalMsg)
        emergencyRepository.insertMessage(criticalMsg)
        emergencyRepository.insertMessage(highMsg)

        val result = syncEmergencyQueueUseCase(NetworkState.CONNECTED_WIFI)
        assertEquals(3, result.syncedCount)

        // Behavior events should record sent messages in order of priority: CRITICAL, HIGH, NORMAL
        val sentEvents = behaviorEventRepository.events.filter { it.type == BehaviorEventType.EMERGENCY_MESSAGE_SENT }
        assertEquals(3, sentEvents.size)
        assertEquals(criticalMsg.messageId, sentEvents[0].metadata["messageId"])
        assertEquals(highMsg.messageId, sentEvents[1].metadata["messageId"])
        assertEquals(normalMsg.messageId, sentEvents[2].metadata["messageId"])
    }

    @Test
    fun testSyncEmergencyQueueExpiresOldMessagesWithClock() = runBlocking {
        val baseTime = Instant.parse("2026-09-19T10:00:00Z")
        val clock = java.time.Clock.fixed(baseTime, java.time.ZoneOffset.UTC)
        val clockEngine = RescueMeshEngine(clock)

        val msg = clockEngine.createMessage(
            senderId = "NODE-A",
            payload = "Expires in 1 hour",
            createdAt = baseTime,
            ttlDuration = Duration.ofHours(1)
        ).copy(status = MessageStatus.QUEUED)
        emergencyRepository.insertMessage(msg)

        // Advance clock by 2 hours
        val futureClock = java.time.Clock.fixed(baseTime.plus(Duration.ofHours(2)), java.time.ZoneOffset.UTC)
        val futureSyncUseCase = SyncEmergencyQueueUseCase(
            emergencyRepository = emergencyRepository,
            networkTransport = networkTransport,
            engine = clockEngine,
            behaviorEventRepository = behaviorEventRepository,
            clock = futureClock
        )

        val syncResult = futureSyncUseCase(NetworkState.CONNECTED_WIFI)
        assertEquals(1, syncResult.expiredCount)
        assertEquals(0, syncResult.syncedCount)
        assertEquals(MessageStatus.EXPIRED, emergencyRepository.getMessageById(msg.messageId)?.status)
    }

    @Test
    fun testRelayRejectsInvalidStateTransition() = runBlocking {
        val deliveredMsg = engine.createMessage(senderId = "NODE-A", payload = "Delivered").copy(status = MessageStatus.DELIVERED)
        emergencyRepository.insertMessage(deliveredMsg)

        val result = relayEmergencyMessageUseCase(deliveredMsg.messageId, "PEER-1")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Invalid state transition") == true)
    }

    private class FakeEmergencyMessageRepository : EmergencyMessageRepository {
        private val messages = mutableMapOf<String, EmergencyMessage>()
        private val queueFlow = MutableStateFlow<List<EmergencyMessage>>(emptyList())
        private val allFlow = MutableStateFlow<List<EmergencyMessage>>(emptyList())

        private fun notifyFlows() {
            val allList = messages.values.toList()
            allFlow.value = allList
            queueFlow.value = allList.filter { it.status == MessageStatus.QUEUED || it.status == MessageStatus.RELAYING }
        }

        override suspend fun insertMessage(message: EmergencyMessage) {
            messages[message.messageId] = message
            notifyFlows()
        }

        override suspend fun updateMessage(message: EmergencyMessage) {
            messages[message.messageId] = message
            notifyFlows()
        }

        override suspend fun getMessageById(id: String): EmergencyMessage? {
            return messages[id]
        }

        override suspend fun getAllMessages(): List<EmergencyMessage> {
            return messages.values.toList()
        }

        override suspend fun getQueuedMessages(): List<EmergencyMessage> {
            return messages.values.filter { it.status == MessageStatus.QUEUED }
        }

        override suspend fun getMessagesByStatus(status: MessageStatus): List<EmergencyMessage> {
            return messages.values.filter { it.status == status }
        }

        override fun observeMessagesByStatus(status: MessageStatus): Flow<List<EmergencyMessage>> {
            return MutableStateFlow(messages.values.filter { it.status == status })
        }

        override fun observeQueue(): Flow<List<EmergencyMessage>> = queueFlow.asStateFlow()

        override fun observeAllMessages(): Flow<List<EmergencyMessage>> = allFlow.asStateFlow()

        override suspend fun hasMessage(messageId: String): Boolean {
            return messages.containsKey(messageId)
        }

        override suspend fun getMessageByFingerprint(sha256: String): EmergencyMessage? {
            return messages.values.find { it.fingerprintSha256 == sha256 }
        }

        override suspend fun deleteMessage(id: String) {
            messages.remove(id)
            notifyFlows()
        }

        override suspend fun clearAll() {
            messages.clear()
            notifyFlows()
        }
    }

    private class FakeBehaviorEventRepository : BehaviorEventRepository {
        val events = mutableListOf<BehaviorEvent>()

        override suspend fun recordEvent(event: BehaviorEvent) {
            events.add(event)
        }

        override fun observeRecentEvents(limit: Int): Flow<List<BehaviorEvent>> {
            return flowOf(events.takeLast(limit))
        }

        override suspend fun getAllEvents(): List<BehaviorEvent> {
            return events.toList()
        }
    }
}
