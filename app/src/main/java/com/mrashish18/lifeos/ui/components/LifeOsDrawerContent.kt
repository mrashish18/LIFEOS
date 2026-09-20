package com.mrashish18.lifeos.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrashish18.lifeos.feature.settings.SettingsModal
import com.mrashish18.lifeos.ui.navigation.LifeOsDestination

@Composable
fun LifeOsDrawerContent(
    currentDestination: LifeOsDestination,
    isDarkMode: Boolean,
    versionName: String,
    versionCode: Int,
    systemStatus: String = "ACTIVE",
    onSelectDestination: (LifeOsDestination) -> Unit,
    onOpenModal: (SettingsModal) -> Unit,
    onOpenNotifications: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()

    val surfaceColor = if (isDarkMode) Color(0xFF111827) else Color(0xFFF8FAFC)
    val cardBgColor = if (isDarkMode) Color(0xFF172033) else Color.White
    val textPrimary = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569)
    val textMuted = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8)
    val borderColor = if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0)

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp),
        color = surfaceColor,
        shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        tonalElevation = 6.dp,
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // ================= HEADER =================
            DrawerHeader(
                isDarkMode = isDarkMode,
                systemStatus = systemStatus,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                cardBgColor = cardBgColor,
                borderColor = borderColor
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = borderColor.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(12.dp))

            // ================= SECTION 1: PRIMARY NAVIGATION =================
            DrawerSectionHeader(title = "LIFEOS", color = textMuted)
            Spacer(modifier = Modifier.height(6.dp))

            DrawerNavigationItem(
                title = "Home",
                subtitle = "Intelligence dashboard",
                icon = Icons.Outlined.Home,
                isSelected = currentDestination == LifeOsDestination.DASHBOARD,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                onClick = {
                    onSelectDestination(LifeOsDestination.DASHBOARD)
                    onCloseDrawer()
                }
            )

            DrawerNavigationItem(
                title = "Tasks",
                subtitle = "Daily actions & focus",
                icon = Icons.Outlined.CheckCircle,
                isSelected = currentDestination == LifeOsDestination.TASKS,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                onClick = {
                    onSelectDestination(LifeOsDestination.TASKS)
                    onCloseDrawer()
                }
            )

            DrawerNavigationItem(
                title = "Goals",
                subtitle = "Strategic objectives",
                icon = Icons.Outlined.TrackChanges,
                isSelected = currentDestination == LifeOsDestination.GOALS,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                onClick = {
                    onSelectDestination(LifeOsDestination.GOALS)
                    onCloseDrawer()
                }
            )

            DrawerNavigationItem(
                title = "Intelligence",
                subtitle = "Cognitive learning loop",
                icon = Icons.Outlined.AutoAwesome,
                isSelected = currentDestination == LifeOsDestination.INTELLIGENCE,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                onClick = {
                    onSelectDestination(LifeOsDestination.INTELLIGENCE)
                    onCloseDrawer()
                }
            )

            DrawerNavigationItem(
                title = "RealityCheck",
                subtitle = "Claim truth verification",
                icon = Icons.AutoMirrored.Outlined.FactCheck,
                isSelected = currentDestination == LifeOsDestination.REALITY_CHECK,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                onClick = {
                    onSelectDestination(LifeOsDestination.REALITY_CHECK)
                    onCloseDrawer()
                }
            )

            DrawerNavigationItem(
                title = "RescueMesh",
                subtitle = "Resilient emergency network",
                icon = Icons.Outlined.Hub,
                isSelected = currentDestination == LifeOsDestination.RESILIENCE,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                onClick = {
                    onSelectDestination(LifeOsDestination.RESILIENCE)
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = borderColor.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(12.dp))

            // ================= SECTION 2: INTELLIGENCE =================
            DrawerSectionHeader(title = "INTELLIGENCE", color = textMuted)
            Spacer(modifier = Modifier.height(6.dp))

            DrawerActionItem(
                title = "How to Use LIFEOS",
                icon = Icons.Outlined.HelpOutline,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                onClick = {
                    onCloseDrawer()
                    onOpenModal(SettingsModal.HOW_TO_USE)
                }
            )

            DrawerActionItem(
                title = "What's New",
                icon = Icons.Outlined.NewReleases,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                onClick = {
                    onCloseDrawer()
                    onOpenModal(SettingsModal.WHATS_NEW)
                }
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = borderColor.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(12.dp))

            // ================= SECTION 3: APP SETTINGS =================
            DrawerSectionHeader(title = "APP SETTINGS", color = textMuted)
            Spacer(modifier = Modifier.height(6.dp))

            DrawerActionItem(
                title = "Notifications",
                icon = Icons.Outlined.Notifications,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                onClick = {
                    onCloseDrawer()
                    onOpenModal(SettingsModal.NOTIFICATIONS)
                }
            )

            DrawerActionItem(
                title = "Appearance",
                icon = Icons.Outlined.Palette,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                onClick = {
                    onCloseDrawer()
                    onOpenModal(SettingsModal.APPEARANCE)
                }
            )

            DrawerActionItem(
                title = "Data & Storage",
                icon = Icons.Outlined.Storage,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                onClick = {
                    onCloseDrawer()
                    onOpenModal(SettingsModal.DATA_STORAGE)
                }
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = borderColor.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(12.dp))

            // ================= SECTION 4: INFORMATION =================
            DrawerSectionHeader(title = "INFORMATION", color = textMuted)
            Spacer(modifier = Modifier.height(6.dp))

            DrawerActionItem(
                title = "FAQ",
                icon = Icons.Outlined.Quiz,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                onClick = {
                    onCloseDrawer()
                    onOpenModal(SettingsModal.FAQ)
                }
            )

            DrawerActionItem(
                title = "Privacy Policy",
                icon = Icons.Outlined.Security,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                onClick = {
                    onCloseDrawer()
                    onOpenModal(SettingsModal.PRIVACY)
                }
            )

            DrawerActionItem(
                title = "Terms & Conditions",
                icon = Icons.Outlined.Description,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                onClick = {
                    onCloseDrawer()
                    onOpenModal(SettingsModal.TERMS)
                }
            )

            DrawerActionItem(
                title = "About LIFEOS",
                icon = Icons.Outlined.Info,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                onClick = {
                    onCloseDrawer()
                    onOpenModal(SettingsModal.ABOUT)
                }
            )

            DrawerActionItem(
                title = "Feedback & Support",
                icon = Icons.Outlined.Feedback,
                isDarkMode = isDarkMode,
                textPrimary = textPrimary,
                onClick = {
                    onCloseDrawer()
                    onOpenModal(SettingsModal.FEEDBACK)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ================= FOOTER =================
            DrawerFooter(
                versionName = versionName,
                versionCode = versionCode,
                textMuted = textMuted
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DrawerHeader(
    isDarkMode: Boolean,
    systemStatus: String,
    textPrimary: Color,
    textSecondary: Color,
    cardBgColor: Color,
    borderColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LIFEOS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = textPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Understand • Decide • Adapt",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = textSecondary
                )
            }

            // Compact Active Status Pill
            Surface(
                color = if (isDarkMode) Color(0xFF064E3B) else Color(0xFFDCFCE7),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF059669) else Color(0xFF16A34A).copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) Color(0xFF34D399) else Color(0xFF16A34A))
                    )
                    Text(
                        text = "ACTIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFD1FAE5) else Color(0xFF15803D),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Adaptive Intelligence Platform",
            fontSize = 12.sp,
            color = Color(0xFF6366F1),
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Three live pillar status chips
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = cardBgColor,
            border = BorderStroke(1.dp, borderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PillarBadge(name = "Personal", status = "Ready", dotColor = Color(0xFF3B82F6), textColor = textSecondary)
                Box(modifier = Modifier.size(1.dp, 16.dp).background(borderColor))
                PillarBadge(name = "Truth", status = "Cached", dotColor = Color(0xFF10B981), textColor = textSecondary)
                Box(modifier = Modifier.size(1.dp, 16.dp).background(borderColor))
                PillarBadge(name = "Mesh", status = "Armed", dotColor = Color(0xFF06B6D4), textColor = textSecondary)
            }
        }
    }
}

