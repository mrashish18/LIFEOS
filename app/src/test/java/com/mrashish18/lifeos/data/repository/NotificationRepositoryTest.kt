package com.mrashish18.lifeos.data.repository

import com.mrashish18.lifeos.core.common.DispatcherProvider
import com.mrashish18.lifeos.core.model.LifeOsNotification
import com.mrashish18.lifeos.core.model.NotificationCategory
import com.mrashish18.lifeos.data.local.dao.NotificationDao
import com.mrashish18.lifeos.data.local.entity.NotificationEntity
import com.mrashish18.lifeos.ui.navigation.LifeOsDestination
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NotificationRepositoryTest {

    private lateinit var fakeDao: FakeNotificationDao
    private lateinit var repository: RoomNotificationRepository

    private val testDispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher = Dispatchers.Unconfined
        override val io: CoroutineDispatcher = Dispatchers.Unconfined
        override val default: CoroutineDispatcher = Dispatchers.Unconfined
        override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
    }

    @Before
    fun setUp() {
        fakeDao = FakeNotificationDao()
        repository = RoomNotificationRepository(fakeDao, testDispatcherProvider)
    }

    @Test
    fun `test insert and observe notifications`() = runBlocking {
        val notif1 = LifeOsNotification(
            id = "notif-1",
            category = NotificationCategory.PERSONAL,
            title = "Task completed",
            message = "Productivity registered",
            timestampEpochMillis = 1000L,
            isRead = false,
            destination = LifeOsDestination.TASKS
        )
        val notif2 = LifeOsNotification(
            id = "notif-2",
            category = NotificationCategory.TRUTH,
            title = "Investigation complete",
            message = "Verdict: Verified",
            timestampEpochMillis = 2000L,
            isRead = false,
            destination = LifeOsDestination.REALITY_CHECK
        )

        repository.addNotification(notif1)
        repository.addNotification(notif2)

        val list = repository.observeNotifications().first()
        assertEquals(2, list.size)
        // Descending timestamp: notif2 first
        assertEquals("notif-2", list[0].id)
        assertEquals(NotificationCategory.TRUTH, list[0].category)
        assertEquals("notif-1", list[1].id)
    }

    @Test
    fun `test unread count tracking`() = runBlocking {
        val notif1 = LifeOsNotification(
            id = "notif-1",
            category = NotificationCategory.PERSONAL,
            title = "Task 1",
            message = "Message 1",
            timestampEpochMillis = 1000L,
            isRead = false
        )
        val notif2 = LifeOsNotification(
            id = "notif-2",
            category = NotificationCategory.MESH,
            title = "Packet queued",
            message = "Mesh ready",
            timestampEpochMillis = 2000L,
            isRead = false
        )

        repository.addNotification(notif1)
        repository.addNotification(notif2)

        assertEquals(2, repository.observeUnreadCount().first())

        repository.markAsRead("notif-1")
        assertEquals(1, repository.observeUnreadCount().first())

        val updated = repository.getNotificationById("notif-1")
        assertNotNull(updated)
        assertTrue(updated!!.isRead)
    }

    @Test
    fun `test mark all as read`() = runBlocking {
        repository.addNotifications(
            listOf(
                LifeOsNotification("n1", NotificationCategory.PERSONAL, "T1", "M1", 1000L, false),
                LifeOsNotification("n2", NotificationCategory.LEARNING, "T2", "M2", 2000L, false),
                LifeOsNotification("n3", NotificationCategory.EMERGENCY, "T3", "M3", 3000L, false)
            )
        )

        assertEquals(3, repository.observeUnreadCount().first())

        repository.markAllAsRead()

        assertEquals(0, repository.observeUnreadCount().first())
        val all = repository.getAllNotifications()
        assertTrue(all.all { it.isRead })
    }

    @Test
    fun `test deduplication ignores duplicate insert and preserves read state`() = runBlocking {
        val original = LifeOsNotification(
            id = "stable_task_123",
            category = NotificationCategory.PERSONAL,
            title = "Original Title",
            message = "Original Message",
            timestampEpochMillis = 5000L,
            isRead = false
        )
        repository.addNotification(original)
        assertEquals(1, repository.observeUnreadCount().first())

        // Mark as read
        repository.markAsRead("stable_task_123")
        assertEquals(0, repository.observeUnreadCount().first())

        // Attempt duplicate insert (e.g. from event replay or recomposition)
        val duplicateAttempt = LifeOsNotification(
            id = "stable_task_123",
            category = NotificationCategory.PERSONAL,
            title = "Duplicate Attempt",
            message = "Duplicate Message",
            timestampEpochMillis = 6000L,
            isRead = false
        )
        repository.addNotification(duplicateAttempt)

        // Count should still be 1 total, and isRead must remain true!
        val all = repository.getAllNotifications()
        assertEquals(1, all.size)
        assertTrue(all[0].isRead)
        assertEquals("Original Title", all[0].title)
    }

    @Test
    fun `test delete notification by id`() = runBlocking {
        repository.addNotification(
            LifeOsNotification("n-del", NotificationCategory.PERSONAL, "To delete", "Msg", 1000L, false)
        )
        assertEquals(1, repository.getAllNotifications().size)

        repository.deleteById("n-del")
        assertEquals(0, repository.getAllNotifications().size)
        assertNull(repository.getNotificationById("n-del"))
    }
}

/**
 * In-memory test double for [NotificationDao].
 */
private class FakeNotificationDao : NotificationDao {
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

    override suspend fun getUnreadCount(): Int {
        return storage.values.count { !it.isRead }
    }

    override suspend fun getNotificationById(id: String): NotificationEntity? {
        return storage[id]
    }

    override suspend fun insert(notification: NotificationEntity): Long {
        if (!storage.containsKey(notification.id)) {
            storage[notification.id] = notification
            emit()
            return 1L
        }
        return -1L // OnConflictStrategy.IGNORE
    }

    override suspend fun insertAll(notifications: List<NotificationEntity>): List<Long> {
        val results = mutableListOf<Long>()
        for (n in notifications) {
            results.add(insert(n))
        }
        return results
    }

    override suspend fun markAsRead(id: String): Int {
        val existing = storage[id] ?: return 0
        storage[id] = existing.copy(isRead = true)
        emit()
        return 1
    }

    override suspend fun markAllAsRead(): Int {
        var count = 0
        storage.keys.toList().forEach { key ->
            val existing = storage[key]!!
            if (!existing.isRead) {
                storage[key] = existing.copy(isRead = true)
                count++
            }
        }
        emit()
        return count
    }

    override suspend fun deleteById(id: String): Int {
        val removed = storage.remove(id) != null
        if (removed) emit()
        return if (removed) 1 else 0
    }

    override suspend fun clearAll(): Int {
        val count = storage.size
        storage.clear()
        emit()
        return count
    }
}
