package com.mrashish18.lifeos.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

class UserSettingsTest {

    @Test
    fun defaultSettings_hasSensibleDefaults() {
        val settings = UserSettings()
        assertEquals(ThemeMode.LIGHT, settings.themeMode)
        assertFalse(settings.autoDayNightEnabled)
        assertEquals("06:00", settings.dayStartTime)
        assertEquals("18:00", settings.nightStartTime)
        assertTrue(settings.inAppNotificationsEnabled)
        assertTrue(settings.enabledNotificationCategories.contains("PERSONAL"))
        assertTrue(settings.enabledNotificationCategories.contains("TRUTH"))
        assertTrue(settings.enabledNotificationCategories.contains("MESH"))
        assertTrue(settings.enabledNotificationCategories.contains("LEARNING"))
        assertTrue(settings.enabledNotificationCategories.contains("EMERGENCY"))
    }

    @Test
    fun shouldUseDarkTheme_lightMode_alwaysReturnsFalse() {
        val settings = UserSettings(themeMode = ThemeMode.LIGHT)
        assertFalse(settings.shouldUseDarkTheme(isSystemInDarkTheme = true))
        assertFalse(settings.shouldUseDarkTheme(isSystemInDarkTheme = false))
    }

    @Test
    fun shouldUseDarkTheme_darkMode_alwaysReturnsTrue() {
        val settings = UserSettings(themeMode = ThemeMode.DARK)
        assertTrue(settings.shouldUseDarkTheme(isSystemInDarkTheme = true))
        assertTrue(settings.shouldUseDarkTheme(isSystemInDarkTheme = false))
    }

    @Test
    fun shouldUseDarkTheme_systemMode_followsSystem() {
        val settings = UserSettings(themeMode = ThemeMode.SYSTEM)
        assertTrue(settings.shouldUseDarkTheme(isSystemInDarkTheme = true))
        assertFalse(settings.shouldUseDarkTheme(isSystemInDarkTheme = false))
    }

    @Test
    fun shouldUseDarkTheme_autoDayNight_duringDay_returnsFalse() {
        val settings = UserSettings(
            autoDayNightEnabled = true,
            dayStartTime = "06:00",
            nightStartTime = "18:00"
        )
        // 12:00 PM is day time -> light theme (false)
        assertFalse(settings.shouldUseDarkTheme(isSystemInDarkTheme = true, currentTime = LocalTime.of(12, 0)))
        assertFalse(settings.shouldUseDarkTheme(isSystemInDarkTheme = false, currentTime = LocalTime.of(6, 0)))
        assertFalse(settings.shouldUseDarkTheme(isSystemInDarkTheme = false, currentTime = LocalTime.of(17, 59)))
    }

    @Test
    fun shouldUseDarkTheme_autoDayNight_duringNight_returnsTrue() {
        val settings = UserSettings(
            autoDayNightEnabled = true,
            dayStartTime = "06:00",
            nightStartTime = "18:00"
        )
        // 21:00 PM is night time -> dark theme (true)
        assertTrue(settings.shouldUseDarkTheme(isSystemInDarkTheme = false, currentTime = LocalTime.of(21, 0)))
        assertTrue(settings.shouldUseDarkTheme(isSystemInDarkTheme = false, currentTime = LocalTime.of(18, 0)))
        assertTrue(settings.shouldUseDarkTheme(isSystemInDarkTheme = false, currentTime = LocalTime.of(4, 0)))
    }
}
