package com.mrashish18.lifeos.feature.resilience

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessagePriority
import com.mrashish18.lifeos.core.model.MessageStatus
import com.mrashish18.lifeos.core.model.MessageType
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.ui.components.RescueMeshNetworkTopologyVisual
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import com.mrashish18.lifeos.ui.components.LifeOsMenuButton
import com.mrashish18.lifeos.ui.components.LifeOsNotificationBell

enum class ResilienceScreenMode {
    CENTER,      // Screen 8: RescueMesh Center
    EMERGENCY,   // Screen 9: Create Emergency Message
    QUEUE        // Screen 10: Message Queue
}

@Composable
fun ResilienceScreen(
    viewModel: ResilienceViewModel,
    unreadNotificationCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    isDarkMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var screenMode by remember { mutableStateOf(ResilienceScreenMode.CENTER) }

    // Sync screen mode with viewModel emergency modal state if changed externally
    if (uiState.isEmergencyModalOpen && screenMode != ResilienceScreenMode.EMERGENCY) {
        screenMode = ResilienceScreenMode.EMERGENCY
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
        when (screenMode) {
            ResilienceScreenMode.CENTER -> {
                RescueMeshCenterScreen(
                    uiState = uiState,
                    unreadNotificationCount = unreadNotificationCount,
                    onOpenNotifications = onOpenNotifications,
                    onOpenDrawer = onOpenDrawer,
                    isDarkMode = isDarkMode,
                    onDismissFeedback = { viewModel.clearFeedback() },
                    onOpenEmergency = {
                        screenMode = ResilienceScreenMode.EMERGENCY
                    },
                    onOpenQueue = {
                        screenMode = ResilienceScreenMode.QUEUE
                    }
                )
            }
            ResilienceScreenMode.EMERGENCY -> {
                EmergencyMessageScreen(
                    isDarkMode = isDarkMode,
                    onCancel = {
                        viewModel.closeEmergencyModal()
                        screenMode = ResilienceScreenMode.CENTER
                    },
                    onSend = { payload, priority, destination ->
                        viewModel.sendEmergencyMessage(
                            payload = payload,
                            type = MessageType.EMERGENCY,
                            priority = priority,
                            recipientId = destination.ifBlank { null }
                        )
                        screenMode = ResilienceScreenMode.QUEUE
                    }
                )
            }
            ResilienceScreenMode.QUEUE -> {
                MessageQueueScreen(
                    messages = uiState.messages,
                    queuedCount = uiState.queuedCount,
                    relayingCount = uiState.relayingCount,
                    deliveredCount = uiState.deliveredCount,
                    feedbackMessage = uiState.feedbackMessage,
                    unreadNotificationCount = unreadNotificationCount,
                    onOpenNotifications = onOpenNotifications,
                    isDarkMode = isDarkMode,
                    onDismissFeedback = { viewModel.clearFeedback() },
                    onRelayMessage = { msgId -> viewModel.relayMessage(msgId) },
                    onOpenEmergency = {
                        screenMode = ResilienceScreenMode.EMERGENCY
                    },
                    onBackToCenter = {
                        screenMode = ResilienceScreenMode.CENTER
                    }
                )
            }
        }
    }
}

/**
 * Screen 8: RescueMesh Center (Reference Image 2, Screen 8).
 * Unified light theme with bold dark typography, green status pill,
 * hero twilight topology visual, white 3-metrics card, and prominent emergency dispatch CTA.
 */
