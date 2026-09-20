package com.mrashish18.lifeos.feature.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.mrashish18.lifeos.domain.model.ThemeMode
import com.mrashish18.lifeos.domain.model.UserSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsModalContainer(
    activeModal: SettingsModal,
    userSettings: UserSettings,
    isDarkMode: Boolean,
    versionName: String,
    versionCode: Int,
    counts: DataStorageCounts,
    isResetConfirmationVisible: Boolean,
    resetSuccessMessage: String?,
    onClose: () -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetAutoDayNight: (Boolean) -> Unit,
    onSetInAppNotifications: (Boolean) -> Unit,
    onToggleCategory: (String, Boolean) -> Unit,
    onOpenNotificationCenter: () -> Unit,
    onShowResetConfirmation: (Boolean) -> Unit,
    onConfirmReset: () -> Unit,
    onClearResetSuccessMessage: () -> Unit
) {
    if (activeModal == SettingsModal.NONE) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bgColor = if (isDarkMode) Color(0xFF111827) else Color(0xFFF8FAFC)

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = bgColor,
        dragHandle = {
            Surface(
                modifier = Modifier.padding(vertical = 10.dp),
                color = if (isDarkMode) Color(0xFF374151) else Color(0xFFCBD5E1),
                shape = RoundedCornerShape(2.dp)
            ) {
                Box(modifier = Modifier.size(width = 36.dp, height = 4.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp)
        ) {
            when (activeModal) {
                SettingsModal.APPEARANCE -> AppearanceContent(
                    userSettings = userSettings,
                    isDarkMode = isDarkMode,
                    onSetThemeMode = onSetThemeMode,
                    onSetAutoDayNight = onSetAutoDayNight,
                    onClose = onClose
                )
                SettingsModal.NOTIFICATIONS -> NotificationSettingsContent(
                    userSettings = userSettings,
                    isDarkMode = isDarkMode,
                    onSetInAppNotifications = onSetInAppNotifications,
                    onToggleCategory = onToggleCategory,
                    onOpenNotificationCenter = {
                        onClose()
                        onOpenNotificationCenter()
                    },
                    onClose = onClose
                )
                SettingsModal.DATA_STORAGE -> DataStorageContent(
                    counts = counts,
                    isDarkMode = isDarkMode,
                    isResetConfirmationVisible = isResetConfirmationVisible,
                    resetSuccessMessage = resetSuccessMessage,
                    onShowResetConfirmation = onShowResetConfirmation,
                    onConfirmReset = onConfirmReset,
                    onClearSuccessMessage = onClearResetSuccessMessage,
                    onClose = onClose
                )
                SettingsModal.HOW_TO_USE -> HowToUseContent(
                    isDarkMode = isDarkMode,
                    onClose = onClose
                )
                SettingsModal.FAQ -> FaqContent(
                    isDarkMode = isDarkMode,
                    onClose = onClose
                )
                SettingsModal.TERMS -> TermsContent(
                    isDarkMode = isDarkMode,
                    onClose = onClose
                )
                SettingsModal.PRIVACY -> PrivacyContent(
                    isDarkMode = isDarkMode,
                    onClose = onClose
                )
                SettingsModal.ABOUT -> AboutContent(
                    versionName = versionName,
                    versionCode = versionCode,
                    isDarkMode = isDarkMode,
                    onClose = onClose
                )
                SettingsModal.WHATS_NEW -> WhatsNewContent(
                    isDarkMode = isDarkMode,
                    onClose = onClose
                )
                SettingsModal.FEEDBACK -> FeedbackContent(
                    isDarkMode = isDarkMode,
                    onClose = onClose
                )
                SettingsModal.NONE -> {}
            }
        }
    }
}

// ==========================================
// 1. APPEARANCE MODAL
// ==========================================
@Composable
private fun AppearanceContent(
    userSettings: UserSettings,
    isDarkMode: Boolean,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetAutoDayNight: (Boolean) -> Unit,
    onClose: () -> Unit
) {
    ModalHeader(title = "Appearance", onClose = onClose, isDarkMode = isDarkMode)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Select application appearance theme:",
            fontSize = 13.sp,
            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
        )

        ThemeOptionCard(
            title = "Light",
            description = "Clean lavender-white pastel aesthetic",
            icon = Icons.Outlined.LightMode,
            isSelected = userSettings.themeMode == ThemeMode.LIGHT,
            isDarkMode = isDarkMode,
            onClick = { onSetThemeMode(ThemeMode.LIGHT) }
        )

        ThemeOptionCard(
            title = "Dark",
            description = "Midnight navy, high contrast surfaces",
            icon = Icons.Outlined.DarkMode,
            isSelected = userSettings.themeMode == ThemeMode.DARK,
            isDarkMode = isDarkMode,
            onClick = { onSetThemeMode(ThemeMode.DARK) }
        )

        ThemeOptionCard(
            title = "System",
            description = "Follows Android system appearance automatically",
            icon = Icons.Outlined.SettingsBrightness,
            isSelected = userSettings.themeMode == ThemeMode.SYSTEM,
            isDarkMode = isDarkMode,
            onClick = { onSetThemeMode(ThemeMode.SYSTEM) }
        )

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0))
        Spacer(modifier = Modifier.height(4.dp))

        // Automatic Day/Night toggle
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkMode) Color(0xFF172033) else Color.White,
            border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "Automatic Day / Night",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                            )
                            Text(
                                text = if (userSettings.autoDayNightEnabled) "Enabled (06:00 - 18:00)" else "Disabled",
                                fontSize = 12.sp,
                                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                        }
                    }
                    Switch(
                        checked = userSettings.autoDayNightEnabled,
                        onCheckedChange = { onSetAutoDayNight(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF6366F1)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "When enabled, LIFEOS automatically applies Light theme during the day (06:00 to 18:00) and Dark theme during the night, overriding manual selection.",
                    fontSize = 11.5.sp,
                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val cardBg = if (isSelected) {
        if (isDarkMode) Color(0xFF1E1B4B) else Color(0xFFEEF2FF)
    } else {
        if (isDarkMode) Color(0xFF172033) else Color.White
    }
    val borderColor = if (isSelected) Color(0xFF6366F1) else (if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .semantics {
                role = Role.RadioButton
                contentDescription = "$title appearance: $description, ${if (isSelected) "Selected" else "Not selected"}"
            },
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = if (isSelected) Color(0xFF6366F1) else (if (isDarkMode) Color(0xFF1F2937) else Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else (if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569)),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFF6366F1) else Color.Transparent)
                    .then(
                        if (!isSelected) Modifier.background(Color.Transparent) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) Color(0xFF374151) else Color(0xFFCBD5E1))
                    )
                }
            }
        }
    }
}

