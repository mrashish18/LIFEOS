package com.mrashish18.lifeos.feature.resilience

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessagePriority
import com.mrashish18.lifeos.core.model.MessageStatus
import com.mrashish18.lifeos.core.model.MessageType
import com.mrashish18.lifeos.ui.theme.*

enum class ResilienceScreenMode {
    CENTER,      // Screen 8: RescueMesh Center
    EMERGENCY,   // Screen 9: Emergency Message
    QUEUE        // Screen 10: Message Queue
}

@Composable
fun ResilienceScreen(
    viewModel: ResilienceViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var screenMode by remember { mutableStateOf(ResilienceScreenMode.CENTER) }

    // Sync screen mode with viewModel emergency modal state if changed externally
    if (uiState.isEmergencyModalOpen && screenMode != ResilienceScreenMode.EMERGENCY) {
        screenMode = ResilienceScreenMode.EMERGENCY
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFAFCFF),
                        Color(0xFFF8FAFC),
                        Color(0xFFF1F5F9)
                    )
                )
            )
    ) {
        when (screenMode) {
            ResilienceScreenMode.CENTER -> {
                RescueMeshCenterScreen(
                    uiState = uiState,
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
                    onBackToCenter = {
                        screenMode = ResilienceScreenMode.CENTER
                    }
                )
            }
        }
    }
}

/**
 * Screen 8: RescueMesh Center matching 08_rescuemesh_ref.png exactly.
 */
@Composable
private fun RescueMeshCenterScreen(
    uiState: ResilienceUiState,
    onOpenEmergency: () -> Unit,
    onOpenQueue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Screen Header matching Screen 8
        Column {
            Text(
                text = "RESILIENCE INTELLIGENCE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF4338CA),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "RescueMesh",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E1B4B),
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Stay connected when normal networks fail.",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Green LOCAL NETWORK READY Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFDCFCE7))
                .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔔", fontSize = 13.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LOCAL NETWORK READY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF15803D),
                    letterSpacing = 0.6.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Mesh Topology Diagram Card matching Screen 8
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0F2FE))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFFF0FDF4),
                                Color(0xFFE0F2FE),
                                Color(0xFFF8FAFC)
                            )
                        )
                    )
            ) {
                // Background curved dotted lines in Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Soft radial glow aura
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFF38BDF8).copy(alpha = 0.2f), Color.Transparent),
                            center = Offset(w * 0.35f, h * 0.2f),
                            radius = 90.dp.toPx()
                        ),
                        radius = 90.dp.toPx(),
                        center = Offset(w * 0.35f, h * 0.2f)
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFF22D3EE).copy(alpha = 0.2f), Color.Transparent),
                            center = Offset(w * 0.5f, h * 0.5f),
                            radius = 90.dp.toPx()
                        ),
                        radius = 90.dp.toPx(),
                        center = Offset(w * 0.5f, h * 0.5f)
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFF38BDF8).copy(alpha = 0.2f), Color.Transparent),
                            center = Offset(w * 0.35f, h * 0.8f),
                            radius = 90.dp.toPx()
                        ),
                        radius = 90.dp.toPx(),
                        center = Offset(w * 0.35f, h * 0.8f)
                    )

                    // Dotted curved connection: Node 1 -> Node 2
                    val path1 = Path().apply {
                        moveTo(w * 0.36f, h * 0.24f)
                        cubicTo(
                            w * 0.45f, h * 0.32f,
                            w * 0.42f, h * 0.42f,
                            w * 0.5f, h * 0.5f
                        )
                    }
                    drawPath(
                        path1,
                        color = Color(0xFF38BDF8),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                        )
                    )

                    // Dotted curved connection: Node 2 -> Node 3
                    val path2 = Path().apply {
                        moveTo(w * 0.5f, h * 0.5f)
                        cubicTo(
                            w * 0.52f, h * 0.62f,
                            w * 0.42f, h * 0.72f,
                            w * 0.36f, h * 0.78f
                        )
                    }
                    drawPath(
                        path2,
                        color = Color(0xFF38BDF8),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                        )
                    )
                }

                // Node 1: Your Device (Origin)
                Row(
                    modifier = Modifier
                        .padding(start = 28.dp, top = 22.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF3B82F6), Color(0xFF2563EB))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📱", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Your Device",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1B4B)
                        )
                        Text(
                            text = "(Origin)",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Node 2: Nearby Devices (Relay)
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(start = 50.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF06B6D4), Color(0xFF0891B2))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔄", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Nearby Devices",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1B4B)
                        )
                        Text(
                            text = "(Relay)",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Node 3: Destination (Internet / Control Center)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 28.dp, bottom = 22.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👤", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Destination",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1B4B)
                        )
                        Text(
                            text = "(Internet / Control Center)",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metrics Card: 0 Queued | 0 Relaying | 0 Delivered matching Screen 8
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenQueue),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${uiState.queuedCount}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Queued",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(Color(0xFFE2E8F0))
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${uiState.relayingCount}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Relaying",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(Color(0xFFE2E8F0))
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${uiState.deliveredCount}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Delivered",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Send Emergency Message Button matching Screen 8
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF3B82F6),
                            Color(0xFF6366F1),
                            Color(0xFF8B5CF6)
                        )
                    )
                )
                .clickable(onClick = onOpenEmergency),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "🔔", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Send Emergency Message",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}

