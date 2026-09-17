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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LifeOsIndigo700,
            contentColor = Color.White,
            disabledContainerColor = LifeOsSlate200,
            disabledContentColor = LifeOsSlate500
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
                color = if (enabled) Color.White else LifeOsSlate500
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
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LifeOsSlate100,
            contentColor = LifeOsSlate800,
            disabledContainerColor = LifeOsSlate100.copy(alpha = 0.5f),
            disabledContentColor = LifeOsSlate400
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsSlate200),
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
                color = if (enabled) LifeOsSlate800 else LifeOsSlate400
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
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (enabled) LifeOsIndigo700.copy(alpha = 0.4f) else LifeOsSlate300
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = if (enabled) LifeOsIndigo700 else LifeOsSlate400
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
    val (bg, textColor, dotColor) = when (status) {
        TaskStatus.IN_PROGRESS -> Triple(LifeOsBlue50, LifeOsBlue700, LifeOsBlue700)
        TaskStatus.PENDING -> Triple(LifeOsIndigo50, LifeOsIndigo700, LifeOsIndigo700)
        TaskStatus.COMPLETED -> Triple(LifeOsGreen50, LifeOsGreen700, LifeOsGreen700)
        TaskStatus.POSTPONED -> Triple(LifeOsAmber50, LifeOsAmber700, LifeOsAmber700)
        TaskStatus.ABANDONED -> Triple(LifeOsSlate100, LifeOsSlate700, LifeOsSlate500)
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
    val (bg, text) = when (priority) {
        TaskPriority.URGENT -> LifeOsRed50 to LifeOsRed700
        TaskPriority.HIGH -> LifeOsAmber50 to LifeOsAmber700
        TaskPriority.MEDIUM -> LifeOsBlue50 to LifeOsBlue700
        TaskPriority.LOW -> LifeOsSlate100 to LifeOsSlate700
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
    val (bg, text) = when (category) {
        TaskCategory.WORK -> LifeOsIndigo50 to LifeOsIndigo700
        TaskCategory.PERSONAL -> LifeOsTeal50 to LifeOsTeal700
        TaskCategory.HEALTH -> LifeOsGreen50 to LifeOsGreen700
        TaskCategory.LEARNING -> LifeOsBlue50 to LifeOsBlue700
        TaskCategory.GENERAL -> LifeOsSlate100 to LifeOsSlate700
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