@Composable
private fun PillarBadge(
    name: String,
    status: String,
    dotColor: Color,
    textColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Text(
                text = name,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
        Text(
            text = status,
            fontSize = 9.sp,
            color = dotColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun DrawerSectionHeader(title: String, color: Color) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = color,
        modifier = Modifier.padding(start = 6.dp, bottom = 2.dp)
    )
}

@Composable
private fun DrawerNavigationItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    isDarkMode: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    onClick: () -> Unit
) {
    val activeBg = if (isDarkMode) Color(0xFF1E1B4B) else Color(0xFFEEF2FF)
    val activeBorder = if (isDarkMode) Color(0xFF4338CA) else Color(0xFFC7D2FE)
    val activeText = if (isDarkMode) Color(0xFFC7D2FE) else Color(0xFF4338CA)
    val activeIcon = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4F46E5)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .semantics {
                role = Role.Button
                contentDescription = "$title navigation: $subtitle"
            },
        color = if (isSelected) activeBg else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) BorderStroke(1.dp, activeBorder) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) activeIcon else textSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) activeText else textPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 10.5.sp,
                    color = textSecondary
                )
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(activeIcon)
                )
            }
        }
    }
}

@Composable
private fun DrawerActionItem(
    title: String,
    icon: ImageVector,
    isDarkMode: Boolean,
    textPrimary: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .semantics {
                role = Role.Button
                contentDescription = "Open $title"
            },
        color = Color.Transparent,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF6366F1),
                modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                color = textPrimary
            )
        }
    }
}

@Composable
private fun DrawerFooter(
    versionName: String,
    versionCode: Int,
    textMuted: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "LIFEOS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textMuted,
            letterSpacing = 0.5.sp
        )
        Text(
            text = "Understand. Decide. Adapt.",
            fontSize = 10.sp,
            color = textMuted.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "Version $versionName (Build $versionCode) • Android",
            fontSize = 9.5.sp,
            color = textMuted.copy(alpha = 0.6f)
        )
    }
}
