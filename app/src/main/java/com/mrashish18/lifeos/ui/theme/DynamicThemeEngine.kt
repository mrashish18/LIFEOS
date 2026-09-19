package com.mrashish18.lifeos.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.UserAvailability

enum class UiStateMode {
    NORMAL,
    FOCUS,
    LEARNING,
    TRUST,
    SUCCESS,
    WARNING,
    OFFLINE,
    SYNCING,
    EMERGENCY
}

data class LifeOsSemanticColors(
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val info: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val surfaceVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val outline: Color,
    val bannerBackground: Color,
    val bannerContent: Color,
    val isEmergency: Boolean = false
)

object DynamicThemeEngine {

    fun resolveMode(
        networkState: NetworkState,
        hasCriticalEmergency: Boolean = false,
        isSyncing: Boolean = false,
        isFocusSession: Boolean = false,
        hasWarnings: Boolean = false,
        userAvailability: UserAvailability = UserAvailability.AVAILABLE
    ): UiStateMode {
        return when {
            hasCriticalEmergency -> UiStateMode.EMERGENCY
            isSyncing -> UiStateMode.SYNCING
            networkState == NetworkState.DISCONNECTED -> UiStateMode.OFFLINE
            isFocusSession || userAvailability == UserAvailability.FOCUS -> UiStateMode.FOCUS
            hasWarnings -> UiStateMode.WARNING
            else -> UiStateMode.NORMAL
        }
    }