// ==========================================
// 2. NOTIFICATION SETTINGS MODAL
// ==========================================
@Composable
private fun NotificationSettingsContent(
    userSettings: UserSettings,
    isDarkMode: Boolean,
    onSetInAppNotifications: (Boolean) -> Unit,
    onToggleCategory: (String, Boolean) -> Unit,
    onOpenNotificationCenter: () -> Unit,
    onClose: () -> Unit
) {
    ModalHeader(title = "Notification Settings", onClose = onClose, isDarkMode = isDarkMode)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Master switch
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkMode) Color(0xFF172033) else Color.White,
            border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "In-App Notifications",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                    )
                    Text(
                        text = "Display reactive bell badges & in-app event notices",
                        fontSize = 12.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
                Switch(
                    checked = userSettings.inAppNotificationsEnabled,
                    onCheckedChange = { onSetInAppNotifications(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF6366F1)
                    )
                )
            }
        }

        Text(
            text = "EVENT CATEGORIES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        )

        val categories = listOf(
            Triple("PERSONAL", "Personal Intelligence", "Task starts, completions, and schedule reminders"),
            Triple("TRUTH", "Truth & RealityCheck", "Investigation verdicts and claim verification results"),
            Triple("MESH", "RescueMesh", "Peer discovery and store-and-forward transmission packets"),
            Triple("LEARNING", "Learning Loop", "Cognitive model adaptations and focus recommendations"),
            Triple("EMERGENCY", "Emergency Alerts", "High-priority emergency message alerts")
        )

        categories.forEach { (catKey, catTitle, catDesc) ->
            val isEnabled = userSettings.enabledNotificationCategories.contains(catKey)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF172033) else Color.White,
                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = catTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Text(
                            text = catDesc,
                            fontSize = 11.5.sp,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { onToggleCategory(catKey, it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF6366F1)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onOpenNotificationCenter,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color.White)
                Text(text = "Open Notification Center", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// ==========================================
// 3. DATA & STORAGE MODAL
// ==========================================
@Composable
private fun DataStorageContent(
    counts: DataStorageCounts,
    isDarkMode: Boolean,
    isResetConfirmationVisible: Boolean,
    resetSuccessMessage: String?,
    onShowResetConfirmation: (Boolean) -> Unit,
    onConfirmReset: () -> Unit,
    onClearSuccessMessage: () -> Unit,
    onClose: () -> Unit
) {
    ModalHeader(title = "Data & Storage", onClose = onClose, isDarkMode = isDarkMode)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (resetSuccessMessage != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF064E3B) else Color(0xFFDCFCE7),
                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF059669) else Color(0xFF16A34A))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = resetSuccessMessage,
                        color = if (isDarkMode) Color(0xFFD1FAE5) else Color(0xFF15803D),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = onClearSuccessMessage, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color(0xFF15803D))
                    }
                }
            }
        }

        Text(
            text = "LOCALLY STORED ENTITIES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
            modifier = Modifier.padding(start = 4.dp)
        )

        val dataRows = listOf(
            "Tasks" to counts.tasksCount,
            "Goals" to counts.goalsCount,
            "Behavior Events" to counts.behaviorEventsCount,
            "Truth Investigations" to counts.investigationsCount,
            "Emergency Messages" to counts.emergencyMessagesCount,
            "Notifications" to counts.notificationsCount
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkMode) Color(0xFF172033) else Color.White,
            border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                dataRows.forEachIndexed { index, (label, count) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 11.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Surface(
                            color = if (isDarkMode) Color(0xFF1E1B4B) else Color(0xFFEEF2FF),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "$count",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFFC7D2FE) else Color(0xFF4338CA),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }
                    if (index < dataRows.size - 1) {
                        HorizontalDivider(color = if (isDarkMode) Color(0xFF1F2937) else Color(0xFFF1F5F9))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "DANGER ZONE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFFEF4444),
            modifier = Modifier.padding(start = 4.dp)
        )

        // Destructive Reset Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkMode) Color(0xFF2D1515) else Color(0xFFFEF2F2),
            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Reset LIFEOS Data",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "This removes locally stored LIFEOS tasks, behavior events, investigations, and emergency messages. This action cannot be undone.",
                    fontSize = 12.sp,
                    color = if (isDarkMode) Color(0xFFFCA5A5) else Color(0xFF991B1B),
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { onShowResetConfirmation(true) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text(text = "Clear Local Data...", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (isResetConfirmationVisible) {
        AlertDialog(
            onDismissRequest = { onShowResetConfirmation(false) },
            title = {
                Text(
                    text = "Reset All LIFEOS Data?",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently clear all local LIFEOS records? All tasks, behavior history, reality check evidence, and emergency queue entries will be deleted immediately.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmReset,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Confirm Reset", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { onShowResetConfirmation(false) }) {
                    Text("Cancel")
                }
            },
            containerColor = if (isDarkMode) Color(0xFF172033) else Color.White
        )
    }
}

// ==========================================
// 4. HOW TO USE MODAL
// ==========================================
@Composable
private fun HowToUseContent(
    isDarkMode: Boolean,
    onClose: () -> Unit
) {
    ModalHeader(title = "How to Use LIFEOS", onClose = onClose, isDarkMode = isDarkMode)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "THE COGNITIVE LEARNING LOOP",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFF6366F1),
            modifier = Modifier.padding(start = 4.dp)
        )

        val loopSteps = listOf(
            Triple("1. OBSERVE", "Telemetry & Context", "Monitors time, network status, circadian rhythms, and activity state."),
            Triple("2. UNDERSTAND", "Synthesis & Focus", "Identifies the user's high-energy windows and task priority requirements."),
            Triple("3. DECIDE", "Deterministic Engine", "Ranks what matters now with zero hallucination and clear explainability."),
            Triple("4. ACT", "Frictionless Execution", "Presents single focused tasks with quick start/postpone actions."),
            Triple("5. MEASURE", "Behavior Verification", "Tracks completion rates, timing adherence, and user overrides."),
            Triple("6. ADAPT", "Feedback Calibration", "Tuning weights adapt next recommendations based on real outcomes.")
        )

        loopSteps.forEach { (step, title, desc) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF172033) else Color.White,
                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = if (isDarkMode) Color(0xFF1E1B4B) else Color(0xFFEEF2FF),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = step.take(2),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF6366F1)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = desc,
                            fontSize = 12.sp,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "THE THREE INTELLIGENCE PILLARS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFF6366F1),
            modifier = Modifier.padding(start = 4.dp)
        )

        val pillars = listOf(
            Triple("Personal Intelligence", "Tasks, Goals, and Circadian Timing", Color(0xFF3B82F6)),
            Triple("Trust Intelligence", "RealityCheck truth claim verification & corroboration", Color(0xFF10B981)),
            Triple("Resilience Intelligence", "RescueMesh store-and-forward emergency network", Color(0xFF06B6D4))
        )

        pillars.forEach { (name, desc, color) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF172033) else Color.White,
                border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Text(
                            text = name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = desc,
                        fontSize = 12.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

// ==========================================
// 5. FAQ MODAL (ACCORDION)
// ==========================================
@Composable
private fun FaqContent(
    isDarkMode: Boolean,
    onClose: () -> Unit
) {
    ModalHeader(title = "Frequently Asked Questions", onClose = onClose, isDarkMode = isDarkMode)

    val expandedMap = remember { mutableStateMapOf<Int, Boolean>() }

    val faqs = listOf(
        "What is LIFEOS?" to "LIFEOS is an adaptive intelligence platform that unifies Personal Intelligence (tasks & habits), Trust Intelligence (truth claim verification), and Resilience Intelligence (offline emergency mesh communications).",
        "How does LIFEOS make recommendations?" to "LIFEOS uses a deterministic DecisionEngine that observes your current time, network status, pending tasks, and circadian rhythms to score and rank what matters most right now.",
        "How does LIFEOS learn from my behavior?" to "Every time you complete, start, postpone, or dismiss a task or recommendation, a BehaviorEvent is recorded locally. The Learning Loop analyzes completed tasks to adjust future scoring weights.",
        "What is RealityCheck?" to "RealityCheck is our truth intelligence tool that takes factual claims, normalizes them, queries evidence sources, and calculates deterministic confidence scores and verdicts (SUPPORTED, CONTRADICTED, or INSUFFICIENT_EVIDENCE).",
        "How does RescueMesh work?" to "RescueMesh uses a local store-and-forward model. Emergency packets are prepared with byte limits, prioritized (Normal, Urgent, Critical), and forwarded through reachable peer nodes until internet gateways are reached.",
        "Does LIFEOS require internet?" to "No. LIFEOS is architected for offline-first resilience. All core engines, task management, learning loops, and message queues operate 100% on-device.",
        "Where is my data stored?" to "All your tasks, behavior history, investigations, emergency messages, and notifications are stored locally in an encrypted/private SQLite Room database on your device.",
        "How does the Notification Center work?" to "The Notification Center gathers genuine LIFEOS domain events (task progress, truth verdicts, mesh relays) into a clean in-app feed with read/unread status and deep links.",
        "Can I change the app appearance?" to "Yes. Under Settings → Appearance, you can choose between Light, Dark, or System mode, or enable Automatic Day/Night scheduling (06:00 - 18:00).",
        "How can I reset my LIFEOS data?" to "Under Settings → Data & Storage, tap 'Clear Local Data' in the Danger Zone. You will be prompted with a confirmation dialog before any data is deleted."
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        faqs.forEachIndexed { index, (question, answer) ->
            val isExpanded = expandedMap[index] == true
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { expandedMap[index] = !isExpanded }
                    .semantics {
                        role = Role.Button
                        contentDescription = "Question: $question. ${if (isExpanded) "Expanded" else "Collapsed"}"
                    },
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF172033) else Color.White,
                border = BorderStroke(1.dp, if (isExpanded) Color(0xFF6366F1) else (if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0)))
            ) {
                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .animateContentSize(spring())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = question,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = if (isExpanded) Color(0xFF6366F1) else (if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B))
                        )
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = answer,
                            fontSize = 12.5.sp,
                            color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. TERMS & CONDITIONS MODAL
