package com.mrashish18.lifeos.ui.navigation

/**
 * Top-level destinations across LIFEOS domains:
 * - Foundation (Dashboard)
 * - Personal Intelligence (Tasks, Goals, Intelligence)
 * - Trust Intelligence (RealityCheck)
 * - Resilience Intelligence (RescueMesh)
 */
enum class LifeOsDestination(
    val title: String,
    val subtitle: String
) {
    DASHBOARD("Dashboard", "Overview & Context"),
    TASKS("Tasks", "Personal Intelligence"),
    GOALS("Goals", "Adaptive Productivity"),
    INTELLIGENCE("Intelligence", "Cognitive Architecture"),
    REALITY_CHECK("RealityCheck", "Trust Intelligence"),
    RESILIENCE("Resilience", "RescueMesh Offline")
}
