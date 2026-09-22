package com.mrashish18.lifeos.core.model

import com.mrashish18.lifeos.core.common.DispatcherProvider
import com.mrashish18.lifeos.data.local.dao.BehaviorEventDao
import com.mrashish18.lifeos.data.local.dao.NotificationDao
import com.mrashish18.lifeos.data.local.entity.BehaviorEventEntity
import com.mrashish18.lifeos.data.local.entity.NotificationEntity
import com.mrashish18.lifeos.data.repository.RoomBehaviorEventRepository
import com.mrashish18.lifeos.data.repository.RoomNotificationRepository
import com.mrashish18.lifeos.ui.navigation.LifeOsDestination
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class NotificationEventIntegrationTest {

    private lateinit var fakeBehaviorDao: FakeBehaviorEventDao
    private lateinit var fakeNotificationDao: FakeTestNotificationDao
    private lateinit var behaviorRepo: RoomBehaviorEventRepository
    private lateinit var notificationRepo: RoomNotificationRepository

    private val testDispatcher = object : DispatcherProvider {
        override val main: CoroutineDispatcher = Dispatchers.Unconfined
        override val io: CoroutineDispatcher = Dispatchers.Unconfined
        override val default: CoroutineDispatcher = Dispatchers.Unconfined
        override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
    }

    @Before
    fun setUp() {
        fakeBehaviorDao = FakeBehaviorEventDao()
        fakeNotificationDao = FakeTestNotificationDao()
        behaviorRepo = RoomBehaviorEventRepository(
            behaviorEventDao = fakeBehaviorDao,
            dispatcherProvider = testDispatcher,
            notificationDao = fakeNotificationDao
        )
        notificationRepo = RoomNotificationRepository(
            notificationDao = fakeNotificationDao,
            dispatcherProvider = testDispatcher
        )
    }

    @Test
    fun `task completed event generates PERSONAL notification with Tasks destination`() = runBlocking {
        val event = BehaviorEvent(
            id = "event-task-1",
            type = BehaviorEventType.TASK_COMPLETED,
            timestamp = Instant.ofEpochMilli(1726750000000L),
            metadata = mapOf(
                "taskId" to "task-999",
                "title" to "Establish LIFEOS Intelligence",
                "priority" to "HIGH"
            )
        )

        behaviorRepo.recordEvent(event)

        val notifications = notificationRepo.getAllNotifications()
        assertEquals(1, notifications.size)

        val notif = notifications[0]
        assertEquals("notif_task_completed_task-999", notif.id)
        assertEquals(NotificationCategory.PERSONAL, notif.category)
        assertEquals("Task completed", notif.title)
        assertTrue(notif.message.contains("Establish LIFEOS Intelligence"))
        assertEquals(LifeOsDestination.TASKS, notif.destination)
        assertEquals("task-999", notif.entityId)
        assertEquals(1, notificationRepo.getUnreadCount())
    }

    @Test
    fun `reality check verified event generates TRUTH notification with RealityCheck destination`() = runBlocking {
        val event = BehaviorEvent(
            id = "event-claim-1",
            type = BehaviorEventType.CLAIM_VERIFIED,
            timestamp = Instant.ofEpochMilli(1726750000000L),
            metadata = mapOf(
                "resultId" to "investigation-77",
                "verdict" to "SUPPORTED",
                "confidence" to "96",
                "evidenceCount" to "4"
            )
        )

        behaviorRepo.recordEvent(event)

        val notifications = notificationRepo.getAllNotifications()
        assertEquals(1, notifications.size)

        val notif = notifications[0]
        assertEquals("notif_claim_verified_investigation-77", notif.id)
        assertEquals(NotificationCategory.TRUTH, notif.category)
        assertEquals("Investigation completed", notif.title)
        assertTrue(notif.message.contains("SUPPORTED"))
        assertTrue(notif.message.contains("96%"))
        assertEquals(LifeOsDestination.REALITY_CHECK, notif.destination)
    }

    @Test
    fun `emergency message queued and relayed generate MESH notifications`() = runBlocking {
        val queuedEvent = BehaviorEvent(
            id = "event-msg-q",
            type = BehaviorEventType.EMERGENCY_MESSAGE_QUEUED,
            timestamp = Instant.ofEpochMilli(1726750000000L),
            metadata = mapOf("messageId" to "mesh-pkt-101")
        )
        behaviorRepo.recordEvent(queuedEvent)

        val relayedEvent = BehaviorEvent(
            id = "event-msg-r",
            type = BehaviorEventType.EMERGENCY_MESSAGE_RELAYED,
            timestamp = Instant.ofEpochMilli(1726750005000L),
            metadata = mapOf("messageId" to "mesh-pkt-101")
        )
        behaviorRepo.recordEvent(relayedEvent)

        val notifications = notificationRepo.getAllNotifications()
        assertEquals(2, notifications.size)
        assertTrue(notifications.all { it.category == NotificationCategory.MESH })
        assertTrue(notifications.all { it.destination == LifeOsDestination.RESILIENCE })
    }

    @Test
    fun `recommendation accepted and session completed generate LEARNING notifications`() = runBlocking {
        val recEvent = BehaviorEvent(
            id = "rec-event-1",
            type = BehaviorEventType.RECOMMENDATION_ACCEPTED,
            timestamp = Instant.ofEpochMilli(1726750000000L),
            metadata = mapOf(
                "recommendationId" to "rec-alpha",
                "notes" to "Morning deep focus"
            )
        )
        behaviorRepo.recordEvent(recEvent)

        val sessionEvent = BehaviorEvent(
            id = "session-event-1",
            type = BehaviorEventType.SESSION_COMPLETED,
            timestamp = Instant.ofEpochMilli(1726753600000L),
            metadata = mapOf("durationSeconds" to "3600")
        )
        behaviorRepo.recordEvent(sessionEvent)

        val notifications = notificationRepo.getAllNotifications()
        assertEquals(2, notifications.size)
        assertTrue(notifications.all { it.category == NotificationCategory.LEARNING })
        assertTrue(notifications.all { it.destination == LifeOsDestination.INTELLIGENCE })
    }

    @Test
    fun `trivial events CLAIM_SUBMITTED and RECOMMENDATION_SHOWN do NOT spam notifications`() = runBlocking {
        behaviorRepo.recordEvent(
            BehaviorEvent("e1", BehaviorEventType.CLAIM_SUBMITTED, Instant.now(), emptyMap())
        )
        behaviorRepo.recordEvent(
            BehaviorEvent("e2", BehaviorEventType.RECOMMENDATION_SHOWN, Instant.now(), emptyMap())
        )

        val notifications = notificationRepo.getAllNotifications()
        assertEquals(0, notifications.size)
        assertEquals(0, notificationRepo.getUnreadCount())
    }

    @Test
    fun `syncHistoricalEvents imports past events without duplicating or resetting read state`() = runBlocking {
        // Pre-populate behavior events
        val event1 = BehaviorEventEntity("e1", "TASK_CREATED", 1000L, """{"taskId":"t1","title":"Task 1","priority":"MEDIUM"}""")
        val event2 = BehaviorEventEntity("e2", "TASK_COMPLETED", 2000L, """{"taskId":"t2","title":"Task 2","priority":"HIGH"}""")
        fakeBehaviorDao.insert(event1)
        fakeBehaviorDao.insert(event2)

        // Sync
        behaviorRepo.syncHistoricalEvents(fakeNotificationDao)

        var notifications = notificationRepo.getAllNotifications()
        assertEquals(2, notifications.size)
        assertEquals(2, notificationRepo.getUnreadCount())

        // Mark one as read
        notificationRepo.markAsRead("notif_task_created_t1")
        assertEquals(1, notificationRepo.getUnreadCount())

        // Re-syncing (e.g. app restart)
        behaviorRepo.syncHistoricalEvents(fakeNotificationDao)

        // Count should still be 2, and read state preserved!
        notifications = notificationRepo.getAllNotifications()
        assertEquals(2, notifications.size)
        assertEquals(1, notificationRepo.getUnreadCount())
        val readNotif = notifications.first { it.id == "notif_task_created_t1" }
        assertTrue(readNotif.isRead)
    }
}

