package com.mrashish18.lifeos.feature.intelligence

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
import com.mrashish18.lifeos.ui.theme.LifeOsElectricIndigo
import com.mrashish18.lifeos.ui.theme.LifeOsGreen50
import com.mrashish18.lifeos.ui.theme.LifeOsGreen700
import com.mrashish18.lifeos.ui.theme.LifeOsIndigo50
import com.mrashish18.lifeos.ui.theme.LifeOsIndigo700
import com.mrashish18.lifeos.ui.theme.LifeOsSlate100
import com.mrashish18.lifeos.ui.theme.LifeOsSlate500
import com.mrashish18.lifeos.ui.theme.LifeOsSlate700
import com.mrashish18.lifeos.ui.theme.LifeOsTeal50
import com.mrashish18.lifeos.ui.theme.LifeOsTeal700

@Composable
fun IntelligenceScreen(modifier: Modifier = Modifier) {
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
            LifeOsEyebrow(text = "COGNITIVE ARCHITECTURE")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "LIFEOS Intelligence",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "How LIFEOS observes, decides, and adapts to you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // The Visual Pipeline Flow: OBSERVE -> UNDERSTAND -> DECIDE -> ADAPT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(18.dp)
        ) {
            LifeOsEyebrow(text = "CLOSED-LOOP ADAPTIVE ENGINE")
            Spacer(modifier = Modifier.height(14.dp))

            ArchitectureStepRow(
                stepNumber = "01",
                stageName = "OBSERVE",
                subsystem = "Context Engine",
                description = "Monitors system time, day of week, network state, current workload density, and active task focus."
            )

            ArchitectureConnector()

            ArchitectureStepRow(
                stepNumber = "02",
                stageName = "UNDERSTAND",
                subsystem = "Behavior Model",
                description = "Tracks completed vs abandoned tasks, computes completion velocity, postponement habits, and category momentum."
            )

            ArchitectureConnector()

            ArchitectureStepRow(
                stepNumber = "03",
                stageName = "DECIDE",
                subsystem = "Decision Engine",
                description = "Evaluates deterministic heuristics, scores urgency and continuity, and calculates calibrated Bayesian confidence."
            )

            ArchitectureConnector()

            ArchitectureStepRow(
                stepNumber = "04",
                stageName = "ADAPT",
                subsystem = "Learning Loop",
                description = "Logs behavioral feedback events (start, postpone, abandon) to update the UserBehaviorModel dynamically in Room."
            )
        }

        // Core Subsystems Breakdown
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LifeOsEyebrow(text = "ARCHITECTURAL PRINCIPLES")

            PrincipleItem(
                title = "Deterministic & Explainable",
                description = "Every recommendation exposes an explicit breakdown of contributing factors with weighted attributions. No black-box hallucinations."
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            PrincipleItem(
                title = "Calibrated Confidence",
                description = "Decision scores reflect empirical certainty based on corroborated context rather than inflated heuristic claims."
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            PrincipleItem(
                title = "Strict On-Device Privacy",
                description = "Context, tasks, and behavioral signals are processed locally on-device using Room SQLite with zero external telemetry leakage."
            )
        }

        Spacer(modifier = Modifier.height(56.dp))
    }
}

@Composable
private fun ArchitectureStepRow(
    stepNumber: String,
    stageName: String,
    subsystem: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = LifeOsIndigo50,
            border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsIndigo700.copy(alpha = 0.2f))
        ) {
            Text(
                text = stepNumber,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = LifeOsIndigo700,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stageName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "·",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = subsystem,
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeOsIndigo700,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun ArchitectureConnector() {
    Row(
        modifier = Modifier
            .padding(start = 14.dp, top = 2.dp, bottom = 2.dp)
            .height(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(1.5.dp)
                .height(16.dp)
                .background(LifeOsIndigo700.copy(alpha = 0.4f))
        )
    }
}

@Composable
private fun PrincipleItem(title: String, description: String) {
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