    fun colorsForMode(mode: UiStateMode): LifeOsSemanticColors {
        return when (mode) {
            UiStateMode.EMERGENCY -> LifeOsSemanticColors(
                primary = LifeOsRed700,
                secondary = LifeOsAmber700,
                accent = LifeOsRed500,
                success = LifeOsGreen600,
                warning = LifeOsAmber600,
                error = LifeOsRed700,
                info = LifeOsIndigo600,
                surface = LifeOsWhite,
                surfaceElevated = LifeOsRed50,
                surfaceVariant = LifeOsRed50.copy(alpha = 0.5f),
                textPrimary = LifeOsSlate950,
                textSecondary = LifeOsSlate700,
                outline = LifeOsRed100,
                bannerBackground = LifeOsRed50,
                bannerContent = LifeOsRed700,
                isEmergency = true
            )
            UiStateMode.OFFLINE -> LifeOsSemanticColors(
                primary = LifeOsAmber700,
                secondary = LifeOsIndigo600,
                accent = LifeOsAmber600,
                success = LifeOsGreen600,
                warning = LifeOsAmber600,
                error = LifeOsRed600,
                info = LifeOsTeal600,
                surface = LifeOsWhite,
                surfaceElevated = LifeOsAmber50,
                surfaceVariant = LifeOsAmber50.copy(alpha = 0.4f),
                textPrimary = LifeOsSlate900,
                textSecondary = LifeOsSlate700,
                outline = LifeOsAmber100,
                bannerBackground = LifeOsAmber50,
                bannerContent = LifeOsAmber700,
                isEmergency = false
            )
            UiStateMode.SYNCING -> LifeOsSemanticColors(
                primary = LifeOsBlue600,
                secondary = LifeOsTeal600,
                accent = LifeOsBlue400,
                success = LifeOsGreen600,
                warning = LifeOsAmber600,
                error = LifeOsRed600,
                info = LifeOsBlue700,
                surface = LifeOsWhite,
                surfaceElevated = LifeOsBlue50,
                surfaceVariant = LifeOsBlue50.copy(alpha = 0.5f),
                textPrimary = LifeOsSlate900,
                textSecondary = LifeOsSlate700,
                outline = LifeOsBlue100,
                bannerBackground = LifeOsBlue50,
                bannerContent = LifeOsBlue700,
                isEmergency = false
            )
            UiStateMode.FOCUS -> LifeOsSemanticColors(
                primary = LifeOsIndigo700,
                secondary = LifeOsBlue600,
                accent = LifeOsIndigo500,
                success = LifeOsGreen600,
                warning = LifeOsAmber600,
                error = LifeOsRed600,
                info = LifeOsTeal600,
                surface = LifeOsWhite,
                surfaceElevated = LifeOsIndigo50,
                surfaceVariant = LifeOsIndigo50.copy(alpha = 0.6f),
                textPrimary = LifeOsSlate900,
                textSecondary = LifeOsSlate700,
                outline = LifeOsIndigo100,
                bannerBackground = LifeOsIndigo50,
                bannerContent = LifeOsIndigo700,
                isEmergency = false
            )
            UiStateMode.LEARNING -> LifeOsSemanticColors(
                primary = LifeOsColors.accentCyan,
                secondary = LifeOsColors.primary,
                accent = LifeOsBlue500,
                success = LifeOsGreen600,
                warning = LifeOsAmber600,
                error = LifeOsRed600,
                info = LifeOsColors.accentCyan,
                surface = LifeOsWhite,
                surfaceElevated = LifeOsBlue50,
                surfaceVariant = LifeOsBlue50.copy(alpha = 0.5f),
                textPrimary = LifeOsSlate900,
                textSecondary = LifeOsSlate700,
                outline = LifeOsBlue100,
                bannerBackground = LifeOsBlue50,
                bannerContent = LifeOsBlue800,
                isEmergency = false
            )
            UiStateMode.TRUST -> LifeOsSemanticColors(
                primary = LifeOsBlue600,
                secondary = LifeOsTeal600,
                accent = LifeOsTeal500,
                success = LifeOsGreen600,
                warning = LifeOsAmber600,
                error = LifeOsRed600,
                info = LifeOsTeal600,
                surface = LifeOsWhite,
                surfaceElevated = LifeOsTeal50,
                surfaceVariant = LifeOsTeal50.copy(alpha = 0.5f),
                textPrimary = LifeOsSlate900,
                textSecondary = LifeOsSlate700,
                outline = LifeOsTeal100,
                bannerBackground = LifeOsTeal50,
                bannerContent = LifeOsTeal700,
                isEmergency = false
            )
            UiStateMode.SUCCESS -> LifeOsSemanticColors(
                primary = LifeOsGreen700,
                secondary = LifeOsIndigo600,
                accent = LifeOsGreen500,
                success = LifeOsGreen600,
                warning = LifeOsAmber600,
                error = LifeOsRed600,
                info = LifeOsTeal600,
                surface = LifeOsWhite,
                surfaceElevated = LifeOsGreen50,
                surfaceVariant = LifeOsGreen50.copy(alpha = 0.5f),
                textPrimary = LifeOsSlate900,
                textSecondary = LifeOsSlate700,
                outline = LifeOsGreen100,
                bannerBackground = LifeOsGreen50,
                bannerContent = LifeOsGreen700,
                isEmergency = false
            )
            UiStateMode.WARNING -> LifeOsSemanticColors(
                primary = LifeOsAmber700,
                secondary = LifeOsIndigo600,
                accent = LifeOsAmber500,
                success = LifeOsGreen600,
                warning = LifeOsAmber600,
                error = LifeOsRed600,
                info = LifeOsTeal600,
                surface = LifeOsWhite,
                surfaceElevated = LifeOsAmber50,
                surfaceVariant = LifeOsAmber50.copy(alpha = 0.5f),
                textPrimary = LifeOsSlate900,
                textSecondary = LifeOsSlate700,
                outline = LifeOsAmber100,
                bannerBackground = LifeOsAmber50,
                bannerContent = LifeOsAmber700,
                isEmergency = false
            )
            UiStateMode.NORMAL -> LifeOsSemanticColors(
                primary = LifeOsBlue700,
                secondary = LifeOsIndigo600,
                accent = LifeOsIndigo700,
                success = LifeOsGreen600,
                warning = LifeOsAmber600,
                error = LifeOsRed600,
                info = LifeOsTeal600,
                surface = LifeOsWhite,
                surfaceElevated = LifeOsSlate50,
                surfaceVariant = LifeOsSlate100,
                textPrimary = LifeOsSlate900,
                textSecondary = LifeOsSlate700,
                outline = LifeOsSlate200,
                bannerBackground = LifeOsIndigo50,
                bannerContent = LifeOsIndigo700,
                isEmergency = false
            )
        }
    }
}

val LocalLifeOsSemanticColors = compositionLocalOf {
    DynamicThemeEngine.colorsForMode(UiStateMode.NORMAL)
}

object LifeOsTheme {
    val colors: LifeOsSemanticColors
        @Composable
        @ReadOnlyComposable
        get() = LocalLifeOsSemanticColors.current
}