// ==========================================
@Composable
private fun TermsContent(
    isDarkMode: Boolean,
    onClose: () -> Unit
) {
    ModalHeader(title = "Terms & Conditions", onClose = onClose, isDarkMode = isDarkMode)

    val terms = listOf(
        "1. Acceptance of Terms" to "By accessing or using LIFEOS, you acknowledge that you have read, understood, and agree to these terms. LIFEOS is provided for productivity, cognitive organization, and resilience support.",
        "2. Use of LIFEOS" to "LIFEOS is intended for personal, non-commercial productivity and resilience management. You agree to use the application in accordance with all applicable local laws.",
        "3. User Responsibilities" to "You are solely responsible for verifying the accuracy of personal schedules, tasks, and actions taken based on recommendation outputs.",
        "4. Information & Recommendations" to "Recommendations generated by LIFEOS are deterministic heuristics designed to assist focus. They do not constitute certified medical, financial, or emergency advice.",
        "5. RealityCheck Limitations" to "RealityCheck uses deterministic evidence scoring from cached or network datasets. Verdicts indicate evidence corroboration level and should not substitute critical thinking or expert advice.",
        "6. RescueMesh Limitations" to "RescueMesh operates as an opportunistic store-and-forward prototype. Node relaying depends on peer proximity and radio conditions and cannot guarantee packet arrival.",
        "7. Emergency Use Disclaimer" to "In life-threatening situations, always dial certified emergency numbers (911, 112, etc.) first. RescueMesh is a supplemental resilience tool, not an official emergency service.",
        "8. Data & Storage" to "All user data resides in local on-device SQLite databases. Users retain complete ownership of their local records and can wipe them anytime via Data & Storage settings.",
        "9. Application Changes" to "Features, engines, and protocols in LIFEOS may be updated, refined, or expanded over successive versions.",
        "10. Contact / Support" to "For issues, bug reports, and project contributions, consult the official LIFEOS repository and documentation."
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        terms.forEach { (section, body) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF172033) else Color.White,
                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = section,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = body,
                        fontSize = 12.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// 7. PRIVACY MODAL
// ==========================================
@Composable
private fun PrivacyContent(
    isDarkMode: Boolean,
    onClose: () -> Unit
) {
    ModalHeader(title = "Privacy Policy", onClose = onClose, isDarkMode = isDarkMode)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkMode) Color(0xFF064E3B) else Color(0xFFDCFCE7),
            border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF059669) else Color(0xFF16A34A))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(28.dp))
                Column {
                    Text(
                        text = "100% Local-First Architecture",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFD1FAE5) else Color(0xFF15803D)
                    )
                    Text(
                        text = "Your records remain on your phone. No tracking, no cloud telemetry.",
                        fontSize = 12.sp,
                        color = if (isDarkMode) Color(0xFFD1FAE5) else Color(0xFF166534)
                    )
                }
            }
        }

        val privacyDetails = listOf(
            "Tasks & Goals" to "Stored exclusively in the on-device Room SQLite database. Never sent to remote servers.",
            "Behavior Events" to "Recorded locally to power the on-device learning loop. No analytics SDKs or remote logs.",
            "RealityCheck Claims" to "Processed on-device against local evidence sets or network checks when available.",
            "Emergency Messages" to "Queued locally in SQLite. Forwarded only to nearby peer nodes or designated gateways when you initiate dispatch.",
            "Notifications" to "Synthesized in-app from domain events. No remote push notifications or FCM tokens used.",
            "Preferences" to "Saved securely in Android Jetpack DataStore on your device."
        )

        privacyDetails.forEach { (title, desc) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF172033) else Color.White,
                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = title,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = desc,
                        fontSize = 12.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// 8. ABOUT LIFEOS MODAL
