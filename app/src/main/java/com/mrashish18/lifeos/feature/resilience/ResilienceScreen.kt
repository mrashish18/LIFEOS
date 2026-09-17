package com.mrashish18.lifeos.feature.resilience

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrashish18.lifeos.ui.components.LifeOsEyebrow
import com.mrashish18.lifeos.ui.theme.LifeOsAmber50
import com.mrashish18.lifeos.ui.theme.LifeOsAmber700
import com.mrashish18.lifeos.ui.theme.LifeOsBlue50
import com.mrashish18.lifeos.ui.theme.LifeOsBlue700
import com.mrashish18.lifeos.ui.theme.LifeOsGreen50
import com.mrashish18.lifeos.ui.theme.LifeOsGreen700
import com.mrashish18.lifeos.ui.theme.LifeOsIndigo50
import com.mrashish18.lifeos.ui.theme.LifeOsIndigo700
import com.mrashish18.lifeos.ui.theme.LifeOsTeal50
import com.mrashish18.lifeos.ui.theme.LifeOsTeal700

@Composable
fun ResilienceScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Header
        Column {
            LifeOsEyebrow(text = "RESILIENCE INTELLIGENCE")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "RescueMesh",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Offline survivability, opportunistic relay & local sync.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Status Callout
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = LifeOsGreen50,
            border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsGreen700.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(LifeOsGreen700)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LOCAL NETWORK READY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = LifeOsGreen700,
                        letterSpacing = 0.8.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = "ARCHITECTURE PREVIEW",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Conceptual Mesh Topology Visualization
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            LifeOsEyebrow(text = "TOPOLOGY SPECIFICATION")
            Spacer(modifier = Modifier.height(16.dp))

            MeshNodeCard(
                nodeId = "ORIGIN",
                title = "LOCAL DEVICE",
                detail = "Signs message payload with local cryptographic identity and enqueues locally."
            )

            MeshLinkConnector(protocol = "BLE / Wi-Fi Direct Relay")

            MeshNodeCard(
                nodeId = "HOP 01",
                title = "AD-HOC MESH PEER",
                detail = "Store-and-forward epidemic dissemination with bounded 48-hour TTL."
            )

            MeshLinkConnector(protocol = "Cryptographic Reconnection Sync")

            MeshNodeCard(
                nodeId = "EGRESS",
                title = "GATEWAY / DESTINATION",
                detail = "Receives verified message, deduplicates via SHA-256, and dispatches to central ledger."
            )
        }

        // Protocol Specifications
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LifeOsEyebrow(text = "SURVIVABILITY PROTOCOL")

            ProtocolDetailItem(
                title = "Zero-Infrastructure Communication",
                description = "When cellular towers or ISP backbones fail, LIFEOS forms dynamic peer-to-peer ad-hoc meshes across nearby devices."
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            ProtocolDetailItem(
                title = "SHA-256 Deduplication & TTL Guardrails",
                description = "Messages carry deterministic hash signatures and finite time-to-live timestamps to prevent flooding and infinite replication loops."
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            ProtocolDetailItem(
                title = "Opportunistic Ledger Synchronization",
                description = "Upon intermittent connection to any verified internet egress, the local queue drains and synchronizes state seamlessly."
            )
        }

        Spacer(modifier = Modifier.height(56.dp))
    }
}

@Composable
private fun MeshNodeCard(
    nodeId: String,
    title: String,
    detail: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = nodeId,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsIndigo700,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun MeshLinkConnector(protocol: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(1.5.dp)
                .height(8.dp)
                .background(LifeOsIndigo700.copy(alpha = 0.4f))
        )
        Text(
            text = "↓ $protocol",
            style = MaterialTheme.typography.labelSmall,
            color = LifeOsIndigo700,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .width(1.5.dp)
                .height(8.dp)
                .background(LifeOsIndigo700.copy(alpha = 0.4f))
        )
    }
}

@Composable
private fun ProtocolDetailItem(title: String, description: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
    }
}
