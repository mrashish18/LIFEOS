package com.mrashish18.lifeos.feature.realitycheck

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrashish18.lifeos.core.model.AnalyzedEvidence
import com.mrashish18.lifeos.core.model.InvestigationRecord
import com.mrashish18.lifeos.core.model.RealityCheckResult
import com.mrashish18.lifeos.core.model.Verdict
import com.mrashish18.lifeos.ui.theme.*

@Composable
fun RealityCheckScreen(
    viewModel: RealityCheckViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val recentInvestigations by viewModel.recentInvestigations.collectAsState()

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
        when (val state = uiState) {
            is RealityCheckUiState.Empty -> {
                TruthInputScreen(
                    claimText = "",
                    recentInvestigations = recentInvestigations,
                    onClaimChanged = { viewModel.onClaimTextChanged(it) },
                    onAnalyze = { viewModel.analyzeClaim() },
                    onSelectQuick = { claim ->
                        viewModel.onClaimTextChanged(claim)
                        viewModel.analyzeClaim()
                    },
                    onSelectInvestigation = { record ->
                        viewModel.selectInvestigation(record)
                    }
                )
            }
            is RealityCheckUiState.Input -> {
                TruthInputScreen(
                    claimText = state.claimText,
                    recentInvestigations = recentInvestigations,
                    onClaimChanged = { viewModel.onClaimTextChanged(it) },
                    onAnalyze = { viewModel.analyzeClaim() },
                    onSelectQuick = { claim ->
                        viewModel.onClaimTextChanged(claim)
                        viewModel.analyzeClaim()
                    },
                    onSelectInvestigation = { record ->
                        viewModel.selectInvestigation(record)
                    }
                )
            }
            is RealityCheckUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = Color(0xFF4338CA),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = state.currentStep,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
            is RealityCheckUiState.Success -> {
                TruthResultScreen(
                    result = state.result,
                    onBack = { viewModel.resetToInput() }
                )
            }
            is RealityCheckUiState.Error -> {
                TruthInputScreen(
                    claimText = state.lastClaimText,
                    recentInvestigations = recentInvestigations,
                    onClaimChanged = { viewModel.onClaimTextChanged(it) },
                    onAnalyze = { viewModel.analyzeClaim() },
                    onSelectQuick = { claim ->
                        viewModel.onClaimTextChanged(claim)
                        viewModel.analyzeClaim()
                    },
                    onSelectInvestigation = { record ->
                        viewModel.selectInvestigation(record)
                    }
                )
            }
        }
    }
}

/**
 * Screen 6: Truth Input Screen matching 06_truth_input_ref.png exactly.
 */
@Composable
private fun TruthInputScreen(
    claimText: String,
    recentInvestigations: List<InvestigationRecord>,
    onClaimChanged: (String) -> Unit,
    onAnalyze: () -> Unit,
    onSelectQuick: (String) -> Unit,
    onSelectInvestigation: (InvestigationRecord) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Screen Header matching Screen 6
        Column {
            Text(
                text = "TRUTH INTELLIGENCE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF4338CA),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "RealityCheck",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E1B4B),
                letterSpacing = (-0.3).sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Examine claims. Find the truth.",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Input Card matching Screen 6
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Inner input box with link icon
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (claimText.isEmpty()) {
                                Text(
                                    text = "Enter a claim, question, or URL...",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            BasicTextField(
                                value = claimText,
                                onValueChange = onClaimChanged,
                                textStyle = TextStyle(
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E1B4B),
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🔗",
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Character count right-aligned
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${claimText.length}/500",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Investigate Claim Gradient Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF3B82F6),
                                    Color(0xFF6366F1),
                                    Color(0xFF8B5CF6)
                                )
                            )
                        )
                        .clickable(onClick = onAnalyze),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🔍", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Investigate Claim",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Examples Section matching Screen 6
        Text(
            text = "Quick examples",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1B4B)
        )
        Spacer(modifier = Modifier.height(10.dp))

        val quickExamples = listOf(
            "Antibiotics cure viral infections?",
            "Is drinking 8 glasses of water necessary?",
            "Earth orbits the Sun in 365 days?",
            "5G causes health problems?"
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            quickExamples.forEach { example ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                        .clickable { onSelectQuick(example) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = example,
                        fontSize = 11.sp,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Recent Investigations Section matching Screen 6 with dynamic Room history
        Text(
            text = "Recent Investigations",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1B4B)
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (recentInvestigations.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectQuick("Earth orbits the Sun in approximately 365 days") },
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
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛡️", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Earth orbits the Sun in 365 d...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1B4B)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFDCFCE7))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "SUPPORTED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "92% • 2 sources",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Text(
                        text = "⋮",
                        fontSize = 16.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recentInvestigations.forEach { record ->
                    val (badgeBg, badgeTextColor, iconBg) = when (record.verdict) {
                        Verdict.SUPPORTED -> Triple(Color(0xFFDCFCE7), Color(0xFF16A34A), Color(0xFFDCFCE7))
                        Verdict.CONTRADICTED -> Triple(Color(0xFFFEE2E2), Color(0xFFDC2626), Color(0xFFFEE2E2))
                        Verdict.MIXED -> Triple(Color(0xFFFFEDD5), Color(0xFFEA580C), Color(0xFFFFEDD5))
                        Verdict.INSUFFICIENT_EVIDENCE -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), Color(0xFFFEF3C7))
                    }
                    val iconText = when (record.verdict) {
                        Verdict.SUPPORTED -> "✓"
                        Verdict.CONTRADICTED -> "✕"
                        Verdict.MIXED -> "!"
                        Verdict.INSUFFICIENT_EVIDENCE -> "?"
                    }

                    val displayClaim = if (record.claimText.length > 32) {
                        record.claimText.take(30) + "..."
                    } else {
                        record.claimText
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectInvestigation(record) },
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
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(iconBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = iconText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = badgeTextColor
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = displayClaim,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1B4B)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(badgeBg)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = record.verdict.name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = badgeTextColor
                                        )
                                    }
                                }
                                 Spacer(modifier = Modifier.height(2.dp))
                                 Text(
                                     text = "${record.confidencePercentage}% • ${record.sourcesCount} source${if (record.sourcesCount == 1) "" else "s"}",
                                     fontSize = 10.sp,
                                     color = Color(0xFF64748B)
                                 )
                            }

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

        Spacer(modifier = Modifier.height(60.dp))
    }
}

