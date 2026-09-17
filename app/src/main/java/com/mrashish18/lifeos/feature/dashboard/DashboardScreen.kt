package com.mrashish18.lifeos.feature.dashboard

import androidx.compose.animation.AnimatedVisibility
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
import com.mrashish18.lifeos.ui.components.LifeOsEyebrow
import com.mrashish18.lifeos.ui.components.LifeOsPrimaryButton
import com.mrashish18.lifeos.ui.components.LifeOsSecondaryButton
import com.mrashish18.lifeos.ui.components.LifeOsStatusChip
import com.mrashish18.lifeos.ui.components.LifeOsPriorityBadge
import com.mrashish18.lifeos.ui.components.LifeOsCategoryBadge
import com.mrashish18.lifeos.ui.components.LifeOsTaskRow
import com.mrashish18.lifeos.ui.theme.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onAcceptRecommendation: (Recommendation) -> Unit,
    onDismissRecommendation: (Recommendation) -> Unit,
    onNavigateToTasks: () -> Unit = {},
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
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. COMMAND CENTER HERO NARRATIVE
        item {
            Spacer(modifier = Modifier.height(12.dp))
            HeroCommandHeader(systemStatus = uiState.systemStatus)
        }

        // Feedback Banner (if any)
        uiState.lastFeedbackMessage?.let { feedback ->
            item {
                FeedbackCallout(feedback = feedback)
            }
        }

        // 2. TODAY MOMENTUM CLUSTER (Open canvas, not a boxed card)
        item {
            TodayMomentumCluster(
                pendingCount = uiState.pendingCount,
                completedCount = uiState.completedCount,
                totalCount = uiState.tasks.size,
                onViewTasks = onNavigateToTasks
            )
        }

        // 3. CENTERPIECE: WHAT MATTERS NOW
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

        // 4. CURRENT STATE (Spatial telemetry hierarchy, not a card)
        item {
            uiState.contextSnapshot?.let { snapshot ->
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
    val completionPct = (completionRatio * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LifeOsEyebrow(text = "TODAY'S MOMENTUM", color = LifeOsIndigo700)
            Text(
                text = "$completionPct%",
                style = MaterialTheme.typography.labelSmall,
                color = LifeOsSlate600,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$pendingCount",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "pending",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LifeOsSlate600,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "·",
                style = MaterialTheme.typography.titleMedium,
                color = LifeOsSlate400,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$completedCount",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsGreen700
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "completed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LifeOsSlate600,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = onViewTasks,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "Tasks →",
                    style = MaterialTheme.typography.labelMedium,
                    color = LifeOsIndigo700,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { completionRatio },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(CircleShape),
            color = LifeOsIndigo700,
            trackColor = LifeOsSlate200
        )
    }
}

/**
 * Centerpiece — "✦ WHAT MATTERS NOW":
 * Elevated focal surface with subtle gradient border, bold typography,
 * high-emphasis primary CTA, confidence gauge, and expandable explainability.
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
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "✦",
                        color = LifeOsElectricIndigo,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    LifeOsEyebrow(
                        text = "WHAT MATTERS NOW",
                        color = LifeOsElectricIndigo
                    )
                }

                Surface(
                    color = LifeOsIndigo50,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "$confidencePct% confidence",
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeOsIndigo700,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Recommendation Title
            Text(
                text = recommendation.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Rationale Description
            Text(
                text = recommendation.reason,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Confidence Gauge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Confidence",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )
                LinearProgressIndicator(
                    progress = { recommendation.confidence.toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(CircleShape),
                    color = LifeOsIndigo700,
                    trackColor = MaterialTheme.colorScheme.outlineVariant
                )
                Text(
                    text = "$confidencePct%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsIndigo700,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // High-emphasis Primary CTA Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LifeOsPrimaryButton(
                    text = "▶  Start Focus",
                    onClick = onAccept,
                    modifier = Modifier.weight(2f)
                )

                LifeOsSecondaryButton(
                    text = "Dismiss",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )
            }

            // Expandable "Why this recommendation? →"
            if (recommendation.factors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isExplanationExpanded = !isExplanationExpanded }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isExplanationExpanded) "▾ Hide Decision Factors" else "Why this recommendation? →",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "${recommendation.factors.size} factors weighed",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 10.sp
                    )
                }

                AnimatedVisibility(visible = isExplanationExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recommendation.factors.forEach { factor ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "✦",
                                    color = LifeOsIndigo700,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = factor.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = factor.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
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

/**
 * CURRENT STATE Telemetry:
 * Structured 2x2 Telemetry Grid and dedicated Availability / Focus status.
 * Eliminates text collisions completely.
 */
@Composable
private fun CurrentStateTelemetry(snapshot: ContextSnapshot) {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneId.systemDefault())
    val formattedTime = formatter.format(snapshot.currentTime)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LifeOsEyebrow(text = "CURRENT SITUATION", color = LifeOsIndigo700)
            Text(
                text = "Observation Engine",
                style = MaterialTheme.typography.labelSmall,
                color = LifeOsSlate600,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2x2 Telemetry Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TelemetryGridCell(
                    label = "TIME",
                    value = formattedTime,
                    modifier = Modifier.weight(1f)
                )
                TelemetryGridCell(
                    label = "NETWORK",
                    value = snapshot.networkState.name,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TelemetryGridCell(
                    label = "DAY",
                    value = snapshot.dayOfWeek.name,
                    modifier = Modifier.weight(1f)
                )
                TelemetryGridCell(
                    label = "WORKLOAD",
                    value = snapshot.workloadLevel.name,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(12.dp))

        // Dedicated Availability & Active Focus Rows (No Text Collisions)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AVAILABILITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeOsSlate600,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 0.6.sp
                )
                Surface(
                    color = LifeOsGreen50,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsGreen700.copy(alpha = 0.25f))
                ) {
                    Text(
                        text = snapshot.userAvailability.name,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = LifeOsGreen700,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            snapshot.activeTask?.let { active ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(LifeOsIndigo50.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "ACTIVE FOCUS",
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeOsIndigo700,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = active.title,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = LifeOsIndigo700,
                        maxLines = 2
                    )
                }
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
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = LifeOsSlate600,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            letterSpacing = 0.6.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * BEHAVIOR MODEL: LIFEOS IS LEARNING
 * Real behavioral signals with calibration progress.
 */
@Composable
private fun LifeOsIsLearningSection(model: UserBehaviorModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LifeOsEyebrow(text = "LIFEOS IS LEARNING")
            Text(
                text = "Adaptive Feedback Loop",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        val totalSignals = model.totalTasksCompleted + model.totalTasksAbandoned
        val calibrationRatio = (totalSignals / 3f).coerceIn(0f, 1f)
        val calibrationPct = (calibrationRatio * 100).toInt()

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
            progress = { calibrationRatio },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (!model.hasSufficientData) {
            Text(
                text = "Still learning your work patterns. As you complete or abandon tasks, LIFEOS calibrates when you focus best and which categories gain momentum.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
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
