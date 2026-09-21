package com.mrashish18.lifeos.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ==========================================
// LIFEOS Brand Color Palette
// Personality: Intelligent, Calm, Trustworthy, Modern
// ==========================================

// Primary Brand: Deep Intelligent Blue / Indigo
val LifeOsNavy900 = Color(0xFF0F172A)
val LifeOsNavy800 = Color(0xFF1E293B)
val LifeOsBlue800 = Color(0xFF1E3A8A)
val LifeOsBlue700 = Color(0xFF1D4ED8)
val LifeOsBlue600 = Color(0xFF2563EB)
val LifeOsBlue500 = Color(0xFF3B82F6)
val LifeOsBlue400 = Color(0xFF60A5FA)
val LifeOsBlue100 = Color(0xFFDBEAFE)
val LifeOsBlue50 = Color(0xFFEFF6FF)

// Secondary Accent: Soft Lavender / Slate Blue
val LifeOsIndigo700 = Color(0xFF4338CA)
val LifeOsIndigo600 = Color(0xFF4F46E5)
val LifeOsIndigo500 = Color(0xFF6366F1)
val LifeOsIndigo100 = Color(0xFFE0E7FF)
val LifeOsIndigo50 = Color(0xFFEEF2FF)

// Neutral & Surface Scale (Cool Grays)
val LifeOsSlate950 = Color(0xFF0B0F19)
val LifeOsSlate900 = Color(0xFF0F172A)
val LifeOsSlate800 = Color(0xFF1E293B)
val LifeOsSlate700 = Color(0xFF334155)
val LifeOsSlate600 = Color(0xFF475569)
val LifeOsSlate500 = Color(0xFF64748B)
val LifeOsSlate400 = Color(0xFF94A3B8)
val LifeOsSlate300 = Color(0xFFCBD5E1)
val LifeOsSlate200 = Color(0xFFE2E8F0)
val LifeOsSlate100 = Color(0xFFF1F5F9)
val LifeOsSlate50 = Color(0xFFF8FAFC)
val LifeOsWhite = Color(0xFFFFFFFF)

// Semantic: Success (Verification, Completion)
val LifeOsGreen700 = Color(0xFF15803D)
val LifeOsGreen600 = Color(0xFF16A34A)
val LifeOsGreen500 = Color(0xFF22C55E)
val LifeOsGreen100 = Color(0xFFDCFCE7)
val LifeOsGreen50 = Color(0xFFF0FDF4)

// Semantic: Warning (Caution, Mixed Evidence, Medium Priority)
val LifeOsAmber700 = Color(0xFFB45309)
val LifeOsAmber600 = Color(0xFFD97706)
val LifeOsAmber500 = Color(0xFFF59E0B)
val LifeOsAmber100 = Color(0xFFFEF3C7)
val LifeOsAmber50 = Color(0xFFFFFBEB)

// Semantic: Danger / Contradicted (Urgent, Refuted Claims, Delete)
val LifeOsRed700 = Color(0xFFB91C1C)
val LifeOsRed600 = Color(0xFFDC2626)
val LifeOsRed500 = Color(0xFFEF4444)
val LifeOsRed100 = Color(0xFFFEE2E2)
val LifeOsRed50 = Color(0xFFFEF2F2)

// Semantic: Informational / Context Accent
val LifeOsTeal700 = Color(0xFF0F766E)
val LifeOsTeal600 = Color(0xFF0D9488)
val LifeOsTeal500 = Color(0xFF14B8A6)
val LifeOsTeal100 = Color(0xFFCCFBF1)
val LifeOsTeal50 = Color(0xFFF0FDFA)

// Restrained Electric Accent
val LifeOsElectricBlue = Color(0xFF2563EB)
val LifeOsElectricIndigo = Color(0xFF4F46E5)

// Selective Brand Gradients for Hero and Primary Focus CTAs
val LifeOsPrimaryGradient = androidx.compose.ui.graphics.Brush.horizontalGradient(
    listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
)
val LifeOsFocusBorderGradient = androidx.compose.ui.graphics.Brush.linearGradient(
    listOf(Color(0xFF3B82F6), Color(0xFF818CF8))
)
val LifeOsHeroSubtleGradient = androidx.compose.ui.graphics.Brush.verticalGradient(
    listOf(Color(0xFFF8FAFC), Color(0xFFEEF2FF).copy(alpha = 0.5f))
)

// ==========================================
// Central Semantic Design System Tokens
// ==========================================
object LifeOsColors {
    val primary = Color(0xFF4F46E5)
    val primaryStrong = Color(0xFF4338CA)
    val secondary = Color(0xFF7C3AED)
    val accentCyan = Color(0xFF06B6D4)
    val success = Color(0xFF10B981)
    val warning = Color(0xFFF59E0B)
    val error = Color(0xFFEF4444)
    val background = Color(0xFFF8FAFC)
    val surface = Color(0xFFFFFFFF)
    val surfaceSoft = Color(0xFFF1F5F9)
    val surfaceGlass = Color(0xF8FFFFFF)
    val border = Color(0xFFE2E8F0)
    val borderSubtle = Color(0xFFF1F5F9)
    val textPrimary = Color(0xFF0F172A)
    val textSecondary = Color(0xFF475569)
    val textMuted = Color(0xFF94A3B8)
}

