package com.mrashish18.lifeos.domain.model

import java.time.LocalTime

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val autoDayNightEnabled: Boolean = false,
    val dayStartTime: String = "06:00",
    val nightStartTime: String = "18:00",
    val inAppNotificationsEnabled: Boolean = true,
    val enabledNotificationCategories: Set<String> = setOf(
        "PERSONAL",
        "TRUTH",
        "MESH",
        "LEARNING",
        "EMERGENCY"
    )
) {
    /**
     * Resolves whether Dark Theme should be active.
     * Precedence:
     * - If [autoDayNightEnabled] is true, uses configured [dayStartTime] and [nightStartTime].
     * - Otherwise adheres to [themeMode] (LIGHT = false, DARK = true, SYSTEM = isSystemInDarkTheme).
     */
    fun shouldUseDarkTheme(
        isSystemInDarkTheme: Boolean,
        currentTime: LocalTime = LocalTime.now()
    ): Boolean {
        if (autoDayNightEnabled) {
            val dayStart = parseTime(dayStartTime, 6, 0)
            val nightStart = parseTime(nightStartTime, 18, 0)
            return if (dayStart < nightStart) {
                currentTime < dayStart || currentTime >= nightStart
            } else {
                currentTime >= nightStart && currentTime < dayStart
            }
        }
        return when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme
        }
    }

    private fun parseTime(timeStr: String, fallbackHour: Int, fallbackMinute: Int): LocalTime {
        return try {
            val parts = timeStr.split(":")
            LocalTime.of(parts[0].toInt(), parts[1].toInt())
        } catch (_: Exception) {
            LocalTime.of(fallbackHour, fallbackMinute)
        }
    }
}
