package com.mrashish18.lifeos.feature.intelligence

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrashish18.lifeos.ui.components.LifeOsEyebrow
import com.mrashish18.lifeos.ui.theme.*

@Composable
fun IntelligenceScreen(
    onNavigateToPersonal: () -> Unit = {},
    onNavigateToTruth: () -> Unit = {},
    onNavigateToResilience: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    isDarkMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // 1. Top Bar matching Global Flagship Header Standard
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                com.mrashish18.lifeos.ui.components.LifeOsMenuButton(
                    onClick = onOpenDrawer,
                    isDarkMode = isDarkMode
                )
                Column {
                    Text(
                        text = "Intelligence",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = "Autonomous Cognitive Architecture",
                        fontSize = 11.5.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFFDCFCE7),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF059669) else Color(0xFFBBF7D0)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) Color(0xFF4ADE80) else Color(0xFF16A34A))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "On-Device • Private",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFF4ADE80) else Color(0xFF16A34A)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2. Scenic Cognitive Cosmos Hero Banner
        com.mrashish18.lifeos.ui.components.IntelligenceScenicBanner(isDarkMode = isDarkMode)

        Spacer(modifier = Modifier.height(6.dp))

        // 3. Cognitive Loop Pipeline Strip
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkMode) Color(0xFF172033) else Color(0xFFF8FAFC),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
            )
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)) {
                Text(
                    text = "COGNITIVE CYCLE ARCHITECTURE",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA),
                    letterSpacing = 0.7.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IntelStagePill(step = "\uD83D\uDC41\uFE0F OBSERVE", label = "Sensory", color = Color(0xFF0284C7), isDarkMode = isDarkMode)
                    Text("→", fontSize = 10.sp, color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    IntelStagePill(step = "\uD83E\uDDE0 MODEL", label = "Patterns", color = Color(0xFF4F46E5), isDarkMode = isDarkMode)
                    Text("→", fontSize = 10.sp, color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    IntelStagePill(step = "\u2696\uFE0F DECIDE", label = "Heuristics", color = Color(0xFF7C3AED), isDarkMode = isDarkMode)
                    Text("→", fontSize = 10.sp, color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    IntelStagePill(step = "\uD83C\uDF31 ADAPT", label = "Learning", color = Color(0xFF16A34A), isDarkMode = isDarkMode)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Closed-Loop 6-Stage Adaptive Pipeline
        val stages = listOf(
            StageData(
                num = "01",
                numColor = Color(0xFF06B6D4),
                stage = "OBSERVE",
                subsystem = "Context Engine",
                subsystemColor = Color(0xFF0284C7),
                desc = "Sense your context with privacy-respecting signals",
                icon = "👁️",
                iconBg = Color(0xFFE0F2FE)
            ),
            StageData(
                num = "02",
                numColor = Color(0xFF6366F1),
                stage = "UNDERSTAND",
                subsystem = "Behavior Model",
                subsystemColor = Color(0xFF4F46E5),
                desc = "Build your behavioral model and understand patterns",
                icon = "🧠",
                iconBg = Color(0xFFEEF2FF)
            ),
            StageData(
                num = "03",
                numColor = Color(0xFF8B5CF6),
                stage = "DECIDE",
                subsystem = "Decision Engine",
                subsystemColor = Color(0xFF7C3AED),
                desc = "Run heuristics and choose the next best action",
                icon = "⚖️",
                iconBg = Color(0xFFF3E8FF)
            ),
            StageData(
                num = "04",
                numColor = Color(0xFFF43F5E),
                stage = "ACT",
                subsystem = "Focused Action",
                subsystemColor = Color(0xFFE11D48),
                desc = "Help you take focused and meaningful action",
                icon = "▶",
                iconBg = Color(0xFFFFE4E6)
            ),
            StageData(
                num = "05",
                numColor = Color(0xFFF59E0B),
                stage = "MEASURE",
                subsystem = "Outcome Telemetry",
                subsystemColor = Color(0xFFD97706),
                desc = "See outcomes and learn from real-world results",
                icon = "📊",
                iconBg = Color(0xFFFEF3C7)
            ),
            StageData(
                num = "06",
                numColor = Color(0xFF10B981),
                stage = "ADAPT",
                subsystem = "Learning Loop",
                subsystemColor = Color(0xFF059669),
                desc = "Continuously improve with your personalized model",
                icon = "🌿",
                iconBg = Color(0xFFD1FAE5)
            )
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            stages.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Numbered Badge + Vertical Connector
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(item.numColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.num,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        if (index < stages.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .width(2.5.dp)
                                    .height(30.dp)
                                    .background(if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Right Content Card
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isDarkMode) Color(0xFF172033) else Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isDarkMode) item.numColor.copy(alpha = 0.2f) else item.iconBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item.icon, fontSize = 17.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = item.stage,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = item.subsystem,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) item.numColor else item.subsystemColor
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.desc,
                                    fontSize = 11.5.sp,
                                    color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Core Principles matching reference
        Text(
            text = "Core Principles",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A),
            letterSpacing = (-0.2).sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        listOf(
            Triple("🔒", "On-Device Privacy", Color(0xFF0284C7)),
            Triple("⚡", "Deterministic & Explainable", Color(0xFF0284C7)),
            Triple("👤", "Human-Centric Design", Color(0xFF0284C7))
        ).forEach { (icon, title, color) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF111827) else Color(0xFFF0F9FF),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF334155) else Color(0xFFBAE6FD)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = icon, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Unified Intelligence Pillars
        Text(
            text = "Unified Intelligence Pillars",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A),
            letterSpacing = (-0.2).sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        listOf(
            PillarData(
                icon = "🎯",
                title = "Personal Intelligence",
                subtitle = "Cognitive Optimization",
                description = "Closed-loop adaptation: Context + Behavior Model + Decision Engine with deterministic explainability.",
                badge = "ACTIVE",
                badgeBg = Color(0xFFDCFCE7),
                badgeColor = Color(0xFF16A34A),
                ctaLabel = "Open Action Queue →",
                onClick = onNavigateToPersonal
            ),
            PillarData(
                icon = "🛡️",
                title = "Trust Intelligence",
                subtitle = "RealityCheck Verification",
                description = "10-stage mathematical claim verification separating authoritative fact from inference using offline corpora.",
                badge = "STANDBY",
                badgeBg = Color(0xFFEEF2FF),
                badgeColor = Color(0xFF4338CA),
                ctaLabel = "Open Truth Inquiry →",
                onClick = onNavigateToTruth
            ),
            PillarData(
                icon = "📡",
                title = "Resilience Intelligence",
                subtitle = "RescueMesh Offline Comms",
                description = "Hop-limited store-and-forward mesh propagation and gateway sync when normal networks fail.",
                badge = "ARMED",
                badgeBg = Color(0xFFFEF3C7),
                badgeColor = Color(0xFFD97706),
                ctaLabel = "Open RescueMesh Center →",
                onClick = onNavigateToResilience
            )
        ).forEach { pillar ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = pillar.onClick),
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkMode) Color(0xFF111827) else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                ),
                shadowElevation = if (isDarkMode) 0.dp else 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = pillar.icon, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = pillar.title,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                )
                                Text(
                                    text = pillar.subtitle,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF6366F1)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isDarkMode) pillar.badgeColor.copy(alpha = 0.2f) else pillar.badgeBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = pillar.badge,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) pillar.badgeColor.copy(alpha = 0.95f) else pillar.badgeColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = pillar.description,
                        fontSize = 12.sp,
                        color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                        lineHeight = 16.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = pillar.ctaLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

private data class PillarData(
    val icon: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val badge: String,
    val badgeBg: Color,
    val badgeColor: Color,
    val ctaLabel: String,
    val onClick: () -> Unit
)

private data class StageData(
    val num: String,
    val numColor: Color,
    val stage: String,
    val subsystem: String,
    val subsystemColor: Color,
    val desc: String,
    val icon: String,
    val iconBg: Color
)

@Composable
private fun IntelStagePill(
    step: String,
    label: String,
    color: Color,
    isDarkMode: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isDarkMode) color.copy(alpha = 0.25f) else color.copy(alpha = 0.12f))
                .padding(horizontal = 6.dp, vertical = 2.5.dp)
        ) {
            Text(
                text = step,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Black,
                color = if (isDarkMode) color.copy(alpha = 0.95f) else color
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 7.5.sp,
            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
            fontWeight = FontWeight.Medium
        )
    }
}