object LifeOsGradients {
    val primary = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
    )
    val emergency = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(Color(0xFFEF4444), Color(0xFFF43F5E))
    )
    val focus = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(Color(0xFF2563EB), Color(0xFF4F46E5))
    )
    val trust = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(Color(0xFF2563EB), Color(0xFF06B6D4))
    )
    val success = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(Color(0xFF059669), Color(0xFF10B981))
    )
    val heroAtmosphere = androidx.compose.ui.graphics.Brush.verticalGradient(
        listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF312E81))
    )
    val cardSurface = androidx.compose.ui.graphics.Brush.verticalGradient(
        listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC))
    )
    val topAura = androidx.compose.ui.graphics.Brush.verticalGradient(
        listOf(Color(0xFFEEF2FF), Color(0xFFF8FAFC))
    )
}

/**
 * Theme-aware semantic surfaces shared by LIFEOS components. Keeping these
 * values here prevents light-only card and chip colors from leaking into the
 * dark experience while retaining intentional semantic accents.
 */
@Immutable
data class LifeOsSemanticPalette(
    val surfaceElevated: Color,
    val surfaceInput: Color,
    val border: Color,
    val borderSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primary: Color,
    val primaryStrong: Color,
    val success: Color,
    val truth: Color,
    val warning: Color,
    val danger: Color,
    val info: Color,
    val successSurface: Color,
    val warningSurface: Color,
    val dangerSurface: Color,
    val infoSurface: Color,
    val neutralSurface: Color
)

val LifeOsLightSemanticPalette = LifeOsSemanticPalette(
    surfaceElevated = Color(0xFFEDE9FE),
    surfaceInput = Color(0xFFF1F5F9),
    border = Color(0xFFE2E8F0),
    borderSubtle = Color(0xFFF1F5F9),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    textMuted = Color(0xFF64748B),
    primary = Color(0xFF4F46E5),
    primaryStrong = Color(0xFF4338CA),
    success = Color(0xFF10B981),
    truth = Color(0xFF059669),
    warning = Color(0xFFF59E0B),
    danger = Color(0xFFEF4444),
    info = Color(0xFF2563EB),
    successSurface = Color(0xFFF0FDF4),
    warningSurface = Color(0xFFFFFBEB),
    dangerSurface = Color(0xFFFEF2F2),
    infoSurface = Color(0xFFEFF6FF),
    neutralSurface = Color(0xFFF1F5F9)
)

val LifeOsDarkSemanticPalette = LifeOsSemanticPalette(
    surfaceElevated = Color(0xFF172033),
    surfaceInput = Color(0xFF1E293B),
    border = Color(0xFF334155),
    borderSubtle = Color(0xFF1E293B),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFFCBD5E1),
    textMuted = Color(0xFF94A3B8),
    primary = Color(0xFF6366F1),
    primaryStrong = Color(0xFF818CF8),
    success = Color(0xFF10B981),
    truth = Color(0xFF34D399),
    warning = Color(0xFFF59E0B),
    danger = Color(0xFFFB7185),
    info = Color(0xFF3B82F6),
    successSurface = Color(0xFF064E3B),
    warningSurface = Color(0xFF451A03),
    dangerSurface = Color(0xFF450A0A),
    infoSurface = Color(0xFF172554),
    neutralSurface = Color(0xFF172033)
)

val LocalLifeOsSemanticPalette = staticCompositionLocalOf { LifeOsLightSemanticPalette }

// ==========================================
// RescueMesh Dark Showcase Design Tokens (Reference Image 1)
// ==========================================
object RescueMeshColors {
    val bgDark = Color(0xFF07152F)
    val bgNavy = Color(0xFF0B1F45)
    val bgCard = Color(0xFF0E1E3E)
    val bgCardElevated = Color(0xFF13254B)
    val cardBorder = Color(0xFF1E3A6E)
    val cardBorderGlow = Color(0xFF2563EB).copy(alpha = 0.4f)
    val cyan = Color(0xFF06B6D4)
    val electricBlue = Color(0xFF38BDF8)
    val emerald = Color(0xFF10B981)
    val amber = Color(0xFFF59E0B)
    val crimson = Color(0xFFEF4444)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFF94A3B8)
    val textMuted = Color(0xFF64748B)
}

object RescueMeshGradients {
    val background = androidx.compose.ui.graphics.Brush.verticalGradient(
        listOf(Color(0xFF07152F), Color(0xFF0B1F45), Color(0xFF0A192F))
    )
    val emergencyDispatch = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(Color(0xFFEF4444), Color(0xFFF43F5E), Color(0xFFFB7185))
    )
    val continueReview = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(Color(0xFFEF4444), Color(0xFFF43F5E), Color(0xFF8B5CF6))
    )
    val peerRelay = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(Color(0xFF2563EB), Color(0xFF3B82F6), Color(0xFF60A5FA))
    )
    val activeStep = androidx.compose.ui.graphics.Brush.horizontalGradient(
        listOf(Color(0xFF2563EB), Color(0xFF38BDF8))
    )
}
