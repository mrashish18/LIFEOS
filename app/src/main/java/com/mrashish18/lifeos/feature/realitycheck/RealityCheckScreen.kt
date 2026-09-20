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
    onOpenDrawer: () -> Unit = {},
    isDarkMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val recentInvestigations by viewModel.recentInvestigations.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0B1020),
                            Color(0xFF111827),
                            Color(0xFF0B1020)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFAFCFF),
                            Color(0xFFF8FAFC),
                            Color(0xFFF1F5F9)
                        )
                    )
                }
            )
    ) {
        when (val state = uiState) {
            is RealityCheckUiState.Empty -> {
                TruthInputScreen(
                    claimText = "",
                    recentInvestigations = recentInvestigations,
                    onClaimChanged = { if (it.length <= 500) viewModel.onClaimTextChanged(it) },
                    onAnalyze = { viewModel.analyzeClaim() },
                    onSelectQuick = { claim ->
                        viewModel.onClaimTextChanged(claim.take(500))
                        viewModel.analyzeClaim()
                    },
                    onSelectInvestigation = { record ->
                        viewModel.selectInvestigation(record)
                    },
                    onOpenDrawer = onOpenDrawer,
                    isDarkMode = isDarkMode
                )
            }
            is RealityCheckUiState.Input -> {
                TruthInputScreen(
                    claimText = state.claimText,
                    recentInvestigations = recentInvestigations,
                    onClaimChanged = { if (it.length <= 500) viewModel.onClaimTextChanged(it) },
                    onAnalyze = { viewModel.analyzeClaim() },
                    onSelectQuick = { claim ->
                        viewModel.onClaimTextChanged(claim.take(500))
                        viewModel.analyzeClaim()
                    },
                    onSelectInvestigation = { record ->
                        viewModel.selectInvestigation(record)
                    },
                    onOpenDrawer = onOpenDrawer,
                    isDarkMode = isDarkMode
                )
            }
            is RealityCheckUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = state.currentStep,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }
            }
            is RealityCheckUiState.Success -> {
                TruthResultScreen(
                    result = state.result,
                    onBack = { viewModel.resetToInput() },
                    isDarkMode = isDarkMode
                )
            }
            is RealityCheckUiState.Error -> {
                TruthInputScreen(
                    claimText = state.lastClaimText,
                    recentInvestigations = recentInvestigations,
                    errorMessage = state.message,
                    onClaimChanged = { if (it.length <= 500) viewModel.onClaimTextChanged(it) },
                    onAnalyze = { viewModel.analyzeClaim() },
                    onSelectQuick = { claim ->
                        viewModel.onClaimTextChanged(claim.take(500))
                        viewModel.analyzeClaim()
                    },
                    onSelectInvestigation = { record ->
                        viewModel.selectInvestigation(record)
                    },
                    onOpenDrawer = onOpenDrawer,
                    isDarkMode = isDarkMode
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
    errorMessage: String? = null,
    onClaimChanged: (String) -> Unit,
    onAnalyze: () -> Unit,
    onSelectQuick: (String) -> Unit,
    onSelectInvestigation: (InvestigationRecord) -> Unit,
    onOpenDrawer: () -> Unit = {},
    isDarkMode: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // ── RealityCheck Header Card (ABOVE scenic image) ──
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = if (isDarkMode) Color(0xFF111827) else Color.White,
            shadowElevation = if (isDarkMode) 0.dp else 2.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Top row: hamburger + title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    com.mrashish18.lifeos.ui.components.LifeOsMenuButton(
                        onClick = onOpenDrawer,
                        isDarkMode = isDarkMode
                    )
                    Column {
                        Text(
                            text = "RealityCheck",
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                            letterSpacing = (-0.3).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Good questions. Better information.\nA clearer view of reality.",
                            fontSize = 11.5.sp,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Supporting text
                Text(
                    text = "Verify claims. Compare evidence.\nMake a clearer decision.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                    lineHeight = 19.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Scenic Mountain Visual (BELOW header) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(18.dp))
        ) {
            com.mrashish18.lifeos.ui.components.RealityCheckScenicHeader(
                isDarkMode = isDarkMode,
                modifier = Modifier.fillMaxSize()
            )

            // Overlay text on scenic image
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "\u201CBetter information\nleads to a brighter\ntomorrow.\u201D",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkMode) Color.White.copy(alpha = 0.92f) else Color(0xFF4C1D95),
                        lineHeight = 16.sp
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        listOf("QUESTION", "EXPLORE", "VERIFY", "UNDERSTAND").forEach { word ->
                            Text(
                                text = word,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isDarkMode) Color.White.copy(alpha = 0.75f) else Color(0xFF6D28D9).copy(alpha = 0.7f),
                                letterSpacing = 1.5.sp,
                                lineHeight = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── Error message (if any) ──
        errorMessage?.let { errorMsg ->
            Surface(
                color = if (isDarkMode) Color(0xFF7F1D1D).copy(alpha = 0.3f) else Color(0xFFFEF2F2),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF991B1B) else Color(0xFFFECACA)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "\u26A0\uFE0F", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMsg,
                            fontSize = 11.5.sp,
                            color = if (isDarkMode) Color(0xFFFCA5A5) else Color(0xFFDC2626),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Recovery: Select one of the verified quick examples below or enter a specific proposition.",
                        fontSize = 10.5.sp,
                        color = if (isDarkMode) Color(0xFFF87171) else Color(0xFF991B1B)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Main Input Card matching Screen 6
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkMode) Color(0xFF111827) else Color.White,
            shadowElevation = if (isDarkMode) 0.dp else 1.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Inner input box with link icon
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF8FAFC))
                        .border(
                            1.dp,
                            if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0),
                            RoundedCornerShape(12.dp)
                        )
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
                                    fontSize = 13.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            BasicTextField(
                                value = claimText,
                                onValueChange = onClaimChanged,
                                textStyle = TextStyle(
                                    fontSize = 13.sp,
                                    color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🔗",
                            fontSize = 15.sp
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
                        fontSize = 10.5.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Investigate Claim Gradient Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
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
                        Text(text = "🔍", fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Investigate Claim",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Examples Section matching Screen 6
        Text(
            text = "Quick examples",
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
            letterSpacing = (-0.2).sp
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
                        .background(if (isDarkMode) Color(0xFF172033) else Color(0xFFF8FAFC))
                        .border(
                            1.dp,
                            if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onSelectQuick(example) }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = example,
                        fontSize = 12.sp,
                        color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF1E293B),
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Recent Investigations Section matching Screen 6 with dynamic Room history
        Text(
            text = "Recent Investigations",
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
            letterSpacing = (-0.2).sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (recentInvestigations.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkMode) Color(0xFF172033) else Color.White,
                shadowElevation = if (isDarkMode) 0.dp else 1.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🛡️", fontSize = 26.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No investigations yet",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Investigate a claim to build your trusted evidence history.",
                        fontSize = 11.5.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recentInvestigations.forEach { record ->
                    val (badgeBg, badgeTextColor, iconBg) = when (record.verdict) {
                        Verdict.SUPPORTED -> {
                            if (isDarkMode) Triple(Color(0xFF064E3B).copy(alpha = 0.5f), Color(0xFF4ADE80), Color(0xFF064E3B).copy(alpha = 0.5f))
                            else Triple(Color(0xFFDCFCE7), Color(0xFF16A34A), Color(0xFFDCFCE7))
                        }
                        Verdict.CONTRADICTED -> {
                            if (isDarkMode) Triple(Color(0xFF7F1D1D).copy(alpha = 0.5f), Color(0xFFFCA5A5), Color(0xFF7F1D1D).copy(alpha = 0.5f))
                            else Triple(Color(0xFFFEE2E2), Color(0xFFDC2626), Color(0xFFFEE2E2))
                        }
                        Verdict.MIXED -> {
                            if (isDarkMode) Triple(Color(0xFF78350F).copy(alpha = 0.5f), Color(0xFFFDBA74), Color(0xFF78350F).copy(alpha = 0.5f))
                            else Triple(Color(0xFFFFEDD5), Color(0xFFEA580C), Color(0xFFFFEDD5))
                        }
                        Verdict.INSUFFICIENT_EVIDENCE -> {
                            if (isDarkMode) Triple(Color(0xFF78350F).copy(alpha = 0.5f), Color(0xFFFCD34D), Color(0xFF78350F).copy(alpha = 0.5f))
                            else Triple(Color(0xFFFEF3C7), Color(0xFFD97706), Color(0xFFFEF3C7))
                        }
                    }
                    val iconText = when (record.verdict) {
                        Verdict.SUPPORTED -> "✓"
                        Verdict.CONTRADICTED -> "✕"
                        Verdict.MIXED -> "!"
                        Verdict.INSUFFICIENT_EVIDENCE -> "?"
                    }

                    val displayClaim = if (record.claimText.length > 34) {
                        record.claimText.take(32) + "..."
                    } else {
                        record.claimText
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectInvestigation(record) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDarkMode) Color(0xFF111827) else Color.White,
                        shadowElevation = if (isDarkMode) 0.dp else 1.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(iconBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = iconText,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = badgeTextColor
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = displayClaim,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(badgeBg)
                                            .padding(horizontal = 7.dp, vertical = 2.5.dp)
                                    ) {
                                        Text(
                                            text = record.verdict.name,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = badgeTextColor
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "${record.confidencePercentage}% • ${record.sourcesCount} source${if (record.sourcesCount == 1) "" else "s"}",
                                    fontSize = 10.5.sp,
                                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            }

                            Text(
                                text = "⋮",
                                fontSize = 16.sp,
                                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
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
    onBack: () -> Unit,
    isDarkMode: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header: Investigation Result matching Screen 7
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onBack),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "← Investigation Result",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "A clearer view of reality.",
            fontSize = 11.5.sp,
            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Dynamic styling based on Verdict
        val bannerGradients = when (result.verdict) {
            Verdict.SUPPORTED -> listOf(Color(0xFF059669), Color(0xFF047857), Color(0xFF064E3B))
            Verdict.CONTRADICTED -> listOf(Color(0xFFEF4444), Color(0xFFDC2626), Color(0xFF991B1B))
            Verdict.MIXED -> listOf(Color(0xFFF97316), Color(0xFFEA580C), Color(0xFF9A3412))
            Verdict.INSUFFICIENT_EVIDENCE -> listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFF78350F))
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

        // 3D Glossy Verdict Banner matching Reference Image 2 Screen 7
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(bannerGradients))
            ) {
                // Top Gloss Reflection for 3D effect
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.White.copy(alpha = 0.28f), Color.Transparent)
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = iconSymbol,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = iconColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = result.verdict.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.25f))
                                    .padding(horizontal = 8.dp, vertical = 2.5.dp)
                            ) {
                                Text(
                                    text = "${result.confidence.percentage}% Confidence",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (result.verdict) {
                                Verdict.CONTRADICTED -> "Strong evidence against this claim"
                                Verdict.SUPPORTED -> "Strong evidence supporting this claim"
                                Verdict.MIXED -> "Evidence presents mixed conclusions"
                                Verdict.INSUFFICIENT_EVIDENCE -> "Inconclusive evidence found"
                            },
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 10-Stage Deterministic Verification Pipeline Strip
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkMode) Color(0xFF172033) else Color(0xFFF8FAFC),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "CLAIM", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA))
                Text(text = "→", fontSize = 9.sp, color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                Text(text = "ANALYSIS", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color(0xFF60A5FA) else Color(0xFF2563EB))
                Text(text = "→", fontSize = 9.sp, color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                Text(text = "EVIDENCE", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color(0xFF2DD4BF) else Color(0xFF0D9488))
                Text(text = "→", fontSize = 9.sp, color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                Text(text = "CONFIDENCE", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color(0xFFFBBF24) else Color(0xFFD97706))
                Text(text = "→", fontSize = 9.sp, color = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                Text(text = "VERDICT", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color(0xFF4ADE80) else Color(0xFF16A34A))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Unified Content Card with The Claim & Evidence
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkMode) Color(0xFF111827) else Color.White,
            shadowElevation = if (isDarkMode) 0.dp else 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFF1F5F9))
        ) {
            Column {// Details below scenic banner: LIFEOS INTERPRETATION
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEEF2FF))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "LIFEOS INTERPRETATION",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA),
                                letterSpacing = 0.5.sp
                            )
                        }

                        Text(
                            text = result.claim.domainCategory.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "\"${result.claim.rawText}\"",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                        lineHeight = 22.sp
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
                                    .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEFF6FF))
                                    .border(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFBFDBFE), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.5.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFF93C5FD) else Color(0xFF2563EB)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Deterministic Assessment",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = result.interpretation,
                        fontSize = 12.sp,
                        color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                        lineHeight = 17.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Scoring Rationale",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = result.confidence.rationale,
                        fontSize = 11.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        lineHeight = 16.sp
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
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color(0xFF60A5FA) else Color(0xFF2563EB)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                    .padding(horizontal = 7.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = "GROUND TRUTH SOURCES",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            result.analyzedEvidence.forEachIndexed { index, analyzed ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDarkMode) Color(0xFF111827) else Color.White,
                    shadowElevation = if (isDarkMode) 0.dp else 1.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Source 0${index + 1}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFF60A5FA) else Color(0xFF2563EB)
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                                    .padding(horizontal = 8.dp, vertical = 2.5.dp)
                            ) {
                                Text(
                                    text = analyzed.evidence.source.quality.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = analyzed.evidence.source.name,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B)
                        )

                        if (analyzed.evidence.source.authorityRationale.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Institutional Criteria: ${analyzed.evidence.source.authorityRationale}",
                                fontSize = 10.5.sp,
                                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "\"${analyzed.evidence.snippet}\"",
                            fontSize = 12.sp,
                            color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF334155),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "URL: ${analyzed.evidence.source.url ?: "Verified Institutional Database"}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkMode) Color(0xFF60A5FA) else Color(0xFF2563EB)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}
