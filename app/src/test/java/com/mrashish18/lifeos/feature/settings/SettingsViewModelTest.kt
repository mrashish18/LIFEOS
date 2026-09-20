package com.mrashish18.lifeos.feature.settings

import com.mrashish18.lifeos.domain.model.ThemeMode
import com.mrashish18.lifeos.domain.model.UserSettings
import com.mrashish18.lifeos.domain.repository.UserSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {

    private lateinit var fakeRepository: FakeUserSettingsRepository
    private lateinit var viewModel: SettingsViewModel
    private val testScope = CoroutineScope(Dispatchers.Unconfined)

    @Before
    fun setUp() {
        fakeRepository = FakeUserSettingsRepository()
        viewModel = SettingsViewModel(
            userSettingsRepository = fakeRepository,
            database = null,
            dispatcherProvider = TestDispatcherProvider(),
            coroutineScope = testScope
        )
    }

    @Test
    fun `initial state reflects default settings and no active modal`() {
        val settings = viewModel.settings.value
        assertEquals(ThemeMode.LIGHT, settings.themeMode)
        assertFalse(settings.autoDayNightEnabled)
        assertEquals(SettingsModal.NONE, viewModel.activeModal.value)
        assertFalse(viewModel.isResetConfirmationVisible.value)
        assertNull(viewModel.resetSuccessMessage.value)
    }

    @Test
    fun `openModal and closeModal transitions active modal state`() {
        viewModel.openModal(SettingsModal.APPEARANCE)
        assertEquals(SettingsModal.APPEARANCE, viewModel.activeModal.value)

        viewModel.openModal(SettingsModal.FAQ)
        assertEquals(SettingsModal.FAQ, viewModel.activeModal.value)

        viewModel.closeModal()
        assertEquals(SettingsModal.NONE, viewModel.activeModal.value)
    }

    @Test
    fun `setThemeMode updates theme preference in repository`() = runBlocking {
        viewModel.setThemeMode(ThemeMode.DARK)
        assertEquals(ThemeMode.DARK, fakeRepository.getSettings().themeMode)

        viewModel.setThemeMode(ThemeMode.SYSTEM)
        assertEquals(ThemeMode.SYSTEM, fakeRepository.getSettings().themeMode)
    }

    @Test
    fun `setAutoDayNight updates auto day-night preference in repository`() = runBlocking {
        viewModel.setAutoDayNight(true, "07:00", "19:00")
        val updated = fakeRepository.getSettings()
        assertTrue(updated.autoDayNightEnabled)
        assertEquals("07:00", updated.dayStartTime)
        assertEquals("19:00", updated.nightStartTime)
    }

    @Test
    fun `setInAppNotifications updates in-app notification master switch`() = runBlocking {
        viewModel.setInAppNotifications(false)
        assertFalse(fakeRepository.getSettings().inAppNotificationsEnabled)

        viewModel.setInAppNotifications(true)
        assertTrue(fakeRepository.getSettings().inAppNotificationsEnabled)
    }

    @Test
    fun `toggleNotificationCategory enables and disables category`() = runBlocking {
        viewModel.toggleNotificationCategory("EMERGENCY", false)
        assertFalse(fakeRepository.getSettings().enabledNotificationCategories.contains("EMERGENCY"))

        viewModel.toggleNotificationCategory("EMERGENCY", true)
        assertTrue(fakeRepository.getSettings().enabledNotificationCategories.contains("EMERGENCY"))
    }

    @Test
    fun `showResetConfirmation and performResetData update confirmation state and success message`() = runBlocking {
        viewModel.showResetConfirmation(true)
        assertTrue(viewModel.isResetConfirmationVisible.value)

        viewModel.performResetData()
        assertFalse(viewModel.isResetConfirmationVisible.value)
        assertEquals("LIFEOS user data cleared successfully", viewModel.resetSuccessMessage.value)

        viewModel.clearResetSuccessMessage()
        assertNull(viewModel.resetSuccessMessage.value)
    }
}

private class FakeUserSettingsRepository : UserSettingsRepository {
    private val state = MutableStateFlow(UserSettings())

    override val settingsFlow: Flow<UserSettings> = state

    override suspend fun getSettings(): UserSettings = state.value

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        state.value = state.value.copy(themeMode = themeMode)
    }

    override suspend fun updateAutoDayNight(enabled: Boolean, dayStart: String, nightStart: String) {
        state.value = state.value.copy(
            autoDayNightEnabled = enabled,
            dayStartTime = dayStart,
            nightStartTime = nightStart
        )
    }

    override suspend fun updateInAppNotifications(enabled: Boolean) {
        state.value = state.value.copy(inAppNotificationsEnabled = enabled)
    }

    override suspend fun toggleNotificationCategory(category: String, enabled: Boolean) {
        val current = state.value.enabledNotificationCategories.toMutableSet()
        if (enabled) current.add(category) else current.remove(category)
        state.value = state.value.copy(enabledNotificationCategories = current)
    }
}

private class TestDispatcherProvider : com.mrashish18.lifeos.core.common.DispatcherProvider {
    override val main = Dispatchers.Unconfined
    override val io = Dispatchers.Unconfined
    override val default = Dispatchers.Unconfined
    override val unconfined = Dispatchers.Unconfined
}
