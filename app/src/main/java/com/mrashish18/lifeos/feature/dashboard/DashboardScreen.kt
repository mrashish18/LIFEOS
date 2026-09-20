package com.mrashish18.lifeos.feature.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.Recommendation
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.UserBehaviorModel
import com.mrashish18.lifeos.ui.components.*
import com.mrashish18.lifeos.ui.theme.*
import java.time.ZoneId
import com.mrashish18.lifeos.ui.components.LifeOsNotificationBell
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onAcceptRecommendation: (Recommendation) -> Unit,
    onDismissRecommendation: (Recommendation) -> Unit,
    onNavigateToTasks: () -> Unit = {},
    onNavigateToTruth: () -> Unit = {},
    onNavigateToResilience: () -> Unit = {},
    unreadNotificationCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    isDarkMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (uiState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        }
        return
    }

    val backgroundBrush = if (isDarkMode) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF0B1020),
                Color(0xFF0D1424),
                Color(0xFF0F172A),
                Color(0xFF0B1020)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFFF8FAFC),
                Color(0xFFF5F3FF),
                Color(0xFFEEF2FF),
                Color(0xFFEDE9FE)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. EDITORIAL TOP BAR & SCENIC SUNSET MOUNTAIN BANNER
            item {
                DashboardTopBar(
                    systemStatus = uiState.systemStatus,
                    unreadCount = unreadNotificationCount,
                    onOpenNotifications = onOpenNotifications,
                    onOpenDrawer = onOpenDrawer,
                    isDarkMode = isDarkMode
                )
            }
            item {
                DashboardScenicBanner(
                    title = "A little focus today\nA much better tomorrow",
                    pillText = "Your life. More possible. →"
                )
            }

            // Feedback Banner (if any)
            uiState.lastFeedbackMessage?.let { feedback ->
                item {
                    FeedbackCallout(feedback = feedback, isDarkMode = isDarkMode)
                }
            }

            // 2. INTELLIGENCE PILLARS (Reference Screen 1)
            item {
                IntelligencePillarsStrip(
                    uiState = uiState,
                    onNavigateToTasks = onNavigateToTasks,
                    onNavigateToTruth = onNavigateToTruth,
                    onNavigateToResilience = onNavigateToResilience,
                    isDarkMode = isDarkMode
                )
            }

            // 3. FLAGSHIP CENTERPIECE: WHAT MATTERS NOW
            if (uiState.recommendations.isNotEmpty()) {
                items(uiState.recommendations, key = { it.id }) { rec ->
                    WhatMattersNowCenterpiece(
                        recommendation = rec,
                        onAccept = { onAcceptRecommendation(rec) },
                        onDismiss = { onDismissRecommendation(rec) },
                        isDarkMode = isDarkMode
                    )
                }
            } else {
                item {
                    NominalFocusState(isDarkMode = isDarkMode)
                }
            }

            // 4. LEARNING LOOP (Reference Screen 1)
            item {
                DashboardLearningLoopCard(onNavigateToIntel = onNavigateToTruth, isDarkMode = isDarkMode)
            }

            // 5. TODAY MOMENTUM PROGRESS (Card matching reference design)
            item {
                TodayMomentumCluster(
                    pendingCount = uiState.pendingCount,
                    completedCount = uiState.completedCount,
                    totalCount = uiState.tasks.size,
                    onViewTasks = onNavigateToTasks,
                    isDarkMode = isDarkMode
                )
            }

            // 4. CURRENT SITUATION (2x2 Telemetry Grid matching reference Screen 1)
            uiState.contextSnapshot?.let { snapshot ->
                item {
                    CurrentStateTelemetry(snapshot = snapshot, isDarkMode = isDarkMode)
                }
            }

            // 5. BEHAVIOR MODEL: LIFEOS IS LEARNING
            item {
                uiState.behaviorModel?.let { model ->
                    LifeOsIsLearningSection(model = model, isDarkMode = isDarkMode)
                }
            }

            // 6. RECENT QUEUE (Open list rows with hairline dividers)
            if (uiState.tasks.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LifeOsEyebrow(
                            text = "RECENT QUEUE",
                            color = if (isDarkMode) Color(0xFF818CF8) else LifeOsIndigo700
                        )
                        TextButton(onClick = onNavigateToTasks) {
                            Text(
                                text = "All Tasks →",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkMode) Color(0xFF818CF8) else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                items(uiState.tasks.take(4), key = { it.id }) { task ->
                    LifeOsTaskRow(
                        title = task.title,
                        description = task.description,
                        status = task.status,
                        priority = task.priority,
                        category = task.category,
                        durationMinutes = task.estimatedMinutes
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

/**
 * Editorial Command Center Header:
 * Eyebrow: ADAPTIVE INTELLIGENCE
 * Identity: LIFEOS
 * Tagline: Understand. Decide. Adapt.
 * Live indicator: ● ACTIVE
 */
@Composable
private fun HeroCommandHeader(systemStatus: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LifeOsEyebrow(text = "ADAPTIVE INTELLIGENCE", color = LifeOsIndigo700)

            if (systemStatus == "ACTIVE" || systemStatus == "NOMINAL" || systemStatus == "ONLINE") {
                Surface(
                    color = LifeOsGreen50,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsGreen700.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(LifeOsGreen700)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = LifeOsGreen700,
                            letterSpacing = 0.8.sp,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "LIFEOS",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                fontSize = 30.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Understand. Decide. Adapt.",
            style = MaterialTheme.typography.titleMedium,
            color = LifeOsSlate600,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Open canvas "TODAY" momentum cluster:
 * Shows pending, completed, total with a sleek inline progress bar.
 */
@Composable
private fun TodayMomentumCluster(
    pendingCount: Int,
    completedCount: Int,
    totalCount: Int,
    onViewTasks: () -> Unit,
    isDarkMode: Boolean = false
) {
    val completionRatio = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = if (totalCount > 0) completionRatio.coerceIn(0.08f, 1f) else 0f,
        label = "momentumProgress"
    )
    val completionPct = (completionRatio * 100).toInt()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewTasks),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkMode) Color(0xFF111827) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkMode) Color(0xFF334155) else Color(0xFFF1F5F9)
        ),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            // Top Row: Title
            Text(
                text = "Today's Momentum",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Middle Row: 3 Pending | 1 Completed | [ 25% ] Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                    Column {
                        Text(
                            text = "$pendingCount",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Text(
                            text = "Pending",
                            fontSize = 11.sp,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }

                    Column {
                        Text(
                            text = "$completedCount",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Text(
                            text = "Completed",
                            fontSize = 11.sp,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }

                // Soft lavender/blue rounded box with 25%
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEEF2FF))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$completionPct%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Row: Thin Progress Bar with small dot on the right
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                )
                if (animatedProgress > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(Color(0xFF10B981), Color(0xFF06B6D4))
                                )
                            )
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA))
                )
            }
        }
    }
}

