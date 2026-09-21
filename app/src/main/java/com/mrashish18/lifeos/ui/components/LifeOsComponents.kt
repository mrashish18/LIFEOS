package com.mrashish18.lifeos.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrashish18.lifeos.core.model.TaskCategory
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.ui.theme.*

/**
 * Standard card surface with clean border stroke and light elevation.
 */
@Composable
fun LifeOsCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    elevation: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val cardModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onClick)
            .border(1.dp, borderColor, shape)
    } else {
        modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, borderColor, shape)
    }

    ElevatedCard(
        modifier = cardModifier,
        shape = shape,
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation)
    ) {
        content()
    }
}

/**
 * Consistent section header with optional category tag/eyebrow and trailing action.
 */
@Composable
fun LifeOsSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    subtitle: String? = null,
    trailingAction: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                if (eyebrow != null) {
                    Text(
                        text = eyebrow.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (trailingAction != null) {
                trailingAction()
            }
        }
    }
}

/**
 * Section eyebrow label used on open canvas layouts.
 */
@Composable
fun LifeOsEyebrow(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.4.sp,
        modifier = modifier
    )
}

/**
 * High-emphasis primary action button.
 * Uses 50dp height, 16dp radius, tactile feedback, bold label, and prominent styling.
 */
@Composable
fun LifeOsPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    val palette = LocalLifeOsSemanticPalette.current
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = palette.primary,
            contentColor = Color.White,
            disabledContainerColor = palette.surfaceInput,
            disabledContentColor = palette.textMuted
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                ),
                color = if (enabled) Color.White else palette.textMuted
            )
        }
    }
}

/**
 * Tonal secondary action button.
 * Uses 46dp height, 14dp radius, high-contrast readable label.
 */
@Composable
fun LifeOsSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isDarkMode: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    val palette = LocalLifeOsSemanticPalette.current
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = palette.surfaceInput,
            contentColor = palette.textPrimary,
            disabledContainerColor = palette.surfaceInput.copy(alpha = 0.5f),
            disabledContentColor = palette.textMuted
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.border),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (enabled) palette.textPrimary else palette.textMuted
            )
        }
    }
}

/**
 * Subtle outlined button.
 */
@Composable
fun LifeOsOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val palette = LocalLifeOsSemanticPalette.current
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (enabled) palette.primary.copy(alpha = 0.45f) else palette.border
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = if (enabled) palette.primary else palette.textMuted
        )
    }
}

/**
 * Task Status Badge.
 */
