package com.mrashish18.lifeos.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mrashish18.lifeos.core.model.LifeOsNotification
import com.mrashish18.lifeos.core.model.NotificationCategory
import com.mrashish18.lifeos.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for the Notification Center.
 */
data class NotificationUiState(
    val notifications: List<LifeOsNotification> = emptyList(),
    val filteredNotifications: List<LifeOsNotification> = emptyList(),
    val unreadCount: Int = 0,
    val selectedCategory: NotificationCategory? = null,
    val isSheetVisible: Boolean = false
)

/**
 * ViewModel managing notification state, read/unread status, and category filtering.
 */
class NotificationViewModel(
    private val notificationRepository: NotificationRepository,
    coroutineScope: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope = coroutineScope ?: viewModelScope

    private val _selectedCategory = MutableStateFlow<NotificationCategory?>(null)
    private val _isSheetVisible = MutableStateFlow(false)

    val uiState: StateFlow<NotificationUiState> = combine(
        notificationRepository.observeNotifications(),
        notificationRepository.observeUnreadCount(),
        _selectedCategory,
        _isSheetVisible
    ) { notifications, unreadCount, category, isSheetVisible ->
        val filtered = if (category == null) {
            notifications
        } else {
            notifications.filter { it.category == category }
        }
        NotificationUiState(
            notifications = notifications,
            filteredNotifications = filtered,
            unreadCount = unreadCount,
            selectedCategory = category,
            isSheetVisible = isSheetVisible
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = NotificationUiState()
    )

    fun openNotificationCenter() {
        _isSheetVisible.value = true
    }

    fun closeNotificationCenter() {
        _isSheetVisible.value = false
    }

    fun setCategoryFilter(category: NotificationCategory?) {
        _selectedCategory.value = category
    }

    fun markAsRead(id: String) {
        scope.launch {
            notificationRepository.markAsRead(id)
        }
    }

    fun markAllAsRead() {
        scope.launch {
            notificationRepository.markAllAsRead()
        }
    }

    fun deleteNotification(id: String) {
        scope.launch {
            notificationRepository.deleteById(id)
        }
    }

    class Factory(
        private val notificationRepository: NotificationRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
                return NotificationViewModel(notificationRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