private class FakeBehaviorEventDao : BehaviorEventDao {
    private val events = mutableListOf<BehaviorEventEntity>()

    override fun observeRecentEvents(limit: Int): Flow<List<BehaviorEventEntity>> {
        return MutableStateFlow(events.takeLast(limit))
    }

    override fun observeAllEvents(): Flow<List<BehaviorEventEntity>> {
        return MutableStateFlow(events.toList())
    }

    override suspend fun getAllEvents(): List<BehaviorEventEntity> = events.toList()

    override suspend fun getRecentEvents(limit: Int): List<BehaviorEventEntity> {
        return events.sortedByDescending { it.timestampEpochMillis }.take(limit)
    }

    override suspend fun getEventsByType(type: String): List<BehaviorEventEntity> {
        return events.filter { it.type == type }
    }

    override suspend fun insert(event: BehaviorEventEntity): Long {
        events.removeAll { it.id == event.id }
        events.add(event)
        return 1L
    }

    override suspend fun insertAll(events: List<BehaviorEventEntity>): List<Long> {
        events.forEach { insert(it) }
        return events.map { 1L }
    }

    override suspend fun countEventsByType(type: String): Int {
        return events.count { it.type == type }
    }

    override fun observeTotalCount(): Flow<Int> = MutableStateFlow(events.size)