@Composable
fun LifeOsStatusChip(
    status: TaskStatus,
    modifier: Modifier = Modifier
) {
    val palette = LocalLifeOsSemanticPalette.current
    val (bg, textColor, dotColor) = when (status) {
        TaskStatus.IN_PROGRESS -> Triple(palette.infoSurface, palette.info, palette.info)
        TaskStatus.PENDING -> Triple(palette.infoSurface, palette.primary, palette.primary)
        TaskStatus.COMPLETED -> Triple(palette.successSurface, palette.success, palette.success)
        TaskStatus.POSTPONED -> Triple(palette.warningSurface, palette.warning, palette.warning)
        TaskStatus.ABANDONED -> Triple(palette.neutralSurface, palette.textSecondary, palette.textMuted)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = bg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = when (status) {
                    TaskStatus.IN_PROGRESS -> "In Progress"
                    TaskStatus.PENDING -> "Pending"
                    TaskStatus.COMPLETED -> "Completed"
                    TaskStatus.POSTPONED -> "Postponed"
                    TaskStatus.ABANDONED -> "Abandoned"
                },
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

/**
 * Task Priority Badge.
 */
@Composable
fun LifeOsPriorityBadge(
    priority: TaskPriority,
    modifier: Modifier = Modifier
) {
    val palette = LocalLifeOsSemanticPalette.current
    val (bg, text) = when (priority) {
        TaskPriority.URGENT -> palette.dangerSurface to palette.danger
        TaskPriority.HIGH -> palette.warningSurface to palette.warning
        TaskPriority.MEDIUM -> palette.infoSurface to palette.info
        TaskPriority.LOW -> palette.neutralSurface to palette.textSecondary
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = bg
    ) {
        Text(
            text = priority.name,
            style = MaterialTheme.typography.labelSmall,
            color = text,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

/**
 * Task Category Badge.
 */
@Composable
fun LifeOsCategoryBadge(
    category: TaskCategory,
    modifier: Modifier = Modifier
) {
    val palette = LocalLifeOsSemanticPalette.current
    val (bg, text) = when (category) {
        TaskCategory.WORK -> palette.infoSurface to palette.primary
        TaskCategory.PERSONAL -> palette.successSurface to palette.truth
        TaskCategory.HEALTH -> palette.successSurface to palette.success
        TaskCategory.LEARNING -> palette.infoSurface to palette.info
        TaskCategory.GENERAL -> palette.neutralSurface to palette.textSecondary
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = bg
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            color = text,
            fontWeight = FontWeight.Medium,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

/**
 * Calibrated Confidence meter with clean sleek gauge.
 */
@Composable
fun LifeOsConfidenceIndicator(
    confidenceScore: Double,
    modifier: Modifier = Modifier,
    label: String = "Confidence",
    trackColor: Color = MaterialTheme.colorScheme.outlineVariant,
    indicatorColor: Color = LifeOsIndigo700
) {
    val percentage = (confidenceScore * 100).toInt()
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
                fontSize = 10.sp
            )
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = indicatorColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { confidenceScore.toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = indicatorColor,
            trackColor = trackColor
        )
    }
}

/**
 * Clean empty state placeholder.
 */
@Composable
fun LifeOsEmptyState(
    iconSymbol: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionButton: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iconSymbol,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
        if (actionButton != null) {
            Spacer(modifier = Modifier.height(18.dp))
            actionButton()
        }
    }
}

/**
 * Open list row for tasks, with hairline divider instead of a boxed card.
 */
@Composable
fun LifeOsTaskRow(
    title: String,
    status: TaskStatus,
    priority: TaskPriority,
    category: TaskCategory,
    modifier: Modifier = Modifier,
    description: String = "",
    durationMinutes: Int? = null,
    onClick: (() -> Unit)? = null,
    primaryAction: @Composable (() -> Unit)? = null,
    secondaryActions: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                LifeOsPriorityBadge(priority = priority)
                LifeOsCategoryBadge(category = category)
                durationMinutes?.let { min ->
                    Text(
                        text = "· ${min}m",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            LifeOsStatusChip(status = status)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (description.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }

        if (primaryAction != null || secondaryActions != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    if (primaryAction != null) primaryAction()
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (secondaryActions != null) secondaryActions()
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

/**
 * Context Strip: Displays current situation signals in a clean, elevated capsule.
 */
@Composable
fun ContextStrip(
    day: String,
    time: String,
    network: String,
    stateLabel: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(LifeOsGreen600)
                )
                Text(
                    text = "$day · $time · $network",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }
            if (stateLabel != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = LifeOsIndigo50
                ) {
                    Text(
                        text = stateLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeOsIndigo700,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Status Pill badge with subtle dot.
 */
@Composable
fun StatusPill(
    label: String,
    color: Color = LifeOsIndigo700,
    backgroundColor: Color = LifeOsIndigo50,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

/**
 * Flagship Adaptive Recommendation Hero for the Dashboard.
 * Answers "What matters right now?" with high visual authority.
 */
@Composable
fun AdaptiveRecommendationHero(
    title: String,
    confidenceScore: Double,
    explanation: String,
    reasons: List<String>,
    onStartFocus: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, LifeOsIndigo600.copy(alpha = 0.35f)),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 13.sp
                    )
                    Text(
                        text = "WHAT MATTERS NOW",
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeOsIndigo700,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LifeOsIndigo50
                ) {
                    Text(
                        text = "${(confidenceScore * 100).toInt()}% CONFIDENCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeOsIndigo700,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 26.sp
            )

            if (explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }

            if (reasons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    reasons.take(3).forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "•",
                                color = LifeOsIndigo700,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = reason,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onStartFocus,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LifeOsIndigo700,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "▶  START FOCUS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                if (onDismiss != null) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(46.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text("Later", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

/**
 * 3-dot contextual overflow menu for secondary task actions.
 */
@Composable
fun LifeOsOverflowMenu(
    onPostpone: () -> Unit,
    onAbandon: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.size(36.dp)
        ) {
            Text(
                text = "⋮",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (onEdit != null) {
                DropdownMenuItem(
                    text = { Text("Edit Task") },
                    onClick = {
                        expanded = false
                        onEdit()
                    }
                )
            }
            DropdownMenuItem(
                text = { Text("Postpone to Later") },
                onClick = {
                    expanded = false
                    onPostpone()
                }
            )
            DropdownMenuItem(
                text = { Text("Abandon") },
                onClick = {
                    expanded = false
                    onAbandon()
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Delete Task", color = LifeOsRed600) },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}

/**
 * Emergency Message Lifecycle Timeline tracker.
 * Visually shows state: CREATED -> QUEUED -> RELAYING -> SENT -> DELIVERED
 */
@Composable
fun MessageLifecycleTimeline(
    status: String,
    modifier: Modifier = Modifier
) {
    val palette = LocalLifeOsSemanticPalette.current
    val steps = listOf("CREATED", "QUEUED", "RELAY", "SENT", "DELIVERED")
    val currentIndex = when (status.uppercase()) {
        "DRAFT" -> 0
        "CREATED" -> 0
        "QUEUED" -> 1
        "RELAYING" -> 2
        "SENT" -> 3
        "DELIVERED" -> 4
        "FAILED", "EXPIRED", "DUPLICATE" -> -1
        else -> 1
    }

    val isFailed = status.uppercase() in listOf("FAILED", "EXPIRED", "DUPLICATE")

    Column(modifier = modifier.fillMaxWidth()) {
        // Circles & Connecting lines Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, _ ->
                val isCompleted = !isFailed && index <= currentIndex
                val isCurrent = !isFailed && index == currentIndex

                Box(
                    modifier = Modifier
                        .size(if (isCurrent) 18.dp else 12.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCurrent -> palette.primary
                                isCompleted -> palette.success
                                else -> MaterialTheme.colorScheme.outlineVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted && !isCurrent) {
                        Text("✓", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                    } else if (isCurrent) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }

                if (index < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(
                                if (!isFailed && index < currentIndex) palette.success
                                else MaterialTheme.colorScheme.outlineVariant
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Evenly spaced labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEachIndexed { index, stepName ->
                val isCompleted = !isFailed && index <= currentIndex
                val isCurrent = !isFailed && index == currentIndex

                Text(
                    text = stepName,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    color = if (isCurrent) palette.primary else if (isCompleted) palette.success else MaterialTheme.colorScheme.outline,
                    maxLines = 1
                )
            }
        }

        if (isFailed) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = palette.dangerSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "STATUS: $status",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.danger,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

/**
 * Modern gradient action button matching reference style.
 */
@Composable
fun LifeOsGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: Brush = LifeOsGradients.primary,
    icon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true
) {
    val palette = LocalLifeOsSemanticPalette.current
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(if (enabled) gradient else Brush.linearGradient(listOf(palette.surfaceInput, palette.border)))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * Filter capsule pill matching reference style.
 */
@Composable
fun LifeOsFilterPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeGradient: Brush = LifeOsGradients.primary
) {
    val palette = LocalLifeOsSemanticPalette.current
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.surface,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, palette.border)
    ) {
        Box(
            modifier = Modifier
                .then(if (isSelected) Modifier.background(activeGradient) else Modifier)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isSelected) Color.White else palette.textSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
    }
}

/**
 * 2x2 metric tile for current situation.
 */
@Composable
fun LifeOsMetricTile(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val palette = LocalLifeOsSemanticPalette.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.borderSubtle),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = label.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textMuted,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = value,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
            }
        }
    }
}

/**
 * Atmospheric dusk & mountain vector header for Dashboard matching reference Screen 1.
 */
@Composable
fun ScenicMountainHeader(
    systemStatus: String = "ACTIVE",
    dateString: String = "Thu, 18 Sep 2026",
    subtitle: String = "Your day, intelligently organized.",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LifeOsGradients.heroAtmosphere)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .matchParentSize()
            ) {
                val w = size.width
                val h = size.height

                // Moon / star glow crest
                drawCircle(
                    color = Color(0xFF6366F1).copy(alpha = 0.25f),
                    radius = w * 0.35f,
                    center = androidx.compose.ui.geometry.Offset(w * 0.85f, h * 0.2f)
                )

                // Background mountain ridge
                val bgPath = Path().apply {
                    moveTo(0f, h * 0.75f)
                    cubicTo(w * 0.25f, h * 0.5f, w * 0.45f, h * 0.7f, w * 0.7f, h * 0.45f)
                    lineTo(w, h * 0.65f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(bgPath, color = Color(0xFF4338CA).copy(alpha = 0.45f))

                // Foreground mountain ridge
                val fgPath = Path().apply {
                    moveTo(0f, h * 0.85f)
                    cubicTo(w * 0.3f, h * 0.65f, w * 0.6f, h * 0.85f, w, h * 0.7f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(fgPath, color = Color(0xFF1E1B4B).copy(alpha = 0.85f))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LIFEOS",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "ADAPTIVE INTELLIGENCE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF818CF8),
                            letterSpacing = 1.2.sp
                        )
                    }

                    // System Active Badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E1B4B).copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "SYSTEM ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.6.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Understand. Decide. Adapt.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dateString,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFC7D2FE)
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

/**
 * Scenic Goals Banner with winding road and quote overlay matching reference Screen 4.
 */
@Composable
fun ScenicGoalBanner(
    quote: String = "\"Disciplined today.\nA better tomorrow.\"",
    author: String = "— LIFEOS STRATEGIC ENGINE",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(146.dp),
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 4.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Alpine twilight emerald sky gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF062C24),
                            Color(0xFF064E3B),
                            Color(0xFF047857),
                            Color(0xFF0D9488),
                            Color(0xFF10B981).copy(alpha = 0.65f)
                        )
                    )
                )

                // Alpine celestial starlight
                val stars = listOf(
                    Offset(w * 0.12f, h * 0.14f) to 1.6f,
                    Offset(w * 0.28f, h * 0.22f) to 1.2f,
                    Offset(w * 0.44f, h * 0.12f) to 2.0f,
                    Offset(w * 0.68f, h * 0.16f) to 1.5f,
                    Offset(w * 0.88f, h * 0.20f) to 1.4f
                )
                stars.forEach { (pos, r) ->
                    drawCircle(
                        color = Color.White.copy(alpha = 0.80f),
                        radius = r.dp.toPx(),
                        center = pos
                    )
                }

                // Aurora emerald glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF34D399).copy(alpha = 0.40f), Color(0xFF059669).copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(w * 0.75f, h * 0.50f),
                        radius = w * 0.45f
                    ),
                    radius = w * 0.45f,
                    center = Offset(w * 0.75f, h * 0.50f)
                )

                // Mountain silhouettes
                val mtnPath = Path().apply {
                    moveTo(0f, h * 0.65f)
                    lineTo(w * 0.30f, h * 0.28f)
                    lineTo(w * 0.58f, h * 0.58f)
                    lineTo(w * 0.82f, h * 0.32f)
                    lineTo(w, h * 0.68f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    path = mtnPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF065F46).copy(alpha = 0.85f), Color(0xFF022C22))
                    )
                )

                // Winding green path ascending to the peak
                val roadPath = Path().apply {
                    moveTo(w * 0.42f, h * 0.38f)
                    cubicTo(w * 0.38f, h * 0.58f, w * 0.62f, h * 0.72f, w * 0.48f, h)
                    lineTo(w * 0.60f, h)
                    cubicTo(w * 0.74f, h * 0.72f, w * 0.48f, h * 0.58f, w * 0.48f, h * 0.38f)
                    close()
                }
                drawPath(
                    path = roadPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF34D399), Color(0xFF059669))
                    )
                )
            }

            // Typography & pill overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = quote,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 21.sp,
                        letterSpacing = (-0.2).sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = author,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFA7F3D0),
                        letterSpacing = 0.6.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.5.dp)
                ) {
                    Text(
                        text = "\uD83C\uDFAF Strategic Compounding Framework \u2192",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}