@Composable
private fun DashboardTopBar(
    systemStatus: String,
    unreadCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    isDarkMode: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LifeOsMenuButton(
            onClick = onOpenDrawer,
            isDarkMode = isDarkMode
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LifeOS",
                fontSize = 21.sp,
                fontWeight = FontWeight.Black,
                color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                letterSpacing = (-0.3).sp
            )
            Text(
                text = "Understand • Decide • Adapt",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                letterSpacing = 0.3.sp
            )
        }

        LifeOsNotificationBell(
            unreadCount = unreadCount,
            onClick = onOpenNotifications,
            isDarkMode = isDarkMode
        )
    }
}

@Composable
private fun IntelligencePillarsStrip(
    uiState: DashboardUiState,
    onNavigateToTasks: () -> Unit,
    onNavigateToTruth: () -> Unit,
    onNavigateToResilience: () -> Unit,
    isDarkMode: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Intelligence Pillars",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
            )
            Text(
                text = "All Systems Ready >",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4F46E5),
                modifier = Modifier.clickable(onClick = onNavigateToTruth)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. PERSONAL PILLAR
            PillarCard(
                modifier = Modifier.weight(1f),
                icon = "⚡",
                pillarName = "PERSONAL",
                status = "Ready",
                statusColor = Color(0xFF10B981),
                onClick = onNavigateToTasks,
                isDarkMode = isDarkMode
            )

            // 2. TRUTH PILLAR
            val truthStatus = if (uiState.investigationCount > 0) "Cached" else "Ready"
            PillarCard(
                modifier = Modifier.weight(1f),
                icon = "🛡️",
                pillarName = "TRUTH",
                status = truthStatus,
                statusColor = Color(0xFF4F46E5),
                onClick = onNavigateToTruth,
                isDarkMode = isDarkMode
            )

            // 3. MESH PILLAR
            val meshStatus = if (uiState.hasCriticalEmergency) "Alert" else "Armed"
            val meshColor = if (uiState.hasCriticalEmergency) Color(0xFFDC2626) else Color(0xFF06B6D4)
            PillarCard(
                modifier = Modifier.weight(1f),
                icon = "📡",
                pillarName = "MESH",
                status = meshStatus,
                statusColor = meshColor,
                onClick = onNavigateToResilience,
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
private fun PillarCard(
    icon: String,
    pillarName: String,
    status: String,
    statusColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (isDarkMode) Color(0xFF111827) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
        ),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = pillarName,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = status,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                )
            }
        }
    }
}

