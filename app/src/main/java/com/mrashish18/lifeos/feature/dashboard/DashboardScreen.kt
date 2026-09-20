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
import androidx.compose.ui.graphics.Color
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
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onAcceptRecommendation: (Recommendation) -> Unit,
    onDismissRecommendation: (Recommendation) -> Unit,
    onNavigateToTasks: () -> Unit = {},
    onNavigateToTruth: () -> Unit = {},
    onNavigateToResilience: () -> Unit = {},
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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. BRANDING & HERO SCENIC MOUNTAIN HEADER
        item {
            ScenicMountainHeader(systemStatus = uiState.systemStatus)
        }

        // Feedback Banner (if any)
        uiState.lastFeedbackMessage?.let { feedback ->
            item {
                FeedbackCallout(feedback = feedback)
            }
        }

        // 2. TODAY MOMENTUM PROGRESS (Card matching reference design)
        item {
            TodayMomentumCluster(
                pendingCount = uiState.pendingCount,
                completedCount = uiState.completedCount,
                totalCount = uiState.tasks.size,
                onViewTasks = onNavigateToTasks
            )
        }

        // Cross-Pillar Guardian Telemetry
        item {
            IntelligencePillarsStrip(
                uiState = uiState,
                onNavigateToTruth = onNavigateToTruth,
                onNavigateToResilience = onNavigateToResilience
            )
        }

        // 3. FLAGSHIP CENTERPIECE: WHAT MATTERS NOW
        if (uiState.recommendations.isNotEmpty()) {
            items(uiState.recommendations, key = { it.id }) { rec ->
                WhatMattersNowCenterpiece(
                    recommendation = rec,
                    onAccept = { onAcceptRecommendation(rec) },
                    onDismiss = { onDismissRecommendation(rec) }
                )
            }
        } else {
            item {
                NominalFocusState()
            }
        }

        // 4. CURRENT SITUATION (2x2 Telemetry Grid matching reference Screen 1)
        uiState.contextSnapshot?.let { snapshot ->
            item {
                CurrentStateTelemetry(snapshot = snapshot)
            }
        }

        // 5. BEHAVIOR MODEL: LIFEOS IS LEARNING
        item {
            uiState.behaviorModel?.let { model ->
                LifeOsIsLearningSection(model = model)
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
                    LifeOsEyebrow(text = "RECENT QUEUE")
                    TextButton(onClick = onNavigateToTasks) {
                        Text(
                            text = "All Tasks →",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
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
    onViewTasks: () -> Unit
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
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            // Top Row: Title
            Text(
                text = "Today's Momentum",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
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
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Pending",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Column {
                        Text(
                            text = "$completedCount",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Completed",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Soft lavender/blue rounded box with 25%
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEEF2FF))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$completionPct%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4338CA)
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
                        .background(Color(0xFFF1F5F9))
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
                        .background(Color(0xFF4338CA))
                )
            }
        }
    }
}