@Composable
private fun RescueMeshCenterScreen(
    uiState: ResilienceUiState,
    unreadNotificationCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    isDarkMode: Boolean = false,
    onDismissFeedback: () -> Unit,
    onOpenEmergency: () -> Unit,
    onOpenQueue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Header: ☰ RescueMesh | [ Armed • Ready › ] + 🔔
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LifeOsMenuButton(
                    onClick = onOpenDrawer,
                    isDarkMode = isDarkMode
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "RescueMesh",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                    letterSpacing = (-0.5).sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDarkMode) Color(0xFF064E3B).copy(alpha = 0.5f) else Color(0xFFDCFCE7))
                        .border(1.dp, if (isDarkMode) Color(0xFF059669) else Color(0xFF86EFAC), RoundedCornerShape(20.dp))
                        .clickable(onClick = onOpenQueue)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Armed • Ready ›",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFF34D399) else Color(0xFF047857)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                LifeOsNotificationBell(
                    unreadCount = unreadNotificationCount,
                    onClick = onOpenNotifications,
                    isDarkMode = isDarkMode
                )
            }
        }

        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "A resilient mesh network for people and communities when it matters most.",
            fontSize = 11.5.sp,
            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
            lineHeight = 16.sp
        )

        // Feedback Banner (if active)
        uiState.feedbackMessage?.let { feedback ->
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF172033) else Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF0284C7) else Color(0xFF38BDF8)),
                shadowElevation = if (isDarkMode) 0.dp else 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feedback,
                            fontSize = 11.5.sp,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable(onClick = onDismissFeedback)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hero Twilight Network Topology Visual (Reference Image 2, Screen 8)
        RescueMeshNetworkTopologyVisual(
            localNodeId = uiState.localNodeId,
            isOnline = uiState.networkState == NetworkState.CONNECTED,
            relayingCount = uiState.relayingCount
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Clean White 3-Metrics Surface Card (Reference Image 2, Screen 8)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenQueue),
            shape = RoundedCornerShape(18.dp),
            color = if (isDarkMode) Color(0xFF111827) else Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)),
            shadowElevation = if (isDarkMode) 0.dp else 2.dp
        ) {
            Column(
                modifier = Modifier.padding(vertical = 14.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "3",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDarkMode) Color(0xFF34D399) else Color(0xFF059669)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Nodes Online",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${uiState.queuedCount}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Queued",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${uiState.relayingCount}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Relaying",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Tap to inspect message queue ›",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4F46E5)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Dominant Primary Action CTA: Dispatch Emergency Message
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFEF4444))
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFF43F5E), Color(0xFFE11D48))
                    )
                )
                .clickable(onClick = onOpenEmergency),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Dispatch Emergency Message",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.5.sp,
                    letterSpacing = 0.2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Secondary Text Link: View Message Queue ›
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenQueue)
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "View Message Queue ›",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4F46E5)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Reassurance Card: "You Are Not Alone" (Reference Image 2, Screen 8)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkMode) Color(0xFF111827) else Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)),
            shadowElevation = if (isDarkMode) 0.dp else 1.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) Color(0xFF3B1D25) else Color(0xFFFEE2E2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Care",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "You Are Not Alone",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Even in low connectivity, messages can reach others through peer-to-peer relay.",
                    fontSize = 11.5.sp,
                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"Stronger communities build safer tomorrows.\"",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4F46E5),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}

/**
 * Screen 9: Create Emergency Message (Reference Image 2, Screen 9).
 * Unified light theme with white message card, 3 priority pills,
 * safety advice callout, and Send to Mesh gradient action.
 */
