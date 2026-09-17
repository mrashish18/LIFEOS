package com.mrashish18.lifeos.ui.theme

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
    listOf(Color(0xFF1E3A8A), Color(0xFF4338CA))
)
val LifeOsFocusBorderGradient = androidx.compose.ui.graphics.Brush.linearGradient(
    listOf(Color(0xFF3B82F6), Color(0xFF818CF8))
)
val LifeOsHeroSubtleGradient = androidx.compose.ui.graphics.Brush.verticalGradient(
    listOf(Color(0xFFF8FAFC), Color(0xFFEEF2FF).copy(alpha = 0.5f))
)