package com.mrashish18.lifeos.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mrashish18.lifeos.core.common.DefaultDispatcherProvider
import com.mrashish18.lifeos.core.common.DispatcherProvider
import com.mrashish18.lifeos.data.local.LifeOsDatabase
import com.mrashish18.lifeos.domain.model.ThemeMode
import com.mrashish18.lifeos.domain.model.UserSettings
import com.mrashish18.lifeos.domain.repository.UserSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SettingsModal {
    NONE,
    HOW_TO_USE,
    FAQ,
    TERMS,
    PRIVACY,
    ABOUT,
    APPEARANCE,
    NOTIFICATIONS,
    DATA_STORAGE,
    WHATS_NEW,
    FEEDBACK
}

data class DataStorageCounts(
    val tasksCount: Int = 0,
    val goalsCount: Int = 5,
    val behaviorEventsCount: Int = 0,
    val investigationsCount: Int = 0,
    val emergencyMessagesCount: Int = 0,
    val notificationsCount: Int = 0
)

class SettingsViewModel(
    private val userSettingsRepository: UserSettingsRepository,
    private val database: LifeOsDatabase? = null,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider(),
    coroutineScope: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope = coroutineScope ?: viewModelScope

    val settings: StateFlow<UserSettings> = userSettingsRepository.settingsFlow
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = UserSettings()
        )

    private val _activeModal = MutableStateFlow(SettingsModal.NONE)
    val activeModal: StateFlow<SettingsModal> = _activeModal.asStateFlow()

    private val _isResetConfirmationVisible = MutableStateFlow(false)
    val isResetConfirmationVisible: StateFlow<Boolean> = _isResetConfirmationVisible.asStateFlow()

    private val _resetSuccessMessage = MutableStateFlow<String?>(null)
    val resetSuccessMessage: StateFlow<String?> = _resetSuccessMessage.asStateFlow()

    val tasksCount: StateFlow<Int> = database?.taskDao()?.observeTotalTaskCount()
        ?.stateIn(scope, SharingStarted.WhileSubscribed(5000), 0)
        ?: MutableStateFlow(0)

    val behaviorEventsCount: StateFlow<Int> = database?.behaviorEventDao()?.observeTotalCount()
        ?.stateIn(scope, SharingStarted.WhileSubscribed(5000), 0)
        ?: MutableStateFlow(0)

    val investigationsCount: StateFlow<Int> = database?.investigationDao()?.observeTotalCount()
        ?.stateIn(scope, SharingStarted.WhileSubscribed(5000), 0)
        ?: MutableStateFlow(0)

    val emergencyMessagesCount: StateFlow<Int> = database?.emergencyMessageDao()?.observeTotalCount()
        ?.stateIn(scope, SharingStarted.WhileSubscribed(5000), 0)
        ?: MutableStateFlow(0)

    val notificationsCount: StateFlow<Int> = database?.notificationDao()?.observeTotalCount()
        ?.stateIn(scope, SharingStarted.WhileSubscribed(5000), 0)
        ?: MutableStateFlow(0)

    fun openModal(modal: SettingsModal) {
        _activeModal.value = modal
    }

    fun closeModal() {
        _activeModal.value = SettingsModal.NONE
        _isResetConfirmationVisible.value = false
        _resetSuccessMessage.value = null
    }

    fun setThemeMode(mode: ThemeMode) {
        scope.launch(dispatcherProvider.io) {
            userSettingsRepository.updateThemeMode(mode)
        }
    }

    fun setAutoDayNight(enabled: Boolean, dayStart: String = "06:00", nightStart: String = "18:00") {
        scope.launch(dispatcherProvider.io) {
            userSettingsRepository.updateAutoDayNight(enabled, dayStart, nightStart)
        }
    }

    fun setInAppNotifications(enabled: Boolean) {
        scope.launch(dispatcherProvider.io) {
            userSettingsRepository.updateInAppNotifications(enabled)
        }
    }

    fun toggleNotificationCategory(category: String, enabled: Boolean) {
        scope.launch(dispatcherProvider.io) {
            userSettingsRepository.toggleNotificationCategory(category, enabled)
        }
    }

    fun showResetConfirmation(show: Boolean) {
        _isResetConfirmationVisible.value = show
    }

    fun performResetData() {
        scope.launch(dispatcherProvider.io) {
            val db = database ?: run {
                _isResetConfirmationVisible.value = false
                _resetSuccessMessage.value = "LIFEOS user data cleared successfully"
                return@launch
            }
            db.taskDao().clearAll()
            db.behaviorEventDao().clearAll()
            db.investigationDao().clearAll()
            db.emergencyMessageDao().clearAll()
            db.notificationDao().clearAll()

            _isResetConfirmationVisible.value = false
            _resetSuccessMessage.value = "LIFEOS user data cleared successfully"
        }
    }

    fun clearResetSuccessMessage() {
        _resetSuccessMessage.value = null
    }

    class Factory(
        private val userSettingsRepository: UserSettingsRepository,
        private val database: LifeOsDatabase,
        private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(userSettingsRepository, database, dispatcherProvider) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
