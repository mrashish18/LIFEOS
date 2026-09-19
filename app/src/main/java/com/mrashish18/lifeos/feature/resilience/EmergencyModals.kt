package com.mrashish18.lifeos.feature.resilience

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessagePriority
import com.mrashish18.lifeos.core.model.MessageStatus
import com.mrashish18.lifeos.core.model.MessageType
import com.mrashish18.lifeos.ui.components.LifeOsEyebrow
import com.mrashish18.lifeos.ui.components.LifeOsGradientButton
import com.mrashish18.lifeos.ui.components.LifeOsPrimaryButton
import com.mrashish18.lifeos.ui.components.LifeOsSecondaryButton
import com.mrashish18.lifeos.ui.components.MessageLifecycleTimeline
import com.mrashish18.lifeos.ui.theme.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm:ss")
    .withZone(ZoneId.systemDefault())

@Composable
fun EmergencyModeDialog(
    onDismiss: () -> Unit,
    onSend: (payload: String, type: MessageType, priority: MessagePriority, recipientId: String?) -> Unit
) {
    var payload by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MessageType.EMERGENCY) }
    var selectedPriority by remember { mutableStateOf(MessagePriority.CRITICAL) }
    var recipientId by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsRed700.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EMERGENCY MESSAGE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = LifeOsRed700,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(LifeOsRed700)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Send Help. Stay Safe.",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LifeOsRed50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsRed100)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⚠️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Use this only for real emergencies. Messages are prioritized for offline relay.",
                            style = MaterialTheme.typography.labelSmall,
                            color = LifeOsRed700,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Column {
                    Text(
                        text = "TRIAGE PRIORITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MessagePriority.values().forEach { priority ->
                            val isSelected = selectedPriority == priority
                            val buttonColor = if (isSelected) {
                                when (priority) {
                                    MessagePriority.CRITICAL -> LifeOsRed700
                                    MessagePriority.HIGH -> LifeOsAmber700
                                    MessagePriority.NORMAL -> LifeOsIndigo700
                                }
                            } else LifeOsSlate100

                            val textColor = if (isSelected) LifeOsWhite else LifeOsSlate700

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(buttonColor)
                                    .clickable { selectedPriority = priority },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = priority.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                        }
                    }
                }

                Column {
                    Text(
                        text = "INCIDENT TYPE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(MessageType.EMERGENCY, MessageType.MEDICAL, MessageType.RESCUE_REQUEST).forEach { type ->
                            val isSelected = selectedType == type
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) LifeOsIndigo700 else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedType = type }
                            ) {
                                Text(
                                    text = type.name.replace("_", " "),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) LifeOsWhite else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Column {
                    Text(
                        text = "SITUATION DETAILS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = payload,
                        onValueChange = { payload = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        placeholder = {
                            Text(
                                text = "Describe your situation, immediate hazards, condition, and needs...",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LifeOsRed700,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }

                OutlinedTextField(
                    value = recipientId,
                    onValueChange = { recipientId = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Target Node (optional, default: BROADCAST)", style = MaterialTheme.typography.labelSmall) },
                    placeholder = { Text("e.g. GATEWAY-EGRESS", style = MaterialTheme.typography.bodySmall) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LifeOsGradientButton(
                        text = "⚠  SEND EMERGENCY MESSAGE",
                        gradient = LifeOsGradients.emergency,
                        onClick = {
                            if (payload.isNotBlank()) {
                                onSend(payload.trim(), selectedType, selectedPriority, recipientId.ifBlank { null })
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = payload.isNotBlank()
                    )

                    LifeOsSecondaryButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun MessageDetailDialog(
    message: EmergencyMessage,
    onDismiss: () -> Unit,
    onTestRelayHop: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 14.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LifeOsEyebrow(text = "MESSAGE AUDIT TRAIL")
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (message.status) {
                            MessageStatus.DELIVERED -> LifeOsGreen50
                            MessageStatus.SENT -> LifeOsBlue50
                            MessageStatus.RELAYING -> LifeOsIndigo50
                            MessageStatus.QUEUED -> LifeOsAmber50
                            MessageStatus.EXPIRED, MessageStatus.FAILED -> LifeOsRed50
                            else -> LifeOsSlate100
                        }
                    ) {
                        Text(
                            text = message.status.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when (message.status) {
                                MessageStatus.DELIVERED -> LifeOsGreen700
                                MessageStatus.SENT -> LifeOsBlue700
                                MessageStatus.RELAYING -> LifeOsIndigo700
                                MessageStatus.QUEUED -> LifeOsAmber700
                                MessageStatus.EXPIRED, MessageStatus.FAILED -> LifeOsRed700
                                else -> LifeOsSlate700
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                // Visual store-and-forward lifecycle timeline
                MessageLifecycleTimeline(
                    status = message.status.name,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "PAYLOAD",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message.payload,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                val stateExplanation = when (message.status) {
                    MessageStatus.QUEUED -> "Stored locally in SQLite. Waiting for network gateway or nearby store-and-forward peer."
                    MessageStatus.RELAYING -> "In active store-and-forward relay. Relayed across ${message.hopCount} mesh hop(s)."
                    MessageStatus.SENT -> "Dispatched through network gateway to egress. Awaiting end-recipient delivery acknowledgment."
                    MessageStatus.DELIVERED -> "Verified delivered. End-to-end receipt recorded."
                    MessageStatus.EXPIRED -> "Message time-to-live expired (${timeFormatter.format(message.expiresAt)}). Dropped from active relay."
                    MessageStatus.FAILED -> "Transmission failed or hop limit (${message.maxHops}) exceeded."
                    MessageStatus.DUPLICATE -> "Duplicate message signature detected. Discarded to prevent flooding loops."
                    MessageStatus.DRAFT -> "Draft message."
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LifeOsIndigo50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsIndigo700.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "ℹ️ $stateExplanation",
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeOsIndigo700,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DetailRow(label = "Message ID", value = message.messageId.take(16) + "...")
                    DetailRow(label = "Sender Node", value = message.senderId)
                    DetailRow(label = "Recipient", value = message.recipientId ?: "BROADCAST")
                    DetailRow(label = "Priority", value = message.priority.name)
                    DetailRow(label = "Type", value = message.type.name)
                    DetailRow(label = "Hops", value = "${message.hopCount} / ${message.maxHops}")
                    DetailRow(label = "Transport", value = message.transportType.name)
                    DetailRow(label = "Created", value = timeFormatter.format(message.createdAt))
                    DetailRow(label = "Expires (TTL)", value = timeFormatter.format(message.expiresAt))
                    DetailRow(label = "SHA-256", value = message.fingerprintSha256.take(24) + "...")
                }

                if (message.relayHistory.isNotEmpty()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "RELAY HISTORY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 9.sp
                        )
                        message.relayHistory.forEach { hop ->
                            Text(
                                text = "• Hop ${hop.hopNumber} via ${hop.relayedBy} (${hop.transportUsed.name}) at ${timeFormatter.format(hop.relayedAt)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                if (message.status == MessageStatus.QUEUED || message.status == MessageStatus.RELAYING) {
                    LifeOsSecondaryButton(
                        text = "Simulate Local Relay Hop",
                        onClick = onTestRelayHop,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                LifeOsPrimaryButton(
                    text = "Close",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = LifeOsSlate600,
            fontSize = 11.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
        )
    }
}