@Composable
private fun EmergencyMessageScreen(
    onCancel: () -> Unit,
    onSend: (payload: String, priority: MessagePriority, destination: String) -> Unit,
    isDarkMode: Boolean = false
) {
    var messageText by remember {
        mutableStateOf("Need medical supplies for our community. Water and basic medications would help. Staying safe together. - LifeOs User")
    }
    var selectedPriority by remember { mutableStateOf(MessagePriority.HIGH) }
    var destinationText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Header with Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDarkMode) Color(0xFF1E293B) else Color.White)
                    .border(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                    .clickable(onClick = onCancel),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Emergency Message",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                    letterSpacing = (-0.4).sp
                )
                Text(
                    text = "A small message today. A safer tomorrow.",
                    fontSize = 11.5.sp,
                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // White Message Card matching Reference Image 2, Screen 9
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = if (isDarkMode) Color(0xFF111827) else Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)),
            shadowElevation = if (isDarkMode) 0.dp else 2.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MESSAGE (256 bytes max)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569)
                    )
                    Text(
                        text = "${messageText.length}/256",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkMode) Color(0xFF60A5FA) else Color(0xFF2563EB)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF8FAFC))
                        .border(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    if (messageText.isEmpty()) {
                        Text(
                            text = "Describe situation, location, and immediate needs...",
                            fontSize = 12.5.sp,
                            color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8),
                            lineHeight = 17.sp
                        )
                    }
                    BasicTextField(
                        value = messageText,
                        onValueChange = { if (it.length <= 256) messageText = it },
                        textStyle = TextStyle(
                            fontSize = 12.5.sp,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                            fontWeight = FontWeight.Medium,
                            lineHeight = 17.sp
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⚠️", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Be clear, specific, and include location if safe.",
                        fontSize = 11.sp,
                        color = if (isDarkMode) Color(0xFFFBBF24) else Color(0xFFD97706),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PRIORITY Section Header
        Text(
            text = "PRIORITY",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 3 Priority Pills matching Reference Image 2, Screen 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Normal
            val isNormal = selectedPriority == MessagePriority.NORMAL
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { selectedPriority = MessagePriority.NORMAL },
                shape = RoundedCornerShape(12.dp),
                color = if (isNormal) {
                    if (isDarkMode) Color(0xFF064E3B).copy(alpha = 0.6f) else Color(0xFFDCFCE7)
                } else {
                    if (isDarkMode) Color(0xFF1E293B) else Color.White
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isNormal) Color(0xFF10B981) else if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Normal",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isNormal) {
                            if (isDarkMode) Color(0xFF34D399) else Color(0xFF047857)
                        } else {
                            if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569)
                        }
                    )
                }
            }

            // Urgent
            val isUrgent = selectedPriority == MessagePriority.HIGH
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { selectedPriority = MessagePriority.HIGH },
                shape = RoundedCornerShape(12.dp),
                color = if (isUrgent) Color(0xFFF59E0B) else if (isDarkMode) Color(0xFF1E293B) else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isUrgent) Color(0xFFD97706) else if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Urgent",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUrgent) Color.White else if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569)
                    )
                }
            }

            // Critical
            val isCritical = selectedPriority == MessagePriority.CRITICAL
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { selectedPriority = MessagePriority.CRITICAL },
                shape = RoundedCornerShape(12.dp),
                color = if (isCritical) {
                    if (isDarkMode) Color(0xFF450A0A) else Color(0xFFFEF2F2)
                } else {
                    if (isDarkMode) Color(0xFF1E293B) else Color.White
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isCritical) Color(0xFFEF4444) else if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Critical",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCritical) Color(0xFFEF4444) else if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info Callout: Messages stored locally and relayed to nearby peers
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEEF2FF),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFC7D2FE))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "ℹ️", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Messages are stored locally and relayed to nearby peers when connectivity is available.",
                    fontSize = 11.sp,
                    color = if (isDarkMode) Color(0xFF93C5FD) else Color(0xFF4338CA),
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Primary Action Button: 🚀 Send to Mesh
        val effectivePayload = if (messageText.isBlank()) {
            "Need medical supplies for our community. Water and basic medications would help. Staying safe together. - LifeOs User"
        } else {
            messageText
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF4F46E5))
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
                    )
                )
                .clickable {
                    onSend(effectivePayload, selectedPriority, destinationText)
                },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🚀", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Send to Mesh",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Secondary Action Button: 💾 Save Draft
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onCancel),
            shape = RoundedCornerShape(14.dp),
            color = if (isDarkMode) Color(0xFF1E293B) else Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💾", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save Draft",
                        fontSize = 13.sp,
                        color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}

/**
 * Screen 10: Message Queue (Reference Image 2, Screen 10).
 * Unified light theme with filter pills (All, Queued, Relaying, Sent),
 * white Store-and-Forward Active card, and message items with hop badges and Relay action.
 */
