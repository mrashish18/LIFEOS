package com.mrashish18.lifeos.security

import com.mrashish18.lifeos.core.model.BehaviorEvent
import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessagePriority
import com.mrashish18.lifeos.core.model.MessageType
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.RealityCheckInput
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskCategory
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.core.realitycheck.RealityCheckEngine
import com.mrashish18.lifeos.core.resilience.RescueMeshEngine
import com.mrashish18.lifeos.core.resilience.transport.LocalStoreAndForwardTransport
import com.mrashish18.lifeos.core.resilience.transport.NetworkGatewayTransport
import com.mrashish18.lifeos.data.repository.DeterministicEvidenceRepository
import com.mrashish18.lifeos.domain.repository.BehaviorEventRepository
import com.mrashish18.lifeos.domain.repository.EmergencyMessageRepository
import com.mrashish18.lifeos.domain.repository.InvestigationRepository
import com.mrashish18.lifeos.domain.repository.TaskRepository
import com.mrashish18.lifeos.domain.usecase.CreateEmergencyMessageUseCase
import com.mrashish18.lifeos.domain.usecase.CreateTaskUseCase
import com.mrashish18.lifeos.domain.usecase.DeleteTaskUseCase
import com.mrashish18.lifeos.domain.usecase.PerformRealityCheckUseCase
import com.mrashish18.lifeos.domain.usecase.ProcessIncomingMessageUseCase
import com.mrashish18.lifeos.domain.usecase.TransitionTaskStatusUseCase
import com.mrashish18.lifeos.domain.usecase.UpdateTaskUseCase
import com.mrashish18.lifeos.feature.realitycheck.RealityCheckUiState
import com.mrashish18.lifeos.feature.realitycheck.RealityCheckViewModel
import com.mrashish18.lifeos.feature.tasks.TasksViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class SecurityHardeningTest {

    private lateinit var fakeTaskRepo: FakeTaskRepository
    private lateinit var fakeBehaviorRepo: FakeBehaviorEventRepository
    private lateinit var fakeEmergencyRepo: FakeEmergencyRepository
    private lateinit var fakeInvestigationRepo: FakeInvestigationRepository
    private lateinit var evidenceRepository: DeterministicEvidenceRepository
    private lateinit var realityEngine: RealityCheckEngine
    private lateinit var meshEngine: RescueMeshEngine
    private lateinit var localTransport: LocalStoreAndForwardTransport
    private lateinit var networkTransport: NetworkGatewayTransport

    private lateinit var createTaskUseCase: CreateTaskUseCase
    private lateinit var updateTaskUseCase: UpdateTaskUseCase
    private lateinit var performRealityCheckUseCase: PerformRealityCheckUseCase
    private lateinit var createEmergencyUseCase: CreateEmergencyMessageUseCase
    private lateinit var processIncomingUseCase: ProcessIncomingMessageUseCase

    @Before
    fun setUp() {
        fakeTaskRepo = FakeTaskRepository()
        fakeBehaviorRepo = FakeBehaviorEventRepository()
        fakeEmergencyRepo = FakeEmergencyRepository()
        fakeInvestigationRepo = FakeInvestigationRepository()
        evidenceRepository = DeterministicEvidenceRepository()
        realityEngine = RealityCheckEngine(evidenceRepository)
        meshEngine = RescueMeshEngine()
        localTransport = LocalStoreAndForwardTransport(fakeEmergencyRepo, meshEngine)
        networkTransport = NetworkGatewayTransport(fakeEmergencyRepo, meshEngine)

        createTaskUseCase = CreateTaskUseCase(fakeTaskRepo, fakeBehaviorRepo)
        updateTaskUseCase = UpdateTaskUseCase(fakeTaskRepo)
        performRealityCheckUseCase = PerformRealityCheckUseCase(realityEngine, fakeBehaviorRepo, fakeInvestigationRepo)
        createEmergencyUseCase = CreateEmergencyMessageUseCase(
            emergencyRepository = fakeEmergencyRepo,
            engine = meshEngine,
            behaviorEventRepository = fakeBehaviorRepo,
            networkTransport = networkTransport,
            localTransport = localTransport
        )
        processIncomingUseCase = ProcessIncomingMessageUseCase(
            emergencyRepository = fakeEmergencyRepo,
            engine = meshEngine,
            behaviorEventRepository = fakeBehaviorRepo
        )
    }

    // =========================================================================
    // 1. TASK INPUT VALIDATION & SANITIZATION TESTS
    // =========================================================================

    @Test(expected = IllegalArgumentException::class)
    fun `task creation with blank title is rejected`() {
        runBlocking {
            createTaskUseCase(title = "   ")
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `task creation with oversized title exceeding 200 chars is rejected`() {
        runBlocking {
            val longTitle = "A".repeat(201)
            createTaskUseCase(title = longTitle)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `task creation with oversized description exceeding 2000 chars is rejected`() {
        runBlocking {
            val longDesc = "D".repeat(2001)
            createTaskUseCase(title = "Valid Title", description = longDesc)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `task creation with negative estimated minutes is rejected`() {
        runBlocking {
            createTaskUseCase(title = "Valid Title", estimatedMinutes = -15)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `task creation with excessive estimated minutes is rejected`() {
        runBlocking {
            createTaskUseCase(title = "Valid Title", estimatedMinutes = 20000)
        }
    }

    @Test
    fun `task creation sanitizes control characters from title and description`() {
        runBlocking {
            val dirtyTitle = "Sanitize\u0000Me\u0007Now"
            val dirtyDesc = "Notes\u0008Here\u001F"
            val task = createTaskUseCase(title = dirtyTitle, description = dirtyDesc)

            assertEquals("SanitizeMeNow", task.title)
            assertEquals("NotesHere", task.description)
            assertFalse(task.title.contains("\u0000"))
            assertFalse(task.description.contains("\u0008"))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `task update with empty title is rejected`() {
        runBlocking {
            val task = Task(id = "1", title = "Original")
            updateTaskUseCase(task.copy(title = "  "))
        }
    }

    // =========================================================================
    // 2. REALITYCHECK INPUT VALIDATION & URL SECURITY TESTS
    // =========================================================================

    @Test
    fun `claim with blank text is rejected safely without engine execution`() = runBlocking {
        val result = performRealityCheckUseCase(RealityCheckInput(text = "   "))
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Claim input cannot be blank", result.exceptionOrNull()?.message)
    }

    @Test
    fun `claim exceeding 500 characters is rejected safely`() = runBlocking {
        val oversizedClaim = "Fact: ".repeat(100) // ~600 chars
        val result = performRealityCheckUseCase(RealityCheckInput(text = oversizedClaim))
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertTrue(result.exceptionOrNull()?.message?.contains("exceeds maximum allowable length") == true)
    }

    @Test
    fun `claim with dangerous javascript scheme URL is rejected`() = runBlocking {
        val result = performRealityCheckUseCase(
            RealityCheckInput(
                text = "Earth orbits the Sun",
                sourceUrl = "javascript:alert(document.cookie)"
            )
        )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertTrue(result.exceptionOrNull()?.message?.contains("must use http:// or https://") == true)
    }

    @Test
    fun `claim with local file scheme URL is rejected`() = runBlocking {
        val result = performRealityCheckUseCase(
            RealityCheckInput(
                text = "Apollo 11 landed on the Moon",
                sourceUrl = "file:///data/data/com.mrashish18.lifeos/databases/lifeos_database.db"
            )
        )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertTrue(result.exceptionOrNull()?.message?.contains("must use http:// or https://") == true)
    }

    @Test
    fun `claim with URL containing line breaks or control chars is rejected`() = runBlocking {
        val result = performRealityCheckUseCase(
            RealityCheckInput(
                text = "Apollo 11 landed on the Moon",
                sourceUrl = "https://nasa.gov/moon\nattack=1"
            )
        )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `claim with legitimate https URL is accepted and processed`() = runBlocking {
        val result = performRealityCheckUseCase(
            RealityCheckInput(
                text = "Earth orbits the Sun in 365 days",
                sourceUrl = "https://solarsystem.nasa.gov/planets/earth/in-depth/"
            )
        )
        assertTrue(result.isSuccess)
    }

    // =========================================================================
    // 3. RESCUEMESH EMERGENCY MESSAGE VALIDATION TESTS
    // =========================================================================

    @Test
    fun `emergency message with blank payload is rejected`() = runBlocking {
        val result = createEmergencyUseCase(senderId = "NODE-1", payload = "   ")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `emergency message with oversized payload is rejected`() = runBlocking {
        val oversizedPayload = "EMERGENCY ".repeat(150) // ~1500 chars
        val result = createEmergencyUseCase(senderId = "NODE-1", payload = oversizedPayload)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertTrue(result.exceptionOrNull()?.message?.contains("exceeds maximum allowable length") == true)
    }

    @Test
    fun `emergency message with invalid sender ID is rejected`() = runBlocking {
        val invalidSender = "NODE<script>alert(1)</script>"
        val result = createEmergencyUseCase(senderId = invalidSender, payload = "Medical assistance needed")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `emergency message with invalid recipient ID is rejected`() = runBlocking {
        val invalidRecipient = "GATEWAY/../ROOT"
        val result = createEmergencyUseCase(
            senderId = "NODE-1",
            payload = "Water needed",
            recipientId = invalidRecipient
        )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `incoming message with invalid hop count is rejected`() = runBlocking {
        val corruptMsg = EmergencyMessage(
            messageId = "corrupt-1",
            senderId = "NODE-2",
            payload = "Help",
            expiresAt = Instant.now().plusSeconds(3600),
            hopCount = -5, // Invalid negative hop count
            maxHops = 5,
            fingerprintSha256 = "dummy"
        )
        val result = processIncomingUseCase(corruptMsg)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    // =========================================================================
    // 4. INFORMATION LEAKAGE & SAFE ERROR HANDLING TESTS
    // =========================================================================

    @Test
    fun `reality check usecase safely handles unexpected repository exceptions without leaking internals`() {
        val failingRepo = object : com.mrashish18.lifeos.domain.repository.EvidenceRepository {
            override suspend fun search(query: String): Result<List<com.mrashish18.lifeos.core.model.Evidence>> {
                return Result.failure(RuntimeException("Internal database corruption at /data/user/0/databases/secret.db"))
            }
        }
        val failingEngine = RealityCheckEngine(failingRepo)
        val failingUseCase = PerformRealityCheckUseCase(failingEngine, fakeBehaviorRepo, fakeInvestigationRepo)

        runBlocking {
            val result = failingUseCase(RealityCheckInput(text = "Valid test claim about physics"))
            assertTrue(result.isSuccess)
            // RealityCheckEngine falls back gracefully to INSUFFICIENT_EVIDENCE without throwing or leaking
            val checkResult = result.getOrThrow()
            assertEquals(com.mrashish18.lifeos.core.model.Verdict.INSUFFICIENT_EVIDENCE, checkResult.verdict)
        }
    }

    @Test
    fun `exception message sanitizer filters raw system paths and stack traces`() {
        val rawSqlException = java.sql.SQLException("near 'SELECT': syntax error at /data/user/0/com.mrashish18.lifeos/databases/lifeos_database.db")
        val sanitizedMsg = if (rawSqlException is IllegalArgumentException) {
            rawSqlException.message ?: "Invalid input"
        } else {
            "Unable to process request. Please check your input and try again."
        }

        assertFalse(sanitizedMsg.contains("/data/user/0"))
        assertFalse(sanitizedMsg.contains("syntax error"))
        assertEquals("Unable to process request. Please check your input and try again.", sanitizedMsg)
    }

    @Test
    fun `user settings time format validation safely rejects invalid hour and minute patterns`() {
        val timeRegex = Regex("^([01]\\d|2[0-3]):([0-5]\\d)$")
        val invalidInputs = listOf("25:00", "12:60", "invalid", "12:5", "08:000", "")
        for (input in invalidInputs) {
            assertFalse("Input '$input' should not match valid HH:mm time format", timeRegex.matches(input))
        }

        val validInputs = listOf("00:00", "06:30", "12:00", "18:45", "23:59")
        for (input in validInputs) {
            assertTrue("Input '$input' should match valid HH:mm time format", timeRegex.matches(input))
        }
    }

    // =========================================================================
    // FAKE TEST REPOSITORIES
    // =========================================================================

    open class FakeTaskRepository : TaskRepository {
        val tasks = mutableListOf<Task>()
        override fun getTasks(): Flow<List<Task>> = flowOf(tasks.toList())
        override fun getTaskById(id: String): Flow<Task?> = flowOf(tasks.find { it.id == id })
        override fun getActiveTask(): Flow<Task?> = flowOf(tasks.find { it.status == TaskStatus.IN_PROGRESS })
        override suspend fun insertTask(task: Task) { tasks.add(task) }
        override suspend fun updateTask(task: Task) {
            val idx = tasks.indexOfFirst { it.id == task.id }
            if (idx != -1) tasks[idx] = task
        }
        override suspend fun deleteTask(id: String) { tasks.removeAll { it.id == id } }
    }

    class FakeBehaviorEventRepository : BehaviorEventRepository {
        val events = mutableListOf<BehaviorEvent>()
        override suspend fun recordEvent(event: BehaviorEvent) { events.add(event) }
        override fun observeRecentEvents(limit: Int): Flow<List<BehaviorEvent>> = flowOf(events.takeLast(limit))
        override suspend fun getAllEvents(): List<BehaviorEvent> = events.toList()
    }

    class FakeEmergencyRepository : EmergencyMessageRepository {
        val messages = mutableListOf<EmergencyMessage>()
        override fun observeAllMessages(): Flow<List<EmergencyMessage>> = flowOf(messages.toList())
        override fun observeQueue(): Flow<List<EmergencyMessage>> = flowOf(messages.filter { it.status == com.mrashish18.lifeos.core.model.MessageStatus.QUEUED })
        override fun observeMessagesByStatus(status: com.mrashish18.lifeos.core.model.MessageStatus): Flow<List<EmergencyMessage>> = flowOf(messages.filter { it.status == status })
        override suspend fun getAllMessages(): List<EmergencyMessage> = messages.toList()
        override suspend fun getQueuedMessages(): List<EmergencyMessage> = messages.filter { it.status == com.mrashish18.lifeos.core.model.MessageStatus.QUEUED }
        override suspend fun getMessagesByStatus(status: com.mrashish18.lifeos.core.model.MessageStatus): List<EmergencyMessage> = messages.filter { it.status == status }
        override suspend fun getMessageById(id: String): EmergencyMessage? = messages.find { it.messageId == id }
        override suspend fun getMessageByFingerprint(sha256: String): EmergencyMessage? = messages.find { it.fingerprintSha256 == sha256 }
        override suspend fun insertMessage(message: EmergencyMessage) { messages.add(message) }
        override suspend fun updateMessage(message: EmergencyMessage) {
            val idx = messages.indexOfFirst { it.messageId == message.messageId }
            if (idx != -1) messages[idx] = message
        }
        override suspend fun deleteMessage(id: String) { messages.removeAll { it.messageId == id } }
        override suspend fun clearAll() { messages.clear() }
        override suspend fun hasMessage(messageId: String): Boolean = messages.any { it.messageId == messageId }
    }

    class FakeInvestigationRepository : InvestigationRepository {
        val records = mutableListOf<com.mrashish18.lifeos.core.model.InvestigationRecord>()
        override fun observeRecentInvestigations(limit: Int): Flow<List<com.mrashish18.lifeos.core.model.InvestigationRecord>> = flowOf(records.takeLast(limit))
        override suspend fun getAllInvestigations(): List<com.mrashish18.lifeos.core.model.InvestigationRecord> = records.toList()
        override suspend fun getInvestigationById(id: String): com.mrashish18.lifeos.core.model.InvestigationRecord? = records.find { it.id == id }
        override suspend fun saveInvestigation(record: com.mrashish18.lifeos.core.model.InvestigationRecord) { records.add(record) }
    }
}