@Composable
private fun DashboardLearningLoopCard(
    onNavigateToIntel: () -> Unit = {},
    isDarkMode: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onNavigateToIntel),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkMode) Color(0xFF111827) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
        ),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Learning Loop",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                )
                Text(
                    text = "See Details >",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4F46E5)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DashboardLoopStep(icon = "⚡", step = "ACTION", sub = "Do", color = Color(0xFF6366F1), isDarkMode = isDarkMode)
                Text("→", fontSize = 11.sp, color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                DashboardLoopStep(icon = "👁️", step = "EVENT", sub = "See", color = Color(0xFFF97316), isDarkMode = isDarkMode)
                Text("→", fontSize = 11.sp, color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                DashboardLoopStep(icon = "🧠", step = "MODEL", sub = "Learn", color = Color(0xFF8B5CF6), isDarkMode = isDarkMode)
                Text("→", fontSize = 11.sp, color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                DashboardLoopStep(icon = "🎯", step = "DECISION", sub = "Better", color = Color(0xFF0D9488), isDarkMode = isDarkMode)
            }
        }
    }
}

@Composable
private fun DashboardLoopStep(
    icon: String,
    step: String,
    sub: String,
    color: Color,
    isDarkMode: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = step,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            color = color,
            letterSpacing = 0.5.sp
        )
        Text(
            text = sub,
            fontSize = 8.5.sp,
            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Centerpiece — "✦ WHAT MATTERS NOW":
 * Elevated focal surface with subtle gradient border, bold typography,
 * high-emphasis primary CTA, and reference layout matching Screen 1.
 */
@Composable
private fun WhatMattersNowCenterpiece(
    recommendation: Recommendation,
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
    isDarkMode: Boolean = false
) {
    var isExplanationExpanded by remember { mutableStateOf(false) }
    val confidencePct = (recommendation.confidence * 100).toInt()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isDarkMode) {
                    Modifier.border(
                        width = 1.dp,
                        color = Color(0xFF334155),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier.border(
                        width = 1.5.dp,
                        brush = LifeOsFocusBorderGradient,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkMode) Color(0xFF111827) else Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: ✦ WHAT MATTERS NOW | [ HIGH IMPACT ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "✦",
                        color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4F46E5),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WHAT MATTERS NOW",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4F46E5),
                        letterSpacing = 0.8.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFFFF7ED))
                        .border(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFFED7AA), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "HIGH IMPACT",
                        color = if (isDarkMode) Color(0xFFFDBA74) else Color(0xFFEA580C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Recommendation Title
            Text(
                text = recommendation.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                letterSpacing = (-0.3).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rationale Description
            Text(
                text = recommendation.reason,
                fontSize = 12.5.sp,
                color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                lineHeight = 17.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row: [ Start Focus ]  [ See Why ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LifeOsGradientButton(
                    text = "Start Focus",
                    gradient = LifeOsGradients.primary,
                    onClick = onAccept,
                    modifier = Modifier.weight(1.3f)
                )

                LifeOsSecondaryButton(
                    text = "See Why",
                    onClick = { isExplanationExpanded = !isExplanationExpanded },
                    modifier = Modifier.weight(0.9f),
                    isDarkMode = isDarkMode
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Explainability Pill Button
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isExplanationExpanded = !isExplanationExpanded },
                color = if (isDarkMode) {
                    if (isExplanationExpanded) Color(0xFF1E293B) else Color(0xFF172033)
                } else {
                    if (isExplanationExpanded) Color(0xFFEEF2FF) else Color(0xFFF8FAFC)
                },
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF334155) else if (isExplanationExpanded) Color(0xFFC7D2FE) else Color(0xFFE2E8F0)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isExplanationExpanded) "✦" else "ℹ️",
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isExplanationExpanded) "Hide Decision Engine Explainability ▲" else "Why this recommendation? ▼",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA)
                    )
                }
            }

            AnimatedVisibility(visible = isExplanationExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDarkMode) Color(0xFF172033) else Color(0xFFF8FAFC))
                        .border(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DECISION ENGINE EXPLAINABILITY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA),
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = "Deterministic",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    recommendation.factors.forEach { factor ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val pts = (factor.scoreContribution * 100).toInt()
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEEF2FF))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (pts > 0) "+$pts" else "$pts",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = factor.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                )
                                Text(
                                    text = factor.description,
                                    fontSize = 10.sp,
                                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Scored transparently across urgency, effort fit, circadian alignment, and behavioral momentum.",
                        fontSize = 9.sp,
                        color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8),
                        lineHeight = 13.sp
                    )
                }
            }
        }
    }
}

