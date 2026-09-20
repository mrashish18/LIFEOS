package com.mrashish18.lifeos.domain.repository

import com.mrashish18.lifeos.domain.model.ThemeMode
import com.mrashish18.lifeos.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    val settingsFlow: Flow<UserSettings>
    suspend fun getSettings(): UserSettings
    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun updateAutoDayNight(enabled: Boolean, dayStart: String = "06:00", nightStart: String = "18:00")
    suspend fun updateInAppNotifications(enabled: Boolean)
    suspend fun toggleNotificationCategory(category: String, enabled: Boolean)
}
