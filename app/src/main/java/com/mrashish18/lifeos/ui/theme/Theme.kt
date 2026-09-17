package com.mrashish18.lifeos.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = LifeOsBlue700,
    onPrimary = LifeOsWhite,
    primaryContainer = LifeOsBlue50,
    onPrimaryContainer = LifeOsBlue800,
    secondary = LifeOsIndigo600,
    onSecondary = LifeOsWhite,
    secondaryContainer = LifeOsIndigo50,
    onSecondaryContainer = LifeOsIndigo700,
    tertiary = LifeOsTeal600,
    onTertiary = LifeOsWhite,
    tertiaryContainer = LifeOsTeal50,
    onTertiaryContainer = LifeOsTeal700,
    background = LifeOsSlate50,
    onBackground = LifeOsSlate900,
    surface = LifeOsWhite,
    onSurface = LifeOsSlate900,
    surfaceVariant = LifeOsSlate100,
    onSurfaceVariant = LifeOsSlate600,
    outline = LifeOsSlate300,
    outlineVariant = LifeOsSlate200,
    error = LifeOsRed600,
    onError = LifeOsWhite,
    errorContainer = LifeOsRed50,
    onErrorContainer = LifeOsRed700
)

private val DarkColorScheme = darkColorScheme(
    primary = LifeOsBlue400,
    onPrimary = LifeOsSlate950,
    primaryContainer = LifeOsBlue800,
    onPrimaryContainer = LifeOsBlue100,
    secondary = LifeOsIndigo500,
    onSecondary = LifeOsWhite,
    secondaryContainer = LifeOsIndigo700,
    onSecondaryContainer = LifeOsIndigo100,
    tertiary = LifeOsTeal500,
    onTertiary = LifeOsSlate950,
    tertiaryContainer = LifeOsTeal700,
    onTertiaryContainer = LifeOsTeal100,
    background = LifeOsSlate950,
    onBackground = LifeOsSlate50,
    surface = LifeOsSlate900,
    onSurface = LifeOsSlate50,
    surfaceVariant = LifeOsSlate800,
    onSurfaceVariant = LifeOsSlate400,
    outline = LifeOsSlate700,
    outlineVariant = LifeOsSlate800,
    error = LifeOsRed500,
    onError = LifeOsSlate950,
    errorContainer = LifeOsRed700,
    onErrorContainer = LifeOsRed100
)

@Composable
fun LIFEOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Default to false so our brand design system is reliably presented across all Android versions
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}