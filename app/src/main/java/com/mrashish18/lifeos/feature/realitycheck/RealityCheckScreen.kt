package com.mrashish18.lifeos.feature.realitycheck

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrashish18.lifeos.core.model.AnalyzedEvidence
import com.mrashish18.lifeos.core.model.ClaimType
import com.mrashish18.lifeos.core.model.EvidenceStance
import com.mrashish18.lifeos.core.model.RealityCheckResult
import com.mrashish18.lifeos.core.model.SourceQuality
import com.mrashish18.lifeos.core.model.Verdict
import com.mrashish18.lifeos.ui.components.LifeOsEyebrow
import com.mrashish18.lifeos.ui.components.LifeOsPrimaryButton
import com.mrashish18.lifeos.ui.theme.LifeOsAmber50
import com.mrashish18.lifeos.ui.theme.LifeOsAmber700
import com.mrashish18.lifeos.ui.theme.LifeOsBlue50
import com.mrashish18.lifeos.ui.theme.LifeOsBlue700
import com.mrashish18.lifeos.ui.theme.LifeOsGreen50
import com.mrashish18.lifeos.ui.theme.LifeOsGreen700
import com.mrashish18.lifeos.ui.theme.LifeOsIndigo50
import com.mrashish18.lifeos.ui.theme.LifeOsIndigo700
import com.mrashish18.lifeos.ui.theme.LifeOsRed50
import com.mrashish18.lifeos.ui.theme.LifeOsRed700
import com.mrashish18.lifeos.ui.theme.LifeOsSlate100
import com.mrashish18.lifeos.ui.theme.LifeOsSlate500
import com.mrashish18.lifeos.ui.theme.LifeOsSlate700
import com.mrashish18.lifeos.ui.theme.LifeOsSlate900
import com.mrashish18.lifeos.ui.theme.LifeOsTeal50
import com.mrashish18.lifeos.ui.theme.LifeOsTeal700

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RealityCheckScreen(
    viewModel: RealityCheckViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Screen Header
        Column {
            LifeOsEyebrow(text = "TRUTH INTELLIGENCE")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "RealityCheck",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Examine a claim against available evidence.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (val state = uiState) {
            is RealityCheckUiState.Empty -> {
                ClaimInputSection(
                    claimText = "",
                    sourceUrl = "",
                    onClaimChanged = { viewModel.onClaimTextChanged(it) },
                    onUrlChanged = { viewModel.onSourceUrlChanged(it) },
                    onAnalyze = { viewModel.analyzeClaim() },
                    sampleClaims = viewModel.sampleClaims,
                    onSelectSample = { viewModel.selectSampleClaim(it) }
                )
            }
            is RealityCheckUiState.Input -> {
                ClaimInputSection(
                    claimText = state.claimText,
                    sourceUrl = state.sourceUrl,
                    onClaimChanged = { viewModel.onClaimTextChanged(it) },
                    onUrlChanged = { viewModel.onSourceUrlChanged(it) },
                    onAnalyze = { viewModel.analyzeClaim() },
                    sampleClaims = viewModel.sampleClaims,
                    onSelectSample = { viewModel.selectSampleClaim(it) }
                )
            }
            is RealityCheckUiState.Loading -> {
                LoadingInvestigationSection(currentStep = state.currentStep)
            }
            is RealityCheckUiState.Success -> {
                InvestigationReportSection(
                    result = state.result,
                    onNewInvestigation = { viewModel.resetToInput() }
                )
            }
            is RealityCheckUiState.Error -> {
                ErrorInvestigationSection(
                    message = state.message,
                    onRetry = { viewModel.analyzeClaim() },
                    onBackToInput = { viewModel.resetToInput() }
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClaimInputSection(
    claimText: String,
    sourceUrl: String,
    onClaimChanged: (String) -> Unit,
    onUrlChanged: (String) -> Unit,
    onAnalyze: () -> Unit,
    sampleClaims: List<String>,
    onSelectSample: (String) -> Unit
) {
    var showUrlField by remember { mutableStateOf(sourceUrl.isNotBlank()) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            LifeOsEyebrow(text = "CHECK A CLAIM")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Enter a statement to evaluate against verified ground truth.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = claimText,
                onValueChange = onClaimChanged,
                placeholder = {
                    Text(
                        "e.g. Earth orbits the Sun in approximately 365 days, or Antibiotics treat viral infections...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${claimText.length} characters",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                TextButton(onClick = { showUrlField = !showUrlField }) {
                    Text(
                        text = if (showUrlField) "- Hide Context URL" else "+ Add Source URL (Optional)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            AnimatedVisibility(visible = showUrlField) {
                Column {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = sourceUrl,
                        onValueChange = onUrlChanged,
                        placeholder = { Text("https://example.org/article-or-claim") },
                        label = { Text("Optional Source / Reference URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LifeOsPrimaryButton(
                text = "→  Investigate Claim",
                onClick = onAnalyze,
                enabled = claimText.trim().length >= 4,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Sample claims for quick verification
    LifeOsEyebrow(text = "QUICK VERIFICATION QUERIES", color = MaterialTheme.colorScheme.outline)
    Spacer(modifier = Modifier.height(10.dp))

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sampleClaims.forEach { sample ->
            val isSelected = (claimText == sample)
            FilterChip(
                selected = isSelected,
                onClick = { onSelectSample(sample) },
                shape = RoundedCornerShape(10.dp),
                label = {
                    Text(
                        text = sample,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LifeOsIndigo50,
                    selectedLabelColor = LifeOsIndigo700
                )
            )
        }
    }
}

@Composable
private fun LoadingInvestigationSection(currentStep: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(46.dp),
                color = LifeOsIndigo700,
                strokeWidth = 3.5.dp
            )
            Spacer(modifier = Modifier.height(20.dp))
            LifeOsEyebrow(text = "INVESTIGATION IN PROGRESS", color = LifeOsIndigo700)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currentStep,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorInvestigationSection(
    message: String,
    onRetry: () -> Unit,
    onBackToInput: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = LifeOsRed50,
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsRed700.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Investigation Error",
                style = MaterialTheme.typography.titleMedium,
                color = LifeOsRed700,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = LifeOsRed700
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LifeOsRed700)
                ) {
                    Text("Retry Investigation", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onBackToInput,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Edit Input")
                }
            }
        }
    }
}

/**
 * High-grade Investigation Report layout:
 * - REALITYCHECK INVESTIGATION REPORT header
 * - CLAIM
 * - VERDICT Banner with confidence gauge
 * - SYSTEM REASONING
 * - RETRIEVED EVIDENCE (SOURCE 01, SOURCE 02) with strict visual divide:
 *   SOURCE EVIDENCE vs SYSTEM ANALYSIS
 * - DISCLAIMER
 */
@Composable
private fun InvestigationReportSection(
    result: RealityCheckResult,
    onNewInvestigation: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Report Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                LifeOsEyebrow(text = "REALITYCHECK")
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "INVESTIGATION REPORT",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            ClaimTypeBadge(type = result.claim.claimType)
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(14.dp))

        // 1. CLAIM
        LifeOsEyebrow(text = "ANALYZED CLAIM")
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "\"${result.claim.rawText}\"",
            style = MaterialTheme.typography.titleMedium.copy(
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 2. VERDICT BANNER
        VerdictReportBanner(verdict = result.verdict, confidence = result.confidence)

        Spacer(modifier = Modifier.height(18.dp))

        // 3. SYSTEM REASONING
        LifeOsEyebrow(text = "SYSTEM REASONING")
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = result.reasoning,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(22.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(16.dp))

        // 4. RETRIEVED EVIDENCE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LifeOsEyebrow(text = "RETRIEVED EVIDENCE (${result.analyzedEvidence.size})")
            Text(
                text = "Corroboration Corpus",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (result.analyzedEvidence.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No direct evidence documents found in the current verified corpus.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            result.analyzedEvidence.forEachIndexed { index, analyzed ->
                EvidenceReportItem(
                    index = index + 1,
                    analyzed = analyzed
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. MANDATORY LEGAL & EPISTEMIC DISCLAIMER
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                LifeOsEyebrow(
                    text = "LEGAL & EPISTEMIC DISCLAIMER",
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result.disclaimer,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LifeOsPrimaryButton(
            text = "Investigate Another Claim →",
            onClick = onNewInvestigation,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * High-contrast Verdict Banner:
 * Displays verdict, confidence percentage, gauge, and rationale.
 */
@Composable
private fun VerdictReportBanner(
    verdict: Verdict,
    confidence: com.mrashish18.lifeos.core.model.Confidence
) {
    val (bannerColor, onColor, label) = when (verdict) {
        Verdict.SUPPORTED -> Triple(Color(0xFF1B5E20), Color(0xFFE8F5E9), "SUPPORTED")
        Verdict.CONTRADICTED -> Triple(Color(0xFFB71C1C), Color(0xFFFFEBEE), "CONTRADICTED")
        Verdict.MIXED -> Triple(Color(0xFFE65100), Color(0xFFFFF3E0), "MIXED EVIDENCE")
        Verdict.INSUFFICIENT_EVIDENCE -> Triple(Color(0xFF37474F), Color(0xFFECEFF1), "INSUFFICIENT EVIDENCE")
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = bannerColor,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "EVALUATION VERDICT",
                        style = MaterialTheme.typography.labelSmall,
                        color = onColor.copy(alpha = 0.8f),
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = onColor
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${confidence.percentage}%",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = onColor
                    )
                    Text(
                        text = "CONFIDENCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = onColor.copy(alpha = 0.8f),
                        letterSpacing = 0.8.sp,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { confidence.score.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = onColor,
                trackColor = onColor.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = confidence.rationale,
                style = MaterialTheme.typography.bodySmall,
                color = onColor.copy(alpha = 0.9f)
            )
        }
    }
}

/**
 * Report Evidence Item:
 * Enforces strict visual separation between:
 * [ SOURCE EVIDENCE ] vs [ SYSTEM ANALYSIS ]
 */
@Composable
private fun EvidenceReportItem(
    index: Int,
    analyzed: AnalyzedEvidence
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: SOURCE 01 / SOURCE 02
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SOURCE 0$index",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = LifeOsIndigo700,
                    letterSpacing = 1.sp
                )
                SourceQualityBadge(quality = analyzed.evidence.source.quality)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = analyzed.evidence.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 1. RAW SOURCE EVIDENCE SECTION
            // ==========================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    LifeOsEyebrow(
                        text = "SOURCE EVIDENCE",
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${analyzed.evidence.snippet}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Publisher: ${analyzed.evidence.source.name}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = analyzed.evidence.source.url,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 2. SYSTEM ANALYSIS SECTION
            // ==========================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = LifeOsIndigo50.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsIndigo700.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LifeOsEyebrow(
                            text = "SYSTEM ANALYSIS",
                            color = LifeOsIndigo700
                        )
                        Text(
                            text = "Relevance Weight: ${(analyzed.weightContribution * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = LifeOsIndigo700,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StanceBadge(stance = analyzed.stance)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = analyzed.analysisNotes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StanceBadge(stance: EvidenceStance) {
    val (bgColor, textColor, label) = when (stance) {
        EvidenceStance.SUPPORTS -> Triple(LifeOsGreen50, LifeOsGreen700, "Supports Claim")
        EvidenceStance.CONTRADICTS -> Triple(LifeOsRed50, LifeOsRed700, "Contradicts Claim")
        EvidenceStance.MENTIONS -> Triple(LifeOsSlate100, LifeOsSlate700, "Contextual Mention")
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun SourceQualityBadge(quality: SourceQuality) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = quality.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun ClaimTypeBadge(type: ClaimType) {
    val (bgColor, textColor) = when (type) {
        ClaimType.FACTUAL -> LifeOsBlue50 to LifeOsBlue700
        ClaimType.NUMERICAL -> LifeOsTeal50 to LifeOsTeal700
        ClaimType.TEMPORAL -> LifeOsIndigo50 to LifeOsIndigo700
        ClaimType.CAUSAL -> LifeOsAmber50 to LifeOsAmber700
        ClaimType.OPINION -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
        ClaimType.UNSUPPORTED -> LifeOsRed50 to LifeOsRed700
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = type.name,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
