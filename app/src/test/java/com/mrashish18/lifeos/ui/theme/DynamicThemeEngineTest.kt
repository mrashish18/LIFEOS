package com.mrashish18.lifeos.ui.theme

import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.UserAvailability
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DynamicThemeEngineTest {

    @Test
    fun testResolveModePrioritizesEmergency() {
        val mode = DynamicThemeEngine.resolveMode(
            networkState = NetworkState.DISCONNECTED,
            hasCriticalEmergency = true,
            isSyncing = true,
            isFocusSession = true,
            hasWarnings = true,
            userAvailability = UserAvailability.FOCUS
        )
        assertEquals(UiStateMode.EMERGENCY, mode)
    }

    @Test
    fun testResolveModeSyncing() {
        val mode = DynamicThemeEngine.resolveMode(
            networkState = NetworkState.CONNECTED_WIFI,
            hasCriticalEmergency = false,
            isSyncing = true
        )
        assertEquals(UiStateMode.SYNCING, mode)
    }

    @Test
    fun testResolveModeOffline() {
        val mode = DynamicThemeEngine.resolveMode(
            networkState = NetworkState.DISCONNECTED,
            hasCriticalEmergency = false,
            isSyncing = false
        )
        assertEquals(UiStateMode.OFFLINE, mode)
    }

    @Test
    fun testResolveModeFocus() {
        val mode1 = DynamicThemeEngine.resolveMode(
            networkState = NetworkState.CONNECTED_WIFI,
            isFocusSession = true
        )
        assertEquals(UiStateMode.FOCUS, mode1)

        val mode2 = DynamicThemeEngine.resolveMode(
            networkState = NetworkState.CONNECTED_CELLULAR,
            userAvailability = UserAvailability.FOCUS
        )
        assertEquals(UiStateMode.FOCUS, mode2)
    }

    @Test
    fun testResolveModeWarning() {
        val mode = DynamicThemeEngine.resolveMode(
            networkState = NetworkState.CONNECTED_WIFI,
            hasWarnings = true
        )
        assertEquals(UiStateMode.WARNING, mode)
    }

    @Test
    fun testResolveModeNormal() {
        val mode = DynamicThemeEngine.resolveMode(
            networkState = NetworkState.CONNECTED_WIFI,
            hasCriticalEmergency = false,
            isSyncing = false,
            isFocusSession = false,
            hasWarnings = false,
            userAvailability = UserAvailability.AVAILABLE
        )
        assertEquals(UiStateMode.NORMAL, mode)
    }

    @Test
    fun testColorsForMode() {
        UiStateMode.values().forEach { mode ->
            val colors = DynamicThemeEngine.colorsForMode(mode)
            assertNotNull(colors)
            assertNotNull(colors.primary)
            assertNotNull(colors.secondary)
            assertNotNull(colors.accent)
            assertNotNull(colors.surface)
            assertNotNull(colors.textPrimary)
            assertNotNull(colors.textSecondary)

            if (mode == UiStateMode.EMERGENCY) {
                assertTrue(colors.isEmergency)
            } else {
                assertFalse(colors.isEmergency)
            }
        }
    }
}