@Composable
private fun IntelligencePillarsStrip(
    uiState: DashboardUiState,
    onNavigateToTruth: () -> Unit,
    onNavigateToResilience: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val truthStatusText = if (uiState.investigationCount > 0) {
            "${uiState.investigationCount} Verified"
        } else {
            "Standby • Ready"
        }
        val truthSubText = uiState.latestInvestigation?.let {
            "${it.verdict.name} (${it.confidencePercentage}%)"
        } ?: "Tap to verify claims →"

        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onNavigateToTruth),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🛡️", fontSize = 15.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TRUTH ENGINE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFEEF2FF))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = if (uiState.investigationCount > 0) "CACHED" else "ARMED",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4338CA)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = truthStatusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1B4B)
                    )
                    Text(
                        text = truthSubText,
                        fontSize = 9.sp,
                        color = Color(0xFF6366F1),
                        maxLines = 1
                    )
                }
            }
        }

        val meshStatusText = if (uiState.emergencyQueuedCount > 0) {
            "${uiState.emergencyQueuedCount} Queued"
        } else {
            "Armed • Ready"
        }
        val meshSubText = if (uiState.hasCriticalEmergency) {
            "CRITICAL ALERT"
        } else if (uiState.emergencyQueuedCount > 0) {
            "Waiting for peer relay"
        } else {
            "Local Mesh Active →"
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onNavigateToResilience),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (uiState.hasCriticalEmergency) Color(0xFFFECACA) else Color(0xFFF1F5F9)
            ),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "📡", fontSize = 15.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RESCUEMESH",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (uiState.hasCriticalEmergency) Color(0xFFFEE2E2)
                                    else if (uiState.emergencyQueuedCount > 0) Color(0xFFFEF3C7)
                                    else Color(0xFFDCFCE7)
                                )
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = if (uiState.hasCriticalEmergency) "ALERT"
                                else if (uiState.emergencyQueuedCount > 0) "OFFLINE"
                                else "READY",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.hasCriticalEmergency) Color(0xFFDC2626)
                                else if (uiState.emergencyQueuedCount > 0) Color(0xFFD97706)
                                else Color(0xFF16A34A)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = meshStatusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.hasCriticalEmergency) Color(0xFFDC2626) else Color(0xFF1E1B4B)
                    )
                    Text(
                        text = meshSubText,
                        fontSize = 9.sp,
                        color = if (uiState.hasCriticalEmergency) Color(0xFFDC2626) else Color(0xFF059669),
                        maxLines = 1
                    )
                }
            }
        }
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
    onDismiss: () -> Unit
) {
    var isExplanationExpanded by remember { mutableStateOf(false) }
    val confidencePct = (recommendation.confidence * 100).toInt()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                brush = LifeOsFocusBorderGradient,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: ✦ WHAT MATTERS NOW | [ 85% confidence ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "✦",
                        color = Color(0xFF4F46E5),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WHAT MATTERS NOW",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4F46E5),
                        letterSpacing = 0.8.sp
                    )
                }

                Surface(
                    color = Color(0xFF4338CA),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "$confidencePct% confidence",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Recommendation Title
            Text(
                text = recommendation.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rationale Description
            Text(
                text = recommendation.reason,
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                lineHeight = 16.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row: [ ▶ Start Focus ]  [ Dismiss ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LifeOsGradientButton(
                    text = "▶  Start Focus",
                    gradient = LifeOsGradients.focus,
                    onClick = onAccept,
                    modifier = Modifier.weight(1.4f)
                )

                LifeOsSecondaryButton(
                    text = "Dismiss",
                    onClick = onDismiss,
                    modifier = Modifier.weight(0.9f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Link: Why this recommendation? →
            Text(
                text = "Why this recommendation? →",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3B82F6),
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { isExplanationExpanded = !isExplanationExpanded }
                    .padding(vertical = 2.dp)
            )

            AnimatedVisibility(visible = isExplanationExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
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
                            color = Color(0xFF4338CA),
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = "Deterministic",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
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
                                    .background(Color(0xFFEEF2FF))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (pts > 0) "+$pts" else "$pts",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4338CA)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = factor.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = factor.description,
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Scored transparently across urgency, effort fit, circadian alignment, and behavioral momentum.",
                        fontSize = 9.sp,
                        color = Color(0xFF94A3B8),
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
private fun NominalFocusState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CurrentStateTelemetry(snapshot: ContextSnapshot) {
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
                color = Color(0xFF0F172A)
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFDCFCE7),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF16A34A))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Live",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D)
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
                iconBg = Color(0xFFEEF2FF),
                label = "Time",
                value = formattedTime,
                modifier = Modifier.weight(1f)
            )
            TelemetryGridCard(
                icon = "📶",
                iconBg = Color(0xFFECFDF5),
                label = "Network",
                value = networkLabel,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TelemetryGridCard(
                icon = "📅",
                iconBg = Color(0xFFEFF6FF),
                label = "Day",
                value = dayLabel,
                modifier = Modifier.weight(1f)
            )
            TelemetryGridCard(
                icon = "⚡",
                iconBg = Color(0xFFF5F3FF),
                label = "Workload",
                value = workloadLabel,
                modifier = Modifier.weight(1f)
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
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 9.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    fontSize = 11.sp,
                    color = Color(0xFF0F172A),
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
private fun LifeOsIsLearningSection(model: UserBehaviorModel) {
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
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LifeOsEyebrow(text = "LIFEOS ADAPTATION")
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFEEF2FF))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (model.hasSufficientData) "CALIBRATED" else "LEARNING",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4338CA)
                    )
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$totalSignals / 3 signals ($calibrationPct%)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
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
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
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
                    modifier = Modifier.weight(1f)
                )
                CognitiveProfileMiniCard(
                    icon = "☀️",
                    label = "PEAK WINDOW",
                    value = peakLabel,
                    modifier = Modifier.weight(1f)
                )
                CognitiveProfileMiniCard(
                    icon = "🚀",
                    label = "MOMENTUM",
                    value = topCat,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!model.hasSufficientData) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Calibrating your baseline. Complete or postpone 3 tasks to train circadian energy and focus duration scoring.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 15.sp
                        )
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
                            value = "${(rate * 100).toInt()}%"
                        )
                    }
                    model.postponementRate?.let { rate ->
                        TelemetryColumn(
                            label = "POSTPONE",
                            value = "${(rate * 100).toInt()}%"
                        )
                    }
                    model.averageCompletedDurationMinutes?.let { dur ->
                        TelemetryColumn(
                            label = "AVG TIME",
                            value = "${dur.toInt()}m"
                        )
                    }
                    val topCat = model.preferredCategories.maxByOrNull { it.value }?.key
                    topCat?.let { cat ->
                        TelemetryColumn(
                            label = "MOMENTUM",
                            value = cat.name
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Observed patterns directly bias recommendation heuristics: favoring your peak energy window and preferred duration without cloud profiling.",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 14.sp
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
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.4.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1B4B),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TelemetryColumn(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun FeedbackCallout(feedback: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = LifeOsIndigo50,
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsIndigo700.copy(alpha = 0.25f))
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
                color = LifeOsIndigo700,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
