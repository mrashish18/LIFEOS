package com.mrashish18.lifeos.feature.goals

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mrashish18.lifeos.ui.components.LifeOsEyebrow
import com.mrashish18.lifeos.ui.components.LifeOsFilterPill
import com.mrashish18.lifeos.ui.components.ScenicGoalBanner
import com.mrashish18.lifeos.ui.theme.*

@Composable
fun GoalsScreen(modifier: Modifier = Modifier) {
    var selectedFilter by remember { mutableStateOf("All") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Section Header matching Screen 4
        Column {
            Text(
                text = "GOALS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E1B4B),
                letterSpacing = (-0.3).sp
            )
            Text(
                text = "LONG-TERM DIRECTION",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4F46E5),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Small steps. A bigger you.",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        // 1. Scenic Mountain Banner with quote matching Screen 4
        ScenicGoalBanner()

        // 2. Filter Pills: All, Active, Completed
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Active", "Completed").forEach { filter ->
                val isSelected = selectedFilter == filter
                val pillBg by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFF4338CA) else Color(0xFFF8FAFC),
                    label = "goalFilterBg"
                )
                val pillTextColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color(0xFF475569),
                    label = "goalFilterText"
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(pillBg)
                        .then(
                            if (!isSelected) Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                            else Modifier
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = pillTextColor
                    )
                }
            }
        }

        // 3. Strategic Objectives Cards matching Screen 4 reference with reactive filtering
        val goals = remember {
            listOf(
                GoalItem(
                    id = "goal_1",
                    title = "Adaptive Autonomous Productivity",
                    tag = "Q4 2026",
                    tagBg = Color(0xFFDCFCE7),
                    tagColor = Color(0xFF16A34A),
                    description = "Turn daily execution into long-term compounding growth.",
                    progress = 0.30f,
                    isCompleted = false,
                    milestones = "2 / 6 milestones"
                ),
                GoalItem(
                    id = "goal_2",
                    title = "Circadian Rhythm & Workload Pacing",
                    tag = "Continuous",
                    tagBg = Color(0xFFEFF6FF),
                    tagColor = Color(0xFF2563EB),
                    description = "Maintain healthy balance and prevent burnout.",
                    progress = 0.60f,
                    isCompleted = false,
                    milestones = "3 / 5 milestones"
                ),
                GoalItem(
                    id = "goal_3",
                    title = "Deep Work Focus Habituation",
                    tag = "Ongoing",
                    tagBg = Color(0xFFF1F5F9),
                    tagColor = Color(0xFF64748B),
                    description = "Build consistent, distraction-free focus sessions.",
                    progress = 0.20f,
                    isCompleted = false,
                    milestones = "1 / 5 milestones"
                ),
                GoalItem(
                    id = "goal_4",
                    title = "Core Architecture Baseline",
                    tag = "Delivered",
                    tagBg = Color(0xFFDCFCE7),
                    tagColor = Color(0xFF16A34A),
                    description = "Room persistence, Context Engine, and Decision Engine baseline established.",
                    progress = 1.0f,
                    isCompleted = true,
                    milestones = "5 / 5 milestones"
                ),
                GoalItem(
                    id = "goal_5",
                    title = "Store-and-Forward Mesh Hardening",
                    tag = "Delivered",
                    tagBg = Color(0xFFDCFCE7),
                    tagColor = Color(0xFF16A34A),
                    description = "Hop-limited emergency mesh routing with deterministic deduplication.",
                    progress = 1.0f,
                    isCompleted = true,
                    milestones = "4 / 4 milestones"
                )
            )
        }

        val filteredGoals = when (selectedFilter) {
            "Active" -> goals.filter { !it.isCompleted }
            "Completed" -> goals.filter { it.isCompleted }
            else -> goals
        }

        if (filteredGoals.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🎯", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No goals in this view.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Switch filters to view active and completed strategic objectives.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            filteredGoals.forEach { goal ->
                ReferenceGoalCard(
                    title = goal.title,
                    tag = goal.tag,
                    tagBg = goal.tagBg,
                    tagColor = goal.tagColor,
                    description = goal.description,
                    progress = goal.progress,
                    milestones = goal.milestones
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

private data class GoalItem(
    val id: String,
    val title: String,
    val tag: String,
    val tagBg: Color,
    val tagColor: Color,
    val description: String,
    val progress: Float,
    val isCompleted: Boolean,
    val milestones: String
)

@Composable
private fun ReferenceGoalCard(
    title: String,
    tag: String,
    tagBg: Color,
    tagColor: Color,
    description: String,
    progress: Float,
    milestones: String? = null
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "goalCardProgress"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Text(
                text = title,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                letterSpacing = (-0.2).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Tag Pill & Milestone Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(tagBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = tag,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagColor
                    )
                }

                milestones?.let { ms ->
                    Text(
                        text = ms,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF475569),
                lineHeight = 16.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar + Percentage Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(7.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                ) {
                    if (animatedProgress > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .height(7.dp)
                                .clip(CircleShape)
                                .background(
                                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        listOf(Color(0xFF10B981), Color(0xFF06B6D4))
                                    )
                                )
                        )
                    }
                }

                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4338CA)
                )
            }
        }
    }
}

@Composable
private fun StrategicGoalCard(
    category: String,
    title: String,
    horizon: String,
    progress: Float,
    completedMilestones: Int,
    totalMilestones: Int,
    dailyTaskLink: String,
    currentMilestoneName: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsSlate200.copy(alpha = 0.8f)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = LifeOsIndigo50
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeOsIndigo700,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = horizon,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Milestones: $completedMilestones / $totalMilestones",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeOsIndigo700,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(LifeOsSlate100)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0.04f, 1f))
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(LifeOsGradients.primary)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = LifeOsSlate50.copy(alpha = 0.8f),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsSlate200.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("▸", color = LifeOsIndigo700, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = currentMilestoneName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = dailyTaskLink,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