/**
 * Calm state when no urgent recommendation is pending.
 */
@Composable
private fun NominalFocusState(isDarkMode: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isDarkMode) Color(0xFF172033) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .then(
                if (isDarkMode) Modifier.border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                else Modifier
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(LifeOsGreen700)
            )
            LifeOsEyebrow(text = "FOCUS STATE NOMINAL", color = LifeOsGreen700)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Queue is balanced and context is stable. Pick your next task from the queue below.",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDarkMode) Color(0xFFCBD5E1) else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CurrentStateTelemetry(
    snapshot: ContextSnapshot,
    isDarkMode: Boolean = false
) {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneId.systemDefault())
    val formattedTime = formatter.format(snapshot.currentTime).uppercase()
    val networkLabel = when (snapshot.networkState.name) {
        "CONNECTED_WIFI", "CONNECTED_CELLULAR", "CONNECTED" -> "CONNECTED"
        "DISCONNECTED" -> "OFFLINE"
        else -> snapshot.networkState.name.uppercase()
    }
    val dayLabel = snapshot.dayOfWeek.name.uppercase()
    val workloadLabel = snapshot.workloadLevel.name.uppercase()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Current Situation",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFDCFCE7),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF334155) else Color(0xFFBBF7D0)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) Color(0xFF10B981) else Color(0xFF16A34A))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Live",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFF34D399) else Color(0xFF15803D)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2x2 Grid of 4 compact cards matching reference Screen 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TelemetryGridCard(
                icon = "🕒",
                iconBg = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEEF2FF),
                label = "Time",
                value = formattedTime,
                modifier = Modifier.weight(1f),
                isDarkMode = isDarkMode
            )
            TelemetryGridCard(
                icon = "📶",
                iconBg = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFECFDF5),
                label = "Network",
                value = networkLabel,
                modifier = Modifier.weight(1f),
                isDarkMode = isDarkMode
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TelemetryGridCard(
                icon = "📅",
                iconBg = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                label = "Day",
                value = dayLabel,
                modifier = Modifier.weight(1f),
                isDarkMode = isDarkMode
            )
            TelemetryGridCard(
                icon = "⚡",
                iconBg = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF5F3FF),
                label = "Workload",
                value = workloadLabel,
                modifier = Modifier.weight(1f),
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
private fun TelemetryGridCard(
    icon: String,
    iconBg: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = if (isDarkMode) Color(0xFF111827) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
        ),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    fontSize = 12.5.sp,
                    color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun TelemetryGridCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(LifeOsSlate50.copy(alpha = 0.7f))
            .border(1.dp, LifeOsSlate200.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = LifeOsSlate600,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            letterSpacing = 0.6.sp
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * BEHAVIOR MODEL: LIFEOS ADAPTATION
 * Real behavioral signals, cognitive profile, and calibration progress.
 */
@Composable
private fun LifeOsIsLearningSection(
    model: UserBehaviorModel,
    isDarkMode: Boolean = false
) {
    val totalSignals = model.totalTasksCompleted + model.totalTasksAbandoned
    val calibrationRatio = (totalSignals / 3f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = calibrationRatio,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 700),
        label = "calibrationProgress"
    )
    val calibrationPct = (calibrationRatio * 100).toInt()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkMode) Color(0xFF111827) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkMode) Color(0xFF334155) else Color(0xFFF1F5F9)
        ),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LifeOsEyebrow(
                    text = "LIFEOS ADAPTATION",
                    color = if (isDarkMode) Color(0xFF818CF8) else LifeOsIndigo700
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEEF2FF))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (model.hasSufficientData) "CALIBRATED" else "LEARNING",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Closed-Loop Learning Cycle: ACTION -> EVENT -> MODEL -> DECISION ENGINE
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = if (isDarkMode) Color(0xFF172033) else Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "ACTION", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA))
                    Text(text = "→", fontSize = 9.sp, color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    Text(text = "EVENT", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                    Text(text = "→", fontSize = 9.sp, color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    Text(text = "MODEL", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
                    Text(text = "→", fontSize = 9.sp, color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    Text(text = "DECISION ENGINE", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Behavior Calibration",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkMode) Color(0xFFF8FAFC) else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$totalSignals / 3 signals ($calibrationPct%)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color(0xFF818CF8) else MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = if (isDarkMode) Color(0xFF6366F1) else MaterialTheme.colorScheme.primary,
                trackColor = if (isDarkMode) Color(0xFF1E293B) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Cognitive Profile Grid: Duration, Peak Window, Habit Momentum
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val durationLabel = model.preferredTaskSize?.label ?: "Calibrating..."
                val peakLabel = model.peakProductivityTimeOfDay?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Calibrating..."
                val topCat = model.preferredCategories.maxByOrNull { it.value }?.key?.name ?: "Building..."

                CognitiveProfileMiniCard(
                    icon = "⏱️",
                    label = "DURATION",
                    value = durationLabel,
                    modifier = Modifier.weight(1f),
                    isDarkMode = isDarkMode
                )
                CognitiveProfileMiniCard(
                    icon = "☀️",
                    label = "PEAK WINDOW",
                    value = peakLabel,
                    modifier = Modifier.weight(1f),
                    isDarkMode = isDarkMode
                )
                CognitiveProfileMiniCard(
                    icon = "🚀",
                    label = "MOMENTUM",
                    value = topCat,
                    modifier = Modifier.weight(1f),
                    isDarkMode = isDarkMode
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!model.hasSufficientData) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDarkMode) Color(0xFF172033) else Color(0xFFF8FAFC))
                        .border(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💡", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Calibrating your baseline. Complete or postpone 3 tasks to train circadian energy and focus duration scoring.",
                                fontSize = 11.5.sp,
                                color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                                lineHeight = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            listOf("✓ Complete", "⏳ Postpone", "▶ Focus", "⚡ Feedback").forEach { action ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEEF2FF))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = action,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    model.completionRate?.let { rate ->
                        TelemetryColumn(
                            label = "COMPLETION",
                            value = "${(rate * 100).toInt()}%",
                            isDarkMode = isDarkMode
                        )
                    }
                    model.postponementRate?.let { rate ->
                        TelemetryColumn(
                            label = "POSTPONE",
                            value = "${(rate * 100).toInt()}%",
                            isDarkMode = isDarkMode
                        )
                    }
                    model.averageCompletedDurationMinutes?.let { dur ->
                        TelemetryColumn(
                            label = "AVG TIME",
                            value = "${dur.toInt()}m",
                            isDarkMode = isDarkMode
                        )
                    }
                    val topCat = model.preferredCategories.maxByOrNull { it.value }?.key
                    topCat?.let { cat ->
                        TelemetryColumn(
                            label = "MOMENTUM",
                            value = cat.name,
                            isDarkMode = isDarkMode
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Observed patterns directly bias recommendation heuristics: favoring your peak energy window and preferred duration without cloud profiling.",
                    fontSize = 11.sp,
                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun CognitiveProfileMiniCard(
    icon: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkMode) Color(0xFF172033) else Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TelemetryColumn(
    label: String,
    value: String,
    isDarkMode: Boolean = false
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isDarkMode) Color(0xFF94A3B8) else MaterialTheme.colorScheme.outline,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color(0xFFF8FAFC) else MaterialTheme.colorScheme.onSurface,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun FeedbackCallout(
    feedback: String,
    isDarkMode: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = if (isDarkMode) Color(0xFF172033) else LifeOsIndigo50,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkMode) Color(0xFF334155) else LifeOsIndigo700.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "⚡", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = feedback,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkMode) Color(0xFF818CF8) else LifeOsIndigo700,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