// ==========================================
@Composable
private fun AboutContent(
    versionName: String,
    versionCode: Int,
    isDarkMode: Boolean,
    onClose: () -> Unit
) {
    ModalHeader(title = "About LIFEOS", onClose = onClose, isDarkMode = isDarkMode)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkMode) Color(0xFF172033) else Color.White,
            border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LIFEOS",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Understand • Decide • Adapt",
                    fontSize = 12.sp,
                    color = Color(0xFF6366F1),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Adaptive Intelligence Platform",
                    fontSize = 13.sp,
                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = if (isDarkMode) Color(0xFF1F2937) else Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "VERSION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                        Text(text = versionName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color.White else Color(0xFF0F172A))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "BUILD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                        Text(text = "$versionCode", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color.White else Color(0xFF0F172A))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "PLATFORM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                        Text(text = "Android", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color.White else Color(0xFF0F172A))
                    }
                }
            }
        }

        Text(
            text = "CORE ARCHITECTURE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFF6366F1),
            modifier = Modifier.padding(start = 4.dp)
        )

        val techStack = listOf(
            "UI Framework" to "Jetpack Compose with Material 3 & Custom Design System",
            "Persistence" to "Room SQLite Database v4 & Jetpack DataStore",
            "Concurrency" to "Kotlin Coroutines, Reactive StateFlow & SharedFlow",
            "Engines" to "Deterministic DecisionEngine, RealityCheckEngine & RescueMeshEngine"
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = if (isDarkMode) Color(0xFF172033) else Color.White,
            border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                techStack.forEach { (label, value) ->
                    Column {
                        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
                        Text(text = value, fontSize = 12.5.sp, color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A))
                    }
                }
            }
        }
    }
}

