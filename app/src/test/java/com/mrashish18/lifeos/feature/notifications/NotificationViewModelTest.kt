package com.mrashish18.lifeos.feature.notifications

import com.mrashish18.lifeos.core.model.LifeOsNotification
import com.mrashish18.lifeos.core.model.NotificationCategory
import com.mrashish18.lifeos.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NotificationViewModelTest {

    private lateinit var fakeRepository: FakeNotificationRepository
    private lateinit var viewModel: NotificationViewModel
    private val testScope = CoroutineScope(Dispatchers.Unconfined)

    @Before
    fun setUp() {
        fakeRepository = FakeNotificationRepository()
        viewModel = NotificationViewModel(fakeRepository, testScope)
    }

    @Test
    fun `initial state has empty notifications, zero unread, and sheet hidden`() {
        val state = viewModel.uiState.value
        assertEquals(0, state.notifications.size)
        assertEquals(0, state.unreadCount)
        assertNull(state.selectedCategory)
        assertFalse(state.isSheetVisible)
    }

    @Test
    fun `open and close notification center updates sheet visibility`() {
        viewModel.openNotificationCenter()
        assertTrue(viewModel.uiState.value.isSheetVisible)

        viewModel.closeNotificationCenter()
        assertFalse(viewModel.uiState.value.isSheetVisible)
    }

    @Test
    fun `unread count reflects unread notifications from repository`() = runBlocking {
        fakeRepository.emitNotifications(
            listOf(
                LifeOsNotification("n1", NotificationCategory.PERSONAL, "Task", "Msg", 1000L, isRead = false),
                LifeOsNotification("n2", NotificationCategory.TRUTH, "Truth", "Msg", 2000L, isRead = false),
                LifeOsNotification("n3", NotificationCategory.MESH, "Mesh", "Msg", 3000L, isRead = true)
            )
        )

        val state = viewModel.uiState.value
        assertEquals(3, state.notifications.size)
        assertEquals(2, state.unreadCount)
    }

    @Test
    fun `category filter correctly filters notifications`() = runBlocking {
        fakeRepository.emitNotifications(
            listOf(
                LifeOsNotification("n1", NotificationCategory.PERSONAL, "Personal Task", "Msg", 1000L),
                LifeOsNotification("n2", NotificationCategory.TRUTH, "Truth Investigation", "Msg", 2000L),
                LifeOsNotification("n3", NotificationCategory.PERSONAL, "Another Task", "Msg", 3000L)
            )
        )

        // Filter by PERSONAL
        viewModel.setCategoryFilter(NotificationCategory.PERSONAL)
        val statePersonal = viewModel.uiState.value
        assertEquals(NotificationCategory.PERSONAL, statePersonal.selectedCategory)
        assertEquals(3, statePersonal.notifications.size)
        assertEquals(2, statePersonal.filteredNotifications.size)
        assertTrue(statePersonal.filteredNotifications.all { it.category == NotificationCategory.PERSONAL })

        // Filter by TRUTH
        viewModel.setCategoryFilter(NotificationCategory.TRUTH)
        val stateTruth = viewModel.uiState.value
        assertEquals(1, stateTruth.filteredNotifications.size)
        assertEquals(NotificationCategory.TRUTH, stateTruth.filteredNotifications[0].category)

        // Reset to All
        viewModel.setCategoryFilter(null)
        assertEquals(3, viewModel.uiState.value.filteredNotifications.size)
    }

    @Test
    fun `mark as read updates repository and unread count`() = runBlocking {
        fakeRepository.emitNotifications(
            listOf(
                LifeOsNotification("n1", NotificationCategory.PERSONAL, "Task 1", "Msg", 1000L, isRead = false)
            )
        )
        assertEquals(1, viewModel.uiState.value.unreadCount)

        viewModel.markAsRead("n1")

        assertEquals(0, viewModel.uiState.value.unreadCount)
        assertTrue(viewModel.uiState.value.notifications[0].isRead)
    }

    @Test
    fun `mark all as read clears unread count`() = runBlocking {
        fakeRepository.emitNotifications(
            listOf(
                LifeOsNotification("n1", NotificationCategory.PERSONAL, "T1", "M1", 1000L, isRead = false),
                LifeOsNotification("n2", NotificationCategory.EMERGENCY, "T2", "M2", 2000L, isRead = false)
            )
        )
        assertEquals(2, viewModel.uiState.value.unreadCount)

        viewModel.markAllAsRead()

        assertEquals(0, viewModel.uiState.value.unreadCount)
        assertTrue(viewModel.uiState.value.notifications.all { it.isRead })
    }
}

/**
 * Fake in-memory test double for [NotificationRepository].
 */
private class FakeNotificationRepository : NotificationRepository {
    private val notificationsFlow = MutableStateFlow<List<LifeOsNotification>>(emptyList())

    fun emitNotifications(list: List<LifeOsNotification>) {
        notificationsFlow.value = list
    }

    override fun observeNotifications(): Flow<List<LifeOsNotification>> = notificationsFlow

    override fun observeUnreadCount(): Flow<Int> = notificationsFlow.map { list ->
        list.count { !it.isRead }
    }

    override suspend fun getAllNotifications(): List<LifeOsNotification> = notificationsFlow.value

    override suspend fun getUnreadCount(): Int = notificationsFlow.value.count { !it.isRead }

    override suspend fun addNotification(notification: LifeOsNotification) {
        val current = notificationsFlow.value.toMutableList()
        current.removeAll { it.id == notification.id }
        current.add(0, notification)
        notificationsFlow.value = current
    }

    override suspend fun addNotifications(notifications: List<LifeOsNotification>) {
        val current = notificationsFlow.value.toMutableList()
        notifications.forEach { n ->
            if (current.none { it.id == n.id }) {
                current.add(n)
            }
        }
        notificationsFlow.value = current
    }

    override suspend fun markAsRead(id: String) {
        val current = notificationsFlow.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
        notificationsFlow.value = current
    }

    override suspend fun markAllAsRead() {
        val current = notificationsFlow.value.map { it.copy(isRead = true) }
        notificationsFlow.value = current
    }

    override suspend fun deleteById(id: String) {
        notificationsFlow.value = notificationsFlow.value.filter { it.id != id }
    }

    override suspend fun getNotificationById(id: String): LifeOsNotification? {
        return notificationsFlow.value.firstOrNull { it.id == id }
    }
}