@Composable
private fun MessageQueueScreen(
    messages: List<EmergencyMessage>,
    queuedCount: Int,
    relayingCount: Int,
    deliveredCount: Int,
    feedbackMessage: String? = null,
    unreadNotificationCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    isDarkMode: Boolean = false,
    onDismissFeedback: () -> Unit = {},
    onRelayMessage: (String) -> Unit = {},
    onOpenEmergency: () -> Unit = {},
    onBackToCenter: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Header: ← Message Queue | 🔔
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.clickable(onClick = onBackToCenter),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDarkMode) Color(0xFF1E293B) else Color.White)
                        .border(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Message Queue",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                        letterSpacing = (-0.4).sp
                    )
                    Text(
                        text = "Store locally. Relay when possible. Together we stay connected.",
                        fontSize = 11.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }

            LifeOsNotificationBell(
                unreadCount = unreadNotificationCount,
                onClick = onOpenNotifications,
                isDarkMode = isDarkMode
            )
        }

        // Feedback Banner (if active)
        feedbackMessage?.let { feedback ->
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF172033) else Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF0284C7) else Color(0xFF38BDF8)),
                shadowElevation = if (isDarkMode) 0.dp else 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feedback,
                            fontSize = 11.5.sp,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable(onClick = onDismissFeedback)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Pills matching Reference Image 2, Screen 10
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "All" to messages.size,
                "Queued" to queuedCount,
                "Relaying" to relayingCount,
                "Sent" to deliveredCount
            ).forEach { (filter, count) ->
                val isSelected = selectedFilter == filter
                val label = if (filter == "All") "All" else "$filter ($count)"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) Brush.horizontalGradient(listOf(Color(0xFF2563EB), Color(0xFF4F46E5)))
                            else Brush.linearGradient(listOf(
                                if (isDarkMode) Color(0xFF1E293B) else Color.White,
                                if (isDarkMode) Color(0xFF1E293B) else Color.White
                            ))
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color(0xFF2563EB) else (if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedFilter = filter }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 10.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else (if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Status Card: Store-and-Forward Active
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = if (isDarkMode) Color(0xFF111827) else Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)),
            shadowElevation = if (isDarkMode) 0.dp else 2.dp
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEEF2FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🌐", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Store-and-Forward Active",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Messages move hop-by-hop across the mesh until they reach people.",
                        fontSize = 11.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val filteredList = when (selectedFilter) {
            "All" -> messages
            "Queued" -> messages.filter { it.status == MessageStatus.QUEUED }
            "Relaying" -> messages.filter { it.status == MessageStatus.RELAYING }
            "Sent" -> messages.filter { it.status == MessageStatus.DELIVERED || it.status == MessageStatus.SENT }
            else -> messages
        }

        if (filteredList.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkMode) Color(0xFF111827) else Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)),
                shadowElevation = if (isDarkMode) 0.dp else 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📭", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Queue clear",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "No messages currently in this filter state.",
                        fontSize = 11.5.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredList.forEach { msg ->
                    val isRelaying = msg.status == MessageStatus.RELAYING
                    val isSent = msg.status == MessageStatus.SENT || msg.status == MessageStatus.DELIVERED

                    val iconBg = if (isRelaying) {
                        if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEFF6FF)
                    } else if (isSent) {
                        if (isDarkMode) Color(0xFF064E3B).copy(alpha = 0.5f) else Color(0xFFECFDF5)
                    } else {
                        if (isDarkMode) Color(0xFF450A0A).copy(alpha = 0.5f) else Color(0xFFFEF2F2)
                    }

                    val iconText = if (isRelaying) "📨" else if (isSent) "✓" else "⚠️"

                    val pillBg = if (isRelaying) {
                        if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEFF6FF)
                    } else if (isSent) {
                        if (isDarkMode) Color(0xFF064E3B).copy(alpha = 0.5f) else Color(0xFFECFDF5)
                    } else {
                        if (isDarkMode) Color(0xFF450A0A).copy(alpha = 0.5f) else Color(0xFFFEF2F2)
                    }

                    val pillTextColor = if (isRelaying) {
                        if (isDarkMode) Color(0xFF60A5FA) else Color(0xFF2563EB)
                    } else if (isSent) {
                        if (isDarkMode) Color(0xFF34D399) else Color(0xFF059669)
                    } else {
                        if (isDarkMode) Color(0xFFF87171) else Color(0xFFDC2626)
                    }

                    val pillLabel = if (isRelaying) "RELAYING • Hop ${msg.hopCount}/${msg.maxHops}"
                        else if (isSent) "SENT • ${msg.hopCount} Hops"
                        else "QUEUED"

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = if (isDarkMode) Color(0xFF111827) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)),
                        shadowElevation = if (isDarkMode) 0.dp else 2.dp
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(iconBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = iconText, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(pillBg)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = pillLabel,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = pillTextColor
                                        )
                                    }
                                }

                                Text(
                                    text = "···",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = msg.payload,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                                lineHeight = 17.sp,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            val timeStr = try {
                                msg.createdAt.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("h:mm a"))
                            } catch (e: Exception) {
                                "Recently"
                            }
                            val subInfo = if (isSent) "Delivered" else "PEER-HOP-A3F2"

                            Text(
                                text = "$timeStr • $subInfo",
                                fontSize = 10.5.sp,
                                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )

                            // Interactive Relay Button (Simulate Hop) matching Reference Image 2, Screen 10
                            if (msg.status == MessageStatus.QUEUED || (msg.status == MessageStatus.RELAYING && msg.hopCount < msg.maxHops)) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFF2563EB), Color(0xFF4F46E5))
                                            )
                                        )
                                        .clickable { onRelayMessage(msg.messageId) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Sync,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Relay to Peer Node (Simulate Hop) ›",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}