// ==========================================
// 9. WHAT'S NEW MODAL
// ==========================================
@Composable
private fun WhatsNewContent(
    isDarkMode: Boolean,
    onClose: () -> Unit
) {
    ModalHeader(title = "What's New", onClose = onClose, isDarkMode = isDarkMode)

    val milestones = listOf(
        Triple("Navigation Drawer & Settings", "Added command navigation drawer, theme switching (Light/Dark/System), Day/Night scheduler, data management, and help modules.", "v1.3"),
        Triple("In-App Notification Center", "Added reactive notification bell, Room v4 event persistence, category filters, and deep-link routing.", "v1.2"),
        Triple("10-Screen Reference-Match UI", "Complete visual redesign with light lavender pastel background, scenic mountain headers, and unified bottom nav.", "v1.1"),
        Triple("Three Intelligence Pillars", "Adaptive productivity engine, RealityCheck truth corroboration, and RescueMesh store-and-forward communications.", "v1.0")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        milestones.forEach { (title, desc, version) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF172033) else Color.White,
                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        )
                        Surface(
                            color = if (isDarkMode) Color(0xFF1E1B4B) else Color(0xFFEEF2FF),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = version,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6366F1),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = desc,
                        fontSize = 12.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// 10. FEEDBACK & SUPPORT MODAL
// ==========================================
@Composable
private fun FeedbackContent(
    isDarkMode: Boolean,
    onClose: () -> Unit
) {
    ModalHeader(title = "Feedback & Support", onClose = onClose, isDarkMode = isDarkMode)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = if (isDarkMode) Color(0xFF172033) else Color.White,
            border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Developer & User Support",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "LIFEOS is actively developed and maintained as an offline-first adaptive intelligence project. We welcome bug reports, cognitive model improvements, and feature suggestions.",
                    fontSize = 12.sp,
                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                    lineHeight = 17.sp
                )
            }
        }

        val supportChannels = listOf(
            "Report an Issue" to "Identify bugs in the cognitive loop, database, or UI layout.",
            "Feature Suggestions" to "Propose new intelligence heuristics, widgets, or mesh protocols.",
            "Security & Privacy Inquiries" to "Review our on-device local storage guarantees."
        )

        supportChannels.forEach { (title, desc) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF172033) else Color.White,
                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = title,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = desc,
                        fontSize = 12.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

// Reusable Modal Header
@Composable
private fun ModalHeader(title: String, onClose: () -> Unit, isDarkMode: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
        )
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isDarkMode) Color(0xFF1F2937) else Color(0xFFF1F5F9))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close $title",
                tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
