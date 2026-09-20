package com.mrashish18.lifeos.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mrashish18.lifeos.domain.model.ThemeMode
import com.mrashish18.lifeos.domain.model.UserSettings
import com.mrashish18.lifeos.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lifeos_settings")

class DataStoreUserSettingsRepository(
    private val context: Context
) : UserSettingsRepository {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val AUTO_DAY_NIGHT = booleanPreferencesKey("auto_day_night")
        val DAY_START = stringPreferencesKey("day_start")
        val NIGHT_START = stringPreferencesKey("night_start")
        val IN_APP_NOTIFS = booleanPreferencesKey("in_app_notifs")
        val NOTIF_CATEGORIES = stringSetPreferencesKey("notif_categories")
    }

    override val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.LIGHT.name
        val themeMode = try {
            ThemeMode.valueOf(themeModeStr)
        } catch (_: Exception) {
            ThemeMode.LIGHT
        }
        val autoDayNight = preferences[PreferencesKeys.AUTO_DAY_NIGHT] ?: false
        val dayStart = preferences[PreferencesKeys.DAY_START] ?: "06:00"
        val nightStart = preferences[PreferencesKeys.NIGHT_START] ?: "18:00"
        val inAppNotifs = preferences[PreferencesKeys.IN_APP_NOTIFS] ?: true
        val categories = preferences[PreferencesKeys.NOTIF_CATEGORIES] ?: setOf(
            "PERSONAL", "TRUTH", "MESH", "LEARNING", "EMERGENCY"
        )

        UserSettings(
            themeMode = themeMode,
            autoDayNightEnabled = autoDayNight,
            dayStartTime = dayStart,
            nightStartTime = nightStart,
            inAppNotificationsEnabled = inAppNotifs,
            enabledNotificationCategories = categories
        )
    }

    override suspend fun getSettings(): UserSettings = settingsFlow.first()

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }

    override suspend fun updateAutoDayNight(enabled: Boolean, dayStart: String, nightStart: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_DAY_NIGHT] = enabled
            preferences[PreferencesKeys.DAY_START] = dayStart
            preferences[PreferencesKeys.NIGHT_START] = nightStart
        }
    }

    override suspend fun updateInAppNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IN_APP_NOTIFS] = enabled
        }
    }

    override suspend fun toggleNotificationCategory(category: String, enabled: Boolean) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.NOTIF_CATEGORIES]?.toMutableSet()
                ?: mutableSetOf("PERSONAL", "TRUTH", "MESH", "LEARNING", "EMERGENCY")
            if (enabled) {
                current.add(category)
            } else {
                current.remove(category)
            }
            preferences[PreferencesKeys.NOTIF_CATEGORIES] = current
        }
    }
}
