package com.mrashish18.lifeos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrashish18.lifeos.core.model.LifeOsNotification
import com.mrashish18.lifeos.core.model.NotificationCategory
import com.mrashish18.lifeos.ui.navigation.LifeOsDestination

/**
 * Modal Bottom Sheet presenting the unified LIFEOS Notification Center.
 * Styled in the established light pastel theme (#F8FAFC / #F5F3FF) with category filters,
 * read/unread state tracking, and deep link navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterBottomSheet(
    notifications: List<LifeOsNotification>,
    unreadCount: Int,
    selectedCategory: NotificationCategory?,
    onSelectCategory: (NotificationCategory?) -> Unit,
    onMarkAsRead: (String) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onNavigateTo: (LifeOsDestination) -> Unit,
    onDismiss: () -> Unit,
    isDarkMode: Boolean = false,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDarkMode) Color(0xFF111827) else Color(0xFFF8FAFC),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(if (isDarkMode) Color(0xFF475569) else Color(0xFFCBD5E1))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            // Header: Title, Unread badge pill, Mark all as read, and Close
            NotificationCenterHeader(
                unreadCount = unreadCount,
                onMarkAllAsRead = onMarkAllAsRead,
                onClose = onDismiss,
                isDarkMode = isDarkMode
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category filter chips
            NotificationCategoryFilterRow(
                selectedCategory = selectedCategory,
                onSelectCategory = onSelectCategory,
                isDarkMode = isDarkMode
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Notifications List or Empty State
            if (notifications.isEmpty()) {
                NotificationCenterEmptyState(
                    hasCategoryFilter = selectedCategory != null,
                    isDarkMode = isDarkMode
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = notifications,
                        key = { it.id }
                    ) { notification ->
                        NotificationCard(
                            notification = notification,
                            onCardClick = {
                                onMarkAsRead(notification.id)
                                notification.destination?.let { dest ->
                                    onNavigateTo(dest)
                                    onDismiss()
                                }
                            },
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCenterHeader(
    unreadCount: Int,
    onMarkAllAsRead: () -> Unit,
    onClose: () -> Unit,
    isDarkMode: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Notifications",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                letterSpacing = (-0.3).sp
            )

            if (unreadCount > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEEF2FF),
                    border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF4338CA) else Color(0xFFC7D2FE))
                ) {
                    Text(
                        text = "$unreadCount new",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (unreadCount > 0) {
                Text(
                    text = "Mark all as read",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onMarkAllAsRead)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close notifications",
                    tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun NotificationCategoryFilterRow(
    selectedCategory: NotificationCategory?,
    onSelectCategory: (NotificationCategory?) -> Unit,
    isDarkMode: Boolean = false
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryFilterChip(
            label = "All",
            isSelected = selectedCategory == null,
            onClick = { onSelectCategory(null) },
            isDarkMode = isDarkMode
        )

        NotificationCategory.values().forEach { category ->
            val label = when (category) {
                NotificationCategory.PERSONAL -> "Personal"
                NotificationCategory.TRUTH -> "Truth"
                NotificationCategory.MESH -> "Mesh"
                NotificationCategory.LEARNING -> "Learning"
                NotificationCategory.EMERGENCY -> "Emergency"
            }
            CategoryFilterChip(
                label = label,
                isSelected = selectedCategory == category,
                onClick = { onSelectCategory(category) },
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
private fun CategoryFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDarkMode: Boolean = false
) {
    val backgroundColor = if (isSelected) {
        if (isDarkMode) Color(0xFF6366F1) else Color(0xFF4338CA)
    } else {
        if (isDarkMode) Color(0xFF1E293B) else Color.White
    }
    val textColor = if (isSelected) Color.White else if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF64748B)
    val borderColor = if (isSelected) {
        if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA)
    } else {
        if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun NotificationCard(
    notification: LifeOsNotification,
    onCardClick: () -> Unit,
    isDarkMode: Boolean = false
) {
    val spec = notificationCategorySpec(notification.category)
    val cardBg = if (isDarkMode) {
        if (!notification.isRead) Color(0xFF1E293B) else Color(0xFF172033)
    } else {
        if (!notification.isRead) Color(0xFFFBFBFE) else Color.White
    }
    val cardBorder = if (isDarkMode) {
        if (!notification.isRead) Color(0xFF6366F1) else Color(0xFF334155)
    } else {
        if (!notification.isRead) Color(0xFFC7D2FE) else Color(0xFFE2E8F0)
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = BorderStroke(1.dp, cardBorder),
        shadowElevation = if (!notification.isRead) 2.dp else 0.5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                onClick = onCardClick,
                role = Role.Button
            )
            .semantics {
                role = Role.Button
                contentDescription = "${notification.category.name}: ${notification.title}. ${notification.message}. ${if (notification.isRead) "Read" else "Unread"}"
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Category Icon Pill
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDarkMode) spec.iconTint.copy(alpha = 0.2f) else spec.backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = spec.icon,
                    contentDescription = null,
                    tint = if (isDarkMode) spec.iconTint.copy(alpha = 0.95f) else spec.iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Main Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = spec.categoryLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = spec.iconTint,
                            letterSpacing = 0.4.sp
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "•  ${formatRelativeTime(notification.timestampEpochMillis)}",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = notification.title,
                    fontSize = 13.5.sp,
                    fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF64748B),
                    lineHeight = 16.sp
                )

                notification.destination?.let { dest ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "View in ${dest.title} ›",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA)
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCenterEmptyState(
    hasCategoryFilter: Boolean,
    isDarkMode: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (isDarkMode) Color(0xFF172033) else Color.White,
        border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (hasCategoryFilter) "No notifications in this category" else "All caught up!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (hasCategoryFilter) {
                    "Try selecting 'All' to see past activity across other pillars."
                } else {
                    "LIFEOS will surface important changes,\ndecisions, and outcomes here."
                },
                fontSize = 12.sp,
                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                lineHeight = 17.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

private data class CategorySpec(
    val categoryLabel: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val iconTint: Color
)

private fun notificationCategorySpec(category: NotificationCategory): CategorySpec {
    return when (category) {
        NotificationCategory.PERSONAL -> CategorySpec(
            categoryLabel = "PERSONAL",
            icon = Icons.Default.CheckCircle,
            backgroundColor = Color(0xFFEEF2FF),
            iconTint = Color(0xFF4338CA)
        )
        NotificationCategory.TRUTH -> CategorySpec(
            categoryLabel = "TRUTH",
            icon = Icons.AutoMirrored.Filled.FactCheck,
            backgroundColor = Color(0xFFDCFCE7),
            iconTint = Color(0xFF16A34A)
        )
        NotificationCategory.MESH -> CategorySpec(
            categoryLabel = "MESH",
            icon = Icons.Default.Hub,
            backgroundColor = Color(0xFFE0F2FE),
            iconTint = Color(0xFF0284C7)
        )
        NotificationCategory.LEARNING -> CategorySpec(
            categoryLabel = "LEARNING",
            icon = Icons.Default.AutoAwesome,
            backgroundColor = Color(0xFFF3E8FF),
            iconTint = Color(0xFF7E22CE)
        )
        NotificationCategory.EMERGENCY -> CategorySpec(
            categoryLabel = "EMERGENCY",
            icon = Icons.Default.Warning,
            backgroundColor = Color(0xFFFFE4E6),
            iconTint = Color(0xFFE11D48)
        )
    }
}

/**
 * Format relative time (e.g. "Just now", "2m ago", "1h ago", "Yesterday").
 */
private fun formatRelativeTime(epochMillis: Long): String {
    val diffSeconds = (System.currentTimeMillis() - epochMillis) / 1000
    return when {
        diffSeconds < 60 -> "Just now"
        diffSeconds < 3600 -> "${diffSeconds / 60}m ago"
        diffSeconds < 86400 -> "${diffSeconds / 3600}h ago"
        diffSeconds < 172800 -> "Yesterday"
        else -> "${diffSeconds / 86400}d ago"
    }
}