/**
 * Screen 9: Emergency Message Screen matching 09_emergency_message_ref.png exactly.
 */
@Composable
private fun EmergencyMessageScreen(
    onCancel: () -> Unit,
    onSend: (payload: String, priority: MessagePriority, destination: String) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(MessagePriority.CRITICAL) }
    var destinationText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header matching Screen 9
        Column {
            Text(
                text = "EMERGENCY MESSAGE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF4338CA),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Send Help. Stay Safe.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E1B4B)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Red Warning Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFEF2F2))
                .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "❗", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Use this only for real emergencies.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Message * Field
        Text(
            text = "Message *",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1B4B)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF8FAFC))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            if (messageText.isEmpty()) {
                Text(
                    text = "Describe your situation...",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            BasicTextField(
                value = messageText,
                onValueChange = { messageText = it },
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    color = Color(0xFF1E1B4B),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Priority Field
        Text(
            text = "Priority",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1B4B)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Normal
            val isNormal = selectedPriority == MessagePriority.NORMAL
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isNormal) Color(0xFF4338CA) else Color(0xFFF1F5F9))
                    .border(1.dp, if (isNormal) Color(0xFF4338CA) else Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                    .clickable { selectedPriority = MessagePriority.NORMAL },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🕒", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Normal",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isNormal) Color.White else Color(0xFF334155)
                    )
                }
            }

            // High
            val isHigh = selectedPriority == MessagePriority.HIGH
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isHigh) Color(0xFFF59E0B) else Color(0xFFF1F5F9))
                    .border(1.dp, if (isHigh) Color(0xFFF59E0B) else Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                    .clickable { selectedPriority = MessagePriority.HIGH },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🚨", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "High",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isHigh) Color.White else Color(0xFF334155)
                    )
                }
            }

            // Critical (Glowing red capsule matching Screen 9)
            val isCritical = selectedPriority == MessagePriority.CRITICAL
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .shadow(if (isCritical) 6.dp else 0.dp, RoundedCornerShape(10.dp), spotColor = Color(0xFFEF4444))
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isCritical) Color(0xFFEF4444) else Color(0xFFF1F5F9))
                    .border(1.dp, if (isCritical) Color(0xFFDC2626) else Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                    .clickable { selectedPriority = MessagePriority.CRITICAL },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🚨", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Critical",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCritical) Color.White else Color(0xFF334155)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Destination (Optional) Field
        Text(
            text = "Destination (Optional)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1B4B)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF8FAFC))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 11.dp)
        ) {
            if (destinationText.isEmpty()) {
                Text(
                    text = "Phone, email or ID...",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            BasicTextField(
                value = destinationText,
                onValueChange = { destinationText = it },
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    color = Color(0xFF1E1B4B),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Offline storage info box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEEF2FF))
                .border(1.dp, Color(0xFFE0E7FF), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "ℹ️", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Your message will be stored locally and sent when connectivity is available.",
                    fontSize = 11.sp,
                    color = Color(0xFF334155),
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons Row: Cancel and Send Message
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Cancel button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    .clickable(onClick = onCancel),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1B4B)
                )
            }

            // Send Message button (rich red gradient)
            val isSendEnabled = messageText.isNotBlank()
            Box(
                modifier = Modifier
                    .weight(1.6f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSendEnabled) {
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFEF4444),
                                    Color(0xFFF43F5E)
                                )
                            )
                        } else {
                            androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1))
                        }
                    )
                    .clickable(enabled = isSendEnabled) {
                        onSend(
                            messageText.trim(),
                            selectedPriority,
                            destinationText.trim()
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "🔔", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Send Message",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}

/**
 * Screen 10: Message Queue Screen matching 10_message_queue_ref.png exactly.
 */
@Composable
private fun MessageQueueScreen(
    messages: List<EmergencyMessage>,
    onBackToCenter: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Queued") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header matching Screen 10
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onBackToCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MESSAGE QUEUE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4338CA),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Offline Storage & Sync",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Pills: All, Queued, Sent, Failed matching Screen 10
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Queued", "Sent", "Failed").forEach { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) Color(0xFF4338CA) else Color(0xFFF1F5F9))
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else if (filter == "All") Color(0xFF4338CA) else Color(0xFF64748B)
                    )
                }
            }
        }

        val filteredList = when (selectedFilter) {
            "All" -> messages
            "Queued" -> messages.filter { it.status == MessageStatus.QUEUED }
            "Sent" -> messages.filter { it.status == MessageStatus.SENT || it.status == MessageStatus.DELIVERED }
            "Failed" -> messages.filter { it.status == MessageStatus.FAILED }
            else -> messages
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredList.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📭", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    val emptyTitle = if (messages.isEmpty()) "Queue clear" else "No messages in $selectedFilter queue"
                    val emptyDesc = if (messages.isEmpty()) {
                        "No emergency messages are waiting for relay."
                    } else {
                        "No emergency messages currently match the '$selectedFilter' filter."
                    }
                    Text(
                        text = emptyTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = emptyDesc,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredList.forEach { msg ->
                    val priorityColor = when (msg.priority) {
                        MessagePriority.CRITICAL -> Color(0xFFDC2626)
                        MessagePriority.HIGH -> Color(0xFFD97706)
                        MessagePriority.NORMAL -> Color(0xFF4338CA)
                    }
                    val priorityBg = when (msg.priority) {
                        MessagePriority.CRITICAL -> Color(0xFFFEE2E2)
                        MessagePriority.HIGH -> Color(0xFFFEF3C7)
                        MessagePriority.NORMAL -> Color(0xFFEEF2FF)
                    }
                    val iconBg = when (msg.priority) {
                        MessagePriority.CRITICAL -> Color(0xFFEF4444)
                        MessagePriority.HIGH -> Color(0xFFF59E0B)
                        MessagePriority.NORMAL -> Color(0xFF8B5CF6)
                    }
                    val iconEmoji = when (msg.priority) {
                        MessagePriority.CRITICAL -> "🚨"
                        MessagePriority.HIGH -> "📦"
                        MessagePriority.NORMAL -> "📝"
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        shadowElevation = 1.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(iconBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = iconEmoji, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = msg.payload.take(28) + if (msg.payload.length > 28) "..." else "",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E1B4B)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(priorityBg)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = msg.priority.name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = priorityColor
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                val timeStr = try {
                                    msg.createdAt.atZone(java.time.ZoneId.systemDefault()).format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))
                                } catch (e: Exception) {
                                    "Recently"
                                }
                                val ttlHours = maxOf(1L, (msg.expiresAt.epochSecond - msg.createdAt.epochSecond) / 3600)
                                Text(
                                    text = "${msg.status.name}  •  $timeStr",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "TTL: ${ttlHours}h  •  ${msg.hopCount} hops",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "⋮",
                                fontSize = 16.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Callout Banner matching Screen 10
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0F2FE))
                .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⚡", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Messages are stored locally and will be automatically sent when connectivity returns.",
                    fontSize = 11.sp,
                    color = Color(0xFF0C4A6E),
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}