/**
 * Screen 7: Truth Result Screen matching 07_truth_result_ref.png exactly.
 * Clearly separates AUTHORITATIVE EVIDENCE from LIFEOS INTERPRETATION.
 */
@Composable
private fun TruthResultScreen(
    result: RealityCheckResult,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header: INVESTIGATION REPORT matching Screen 7
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onBack),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "← INVESTIGATION REPORT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2563EB),
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dynamic styling based on Verdict
        val bannerGradients = when (result.verdict) {
            Verdict.SUPPORTED -> listOf(Color(0xFF047857), Color(0xFF065F46), Color(0xFF064E3B))
            Verdict.CONTRADICTED -> listOf(Color(0xFFB91C1C), Color(0xFF991B1B), Color(0xFF7F1D1D))
            Verdict.MIXED -> listOf(Color(0xFFC2410C), Color(0xFF9A3412), Color(0xFF7C2D12))
            Verdict.INSUFFICIENT_EVIDENCE -> listOf(Color(0xFFB45309), Color(0xFF92400E), Color(0xFF78350F))
        }
        val iconSymbol = when (result.verdict) {
            Verdict.SUPPORTED -> "✓"
            Verdict.CONTRADICTED -> "✕"
            Verdict.MIXED -> "!"
            Verdict.INSUFFICIENT_EVIDENCE -> "?"
        }
        val iconColor = when (result.verdict) {
            Verdict.SUPPORTED -> Color(0xFF10B981)
            Verdict.CONTRADICTED -> Color(0xFFEF4444)
            Verdict.MIXED -> Color(0xFFF97316)
            Verdict.INSUFFICIENT_EVIDENCE -> Color(0xFFF59E0B)
        }
        val subColor = when (result.verdict) {
            Verdict.SUPPORTED -> Color(0xFFA7F3D0)
            Verdict.CONTRADICTED -> Color(0xFFFECACA)
            Verdict.MIXED -> Color(0xFFFED7AA)
            Verdict.INSUFFICIENT_EVIDENCE -> Color(0xFFFDE68A)
        }

        // Unified Hero Card with Scenic Mountain Banner matching Screen 7
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
        ) {
            Column {
                // Top Scenic Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                        .background(Brush.linearGradient(bannerGradients))
                ) {
                    // Stylized layered mountain ridges
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Back ridge
                        val backRidge = Path().apply {
                            moveTo(w * 0.4f, h)
                            lineTo(w * 0.65f, h * 0.35f)
                            lineTo(w * 0.85f, h * 0.65f)
                            lineTo(w * 0.95f, h * 0.25f)
                            lineTo(w, h * 0.4f)
                            lineTo(w, h)
                            close()
                        }
                        drawPath(backRidge, color = iconColor.copy(alpha = 0.2f))

                        // Front ridge
                        val frontRidge = Path().apply {
                            moveTo(w * 0.5f, h)
                            lineTo(w * 0.72f, h * 0.45f)
                            lineTo(w * 0.82f, h * 0.55f)
                            lineTo(w * 0.92f, h * 0.15f)
                            lineTo(w * 0.96f, h * 0.4f)
                            lineTo(w, h * 0.3f)
                            lineTo(w, h)
                            close()
                        }
                        drawPath(frontRidge, color = iconColor.copy(alpha = 0.35f))
                    }

                    // Content over banner
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // White badge with verdict icon
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = iconSymbol,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = iconColor
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = result.verdict.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${result.confidence.percentage}% confidence",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = subColor
                            )
                        }
                    }
                }

                // Details below scenic banner: LIFEOS INTERPRETATION
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFEEF2FF))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LIFEOS INTERPRETATION",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4338CA),
                                letterSpacing = 0.5.sp
                            )
                        }

                        Text(
                            text = result.claim.domainCategory.name,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "\"${result.claim.rawText}\"",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1B4B),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dynamic Claim & Domain Tags
                    val dynamicTags = buildList {
                        add(result.claim.claimType.name)
                        add(result.claim.domainCategory.name)
                        if (result.claim.rawText.contains(Regex("\\b\\d+\\b"))) add("NUMERICAL")
                    }.distinct()

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        dynamicTags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2563EB)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Deterministic Assessment",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = result.interpretation,
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Scoring Rationale",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = result.confidence.rationale,
                        fontSize = 10.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Authoritative Evidence Section matching Screen 7
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AUTHORITATIVE EVIDENCE (${result.analyzedEvidence.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2563EB)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF1F5F9))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "GROUND TRUTH SOURCES",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            result.analyzedEvidence.forEachIndexed { index, analyzed ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Source 0${index + 1}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = analyzed.evidence.source.quality.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF475569)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = analyzed.evidence.source.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1B4B)
                        )

                        if (analyzed.evidence.source.authorityRationale.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Institutional Criteria: ${analyzed.evidence.source.authorityRationale}",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "\"${analyzed.evidence.snippet}\"",
                            fontSize = 11.sp,
                            color = Color(0xFF475569),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "URL: ${analyzed.evidence.source.url ?: "Verified Institutional Database"}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2563EB)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}
