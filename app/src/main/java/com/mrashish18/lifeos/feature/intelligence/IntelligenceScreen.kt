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

        // Header matching Screen 5
        Column {
            Text(
                text = "LIFEOS INTELLIGENCE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E1B4B),
                letterSpacing = (-0.3).sp
            )
            Text(
                text = "COGNITIVE ARCHITECTURE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4F46E5),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Observe. Understand. Decide. Act. Measure. Adapt.",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
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
                desc = "Monitors environment, real-time clock, connectivity, and workload state.",
                icon = "🌐",
                iconBg = Color(0xFFE0F2FE)
            ),
            StageData(
                num = "02",
                numColor = Color(0xFF10B981),
                stage = "UNDERSTAND",
                subsystem = "Behavior Model",
                subsystemColor = Color(0xFF059669),
                desc = "Learns completion velocities, preferred task size, and circadian peak focus.",
                icon = "🧠",
                iconBg = Color(0xFFD1FAE5)
            ),
            StageData(
                num = "03",
                numColor = Color(0xFF6366F1),
                stage = "DECIDE",
                subsystem = "Decision Engine",
                subsystemColor = Color(0xFF4F46E5),
                desc = "Scores priority, urgency, and effort fit with deterministic heuristics.",
                icon = "⚖️",
                iconBg = Color(0xFFEEF2FF)
            ),
            StageData(
                num = "04",
                numColor = Color(0xFF8B5CF6),
                stage = "ACT",
                subsystem = "User Execution",
                subsystemColor = Color(0xFF7C3AED),
                desc = "User initiates focus sessions, completes tasks, or investigates claims.",
                icon = "⚡",
                iconBg = Color(0xFFF3E8FF)
            ),
            StageData(
                num = "05",
                numColor = Color(0xFFF59E0B),
                stage = "MEASURE",
                subsystem = "Outcome Telemetry",
                subsystemColor = Color(0xFFD97706),
                desc = "Tracks task completion, focus session duration, and verification outcomes.",
                icon = "📊",
                iconBg = Color(0xFFFEF3C7)
            ),
            StageData(
                num = "06",
                numColor = Color(0xFF3B82F6),
                stage = "ADAPT",
                subsystem = "Learning Loop",
                subsystemColor = Color(0xFF2563EB),
                desc = "Calibrates on-device model weights to continuously improve future guidance.",
                icon = "🔄",
                iconBg = Color(0xFFEFF6FF)
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
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(item.numColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.num,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        if (index < stages.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(28.dp)
                                    .background(Color(0xFFCBD5E1))
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
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(item.iconBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item.icon, fontSize = 15.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = item.stage,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0F172A),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = item.subsystem,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = item.subsystemColor
                                )
                                Text(
                                    text = item.desc,
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Core Principles matching reference
        Text(
            text = "Core Principles",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(2.dp))

        listOf(
            Triple("🔒", "On-Device Privacy", Color(0xFF0284C7)),
            Triple("⚡", "Deterministic & Explainable", Color(0xFF0284C7)),
            Triple("👤", "Human-Centric Design", Color(0xFF0284C7))
        ).forEach { (icon, title, color) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0F9FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0F2FE))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = icon, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Unified Intelligence Pillars
        Text(
            text = "Unified Intelligence Pillars",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(2.dp))

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
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = pillar.icon, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = pillar.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = pillar.subtitle,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF6366F1)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(pillar.badgeBg)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = pillar.badge,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = pillar.badgeColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = pillar.description,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = pillar.ctaLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4338CA)
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