    override suspend fun clearAll(): Int {
        val count = events.size
        events.clear()
        return count
    }
}

private class FakeTestNotificationDao : NotificationDao {
    private val storage = mutableMapOf<String, NotificationEntity>()
    private val flow = MutableStateFlow<List<NotificationEntity>>(emptyList())

    private fun emit() {
        flow.value = storage.values.sortedByDescending { it.timestampEpochMillis }
    }

    override fun observeNotifications(): Flow<List<NotificationEntity>> = flow

    override fun observeUnreadCount(): Flow<Int> = flow.map { list -> list.count { !it.isRead } }

    override fun observeTotalCount(): Flow<Int> = flow.map { it.size }

    override suspend fun getAllNotifications(): List<NotificationEntity> {
        return storage.values.sortedByDescending { it.timestampEpochMillis }
    }

    override suspend fun getUnreadCount(): Int = storage.values.count { !it.isRead }

    override suspend fun getNotificationById(id: String): NotificationEntity? = storage[id]

    override suspend fun insert(notification: NotificationEntity): Long {
        if (!storage.containsKey(notification.id)) {
            storage[notification.id] = notification
            emit()
            return 1L
        }
        return -1L
    }

    override suspend fun insertAll(notifications: List<NotificationEntity>): List<Long> {
        return notifications.map { insert(it) }
    }

    override suspend fun markAsRead(id: String): Int {
        val existing = storage[id] ?: return 0
        storage[id] = existing.copy(isRead = true)
        emit()
        return 1
    }

    override suspend fun markAllAsRead(): Int {
        var c = 0
        storage.keys.toList().forEach { k ->
            if (!storage[k]!!.isRead) {
                storage[k] = storage[k]!!.copy(isRead = true)
                c++
            }
        }
        emit()
        return c
    }

    override suspend fun deleteById(id: String): Int {
        val rem = storage.remove(id) != null
        if (rem) emit()
        return if (rem) 1 else 0
    }

    override suspend fun clearAll(): Int {
        val c = storage.size
        storage.clear()
        emit()
        return c
    }
}
