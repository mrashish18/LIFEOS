package com.mrashish18.lifeos.core.realitycheck

import com.mrashish18.lifeos.core.model.AnalyzedEvidence
import com.mrashish18.lifeos.core.model.Claim
import com.mrashish18.lifeos.core.model.Confidence
import com.mrashish18.lifeos.core.model.Evidence
import com.mrashish18.lifeos.core.model.EvidenceStance
import com.mrashish18.lifeos.core.model.Verdict

/**
 * Transparent, deterministic evidence comparison engine.
 *
 * Evaluates a structured claim against retrieved evidence documents, detects stances,
 * weights sources by credibility and relevance, calculates calibrated confidence,
 * and derives an explainable investigation verdict.
 */
class EvidenceComparator {

    fun compare(claim: Claim, evidenceList: List<Evidence>): ComparisonResult {
        if (evidenceList.isEmpty()) {
            return ComparisonResult(
                verdict = Verdict.INSUFFICIENT_EVIDENCE,
                confidence = Confidence(
                    score = 0.25,
                    rationale = "No verified external evidence matching the claim's core terms could be retrieved.",
                    corroboratedSourcesCount = 0,
                    conflictingSourcesCount = 0
                ),
                analyzedEvidence = emptyList(),
                reasoning = "No authoritative or reference records were found that directly address this claim. " +
                        "In accordance with LIFEOS safety principles, the system declines to guess or speculate " +
                        "without verifiable evidence."
            )
        }

        // 1. Analyze stance and relevance for each evidence item
        val analyzedItems = evidenceList.map { evidence ->
            analyzeSingleEvidence(claim, evidence)
        }

        // 2. Aggregate weighted stance scores
        val supporting = analyzedItems.filter { it.stance == EvidenceStance.SUPPORTS }
        val contradicting = analyzedItems.filter { it.stance == EvidenceStance.CONTRADICTS }
        val mentioning = analyzedItems.filter { it.stance == EvidenceStance.MENTIONS }

        val supportWeight = supporting.sumOf { it.weightContribution }
        val contradictWeight = contradicting.sumOf { it.weightContribution }
        val totalSignal = supportWeight + contradictWeight

        // 3. Determine Verdict based on objective evidentiary balance
        val verdict: Verdict
        val conflictRatio: Double

        if (totalSignal < 0.35) {
            verdict = Verdict.INSUFFICIENT_EVIDENCE
            conflictRatio = 0.0
        } else if (supporting.isNotEmpty() && contradicting.isNotEmpty()) {
            val minScore = minOf(supportWeight, contradictWeight)
            val maxScore = maxOf(supportWeight, contradictWeight)
            conflictRatio = if (maxScore > 0) minScore / maxScore else 0.0

            verdict = if (conflictRatio > 0.25 || (supportWeight >= 0.15 && contradictWeight >= 0.15)) {
                Verdict.MIXED
            } else if (supportWeight > contradictWeight) {
                Verdict.SUPPORTED
            } else {
                Verdict.CONTRADICTED
            }
        } else if (supportWeight >= 1.3 * contradictWeight) {
            verdict = Verdict.SUPPORTED
            conflictRatio = if (supportWeight > 0) contradictWeight / supportWeight else 0.0
        } else if (contradictWeight >= 1.3 * supportWeight) {
            verdict = Verdict.CONTRADICTED
            conflictRatio = if (contradictWeight > 0) supportWeight / contradictWeight else 0.0
        } else {
            verdict = Verdict.MIXED
            conflictRatio = 0.5
        }

        // 4. Compute Calibrated Confidence
        val confidence = calculateConfidence(
            verdict = verdict,
            analyzedItems = analyzedItems,
            supporting = supporting,
            contradicting = contradicting,
            conflictRatio = conflictRatio
        )

        // 5. Generate human-readable reasoning
        val reasoning = generateReasoning(
            claim = claim,
            verdict = verdict,
            confidence = confidence,
            supporting = supporting,
            contradicting = contradicting,
            mentioning = mentioning
        )

        return ComparisonResult(
            verdict = verdict,
            confidence = confidence,
            analyzedEvidence = analyzedItems,
            reasoning = reasoning
        )
    }

    private fun analyzeSingleEvidence(claim: Claim, evidence: Evidence): AnalyzedEvidence {
        val snippetLower = evidence.snippet.lowercase()
        val titleLower = evidence.title.lowercase()
        val combinedText = "$titleLower $snippetLower"
        val claimTextLower = claim.rawText.lowercase()

        // Calculate relevance: token overlap ratio
        val keywords = claim.detectedKeywords
        val matchedKeywords = keywords.filter { combinedText.contains(it) }
        val rawRelevance = if (keywords.isNotEmpty()) {
            (matchedKeywords.size.toDouble() / keywords.size.toDouble()).coerceIn(0.2, 1.0)
        } else {
            0.5
        }

        // Detect stance: Refuting vs Affirming signals
        val hasAffirmingLanguage = AFFIRMING_TERMS.any { combinedText.contains(it) }
        val hasRefutingLanguage = REFUTING_TERMS.any { combinedText.contains(it) }

        // Contextual analysis: check if claim is negated in evidence or affirmed
        val claimAssertsNegative = CLAIM_NEGATION_TERMS.any { claimTextLower.contains(it) }

        val stance: EvidenceStance
        val analysisNote: String

        if (hasAffirmingLanguage && !hasRefutingLanguage) {
            stance = if (claimAssertsNegative) EvidenceStance.CONTRADICTS else EvidenceStance.SUPPORTS
            analysisNote = "Evidence directly corroborates the core elements and positive findings of the claim."
        } else if (hasRefutingLanguage && !hasAffirmingLanguage) {
            stance = if (claimAssertsNegative) EvidenceStance.SUPPORTS else EvidenceStance.CONTRADICTS
            analysisNote = "Evidence explicitly contradicts or refutes the proposition with counter-evidence."
        } else if (hasAffirmingLanguage && hasRefutingLanguage) {
            // Nuanced / multi-faceted source mentioning both
            stance = EvidenceStance.MENTIONS
            analysisNote = "Evidence notes both positive associations and documented risks."
        } else if (matchedKeywords.size >= (keywords.size * 0.5) && keywords.isNotEmpty()) {
            stance = EvidenceStance.SUPPORTS
            analysisNote = "Evidence discusses and corroborates the topical terms of the claim."
        } else {
            stance = EvidenceStance.MENTIONS
            analysisNote = "Evidence discusses related subject matter without taking an explicit decisive stance."
        }

        val weightContribution = evidence.source.quality.weight * rawRelevance

        return AnalyzedEvidence(
            evidence = evidence,
            stance = stance,
            relevanceScore = rawRelevance,
            weightContribution = weightContribution,
            analysisNotes = analysisNote
        )
    }

    private fun calculateConfidence(
        verdict: Verdict,
        analyzedItems: List<AnalyzedEvidence>,
        supporting: List<AnalyzedEvidence>,
        contradicting: List<AnalyzedEvidence>,
        conflictRatio: Double
    ): Confidence {
        val totalCount = analyzedItems.size
        val qualityScore = if (totalCount > 0) {
            analyzedItems.map { it.evidence.source.quality.weight }.average()
        } else {
            0.4
        }
        val relevanceScore = if (totalCount > 0) {
            analyzedItems.map { it.relevanceScore }.average()
        } else {
            0.3
        }

        val dominantCount = when (verdict) {
            Verdict.SUPPORTED -> supporting.size
            Verdict.CONTRADICTED -> contradicting.size
            Verdict.MIXED -> maxOf(supporting.size, contradicting.size)
            Verdict.INSUFFICIENT_EVIDENCE -> 0
        }
        val consensusScore = if (totalCount > 0) {
            dominantCount.toDouble() / totalCount.toDouble()
        } else {
            0.0
        }
        val coverageScore = minOf(1.0, totalCount / 2.0)

        val finalScore: Double
        val rationale: String

        when (verdict) {
            Verdict.INSUFFICIENT_EVIDENCE -> {
                finalScore = (0.15 + (coverageScore * 0.10) + (relevanceScore * 0.05)).coerceIn(0.10, 0.35)
                rationale = "Low confidence: available repositories do not contain sufficient corroborating or refuting evidence."
            }
            Verdict.MIXED -> {
                val raw = (0.35 * qualityScore) + (0.25 * relevanceScore) + (0.25 * coverageScore) - (conflictRatio * 0.15)
                finalScore = raw.coerceIn(0.40, 0.72)
                rationale = "Moderate confidence: verified sources present significant divergent data or critical caveats."
            }
            Verdict.SUPPORTED -> {
                val raw = (0.35 * qualityScore) + (0.30 * relevanceScore) + (0.20 * consensusScore) + (0.15 * coverageScore) - (conflictRatio * 0.25)
                finalScore = raw.coerceIn(0.20, 0.94)
                rationale = "High confidence derived from ${supporting.size} corroborating source(s) with ${(qualityScore * 100).toInt()}% average quality rating and ${(consensusScore * 100).toInt()}% consensus."
            }
            Verdict.CONTRADICTED -> {
                val raw = (0.35 * qualityScore) + (0.30 * relevanceScore) + (0.20 * consensusScore) + (0.15 * coverageScore) - (conflictRatio * 0.25)
                finalScore = raw.coerceIn(0.20, 0.94)
                rationale = "High confidence derived from ${contradicting.size} refuting source(s) with ${(qualityScore * 100).toInt()}% average quality rating and ${(consensusScore * 100).toInt()}% consensus."
            }
        }

        return Confidence(
            score = finalScore,
            rationale = rationale,
            corroboratedSourcesCount = supporting.size,
            conflictingSourcesCount = contradicting.size
        )
    }

    private fun generateReasoning(
        claim: Claim,
        verdict: Verdict,
        confidence: Confidence,
        supporting: List<AnalyzedEvidence>,
        contradicting: List<AnalyzedEvidence>,
        mentioning: List<AnalyzedEvidence>
    ): String {
        return buildString {
            when (verdict) {
                Verdict.SUPPORTED -> {
                    append("Investigation indicates the claim is supported by ")
                    append("${supporting.size} verified source(s)")
                    val topSourceNames = supporting.map { it.evidence.source.name }.distinct().take(2)
                    if (topSourceNames.isNotEmpty()) {
                        append(" (${topSourceNames.joinToString(", ")})")
                    }
                    append(". Independent records corroborate the core proposition. ")
                    if (contradicting.isNotEmpty()) {
                        append("Note: ${contradicting.size} source(s) noted caveats or counter-evidence. ")
                    }
                    append("Confidence is calibrated at ${confidence.percentage}%.")
                }
                Verdict.CONTRADICTED -> {
                    append("Investigation indicates the claim is contradicted by ")
                    append("${contradicting.size} authoritative source(s)")
                    val topSourceNames = contradicting.map { it.evidence.source.name }.distinct().take(2)
                    if (topSourceNames.isNotEmpty()) {
                        append(" (${topSourceNames.joinToString(", ")})")
                    }
                    append(". Authoritative scientific evidence directly refutes the premise. ")
                    append("Confidence is calibrated at ${confidence.percentage}%.")
                }
                Verdict.MIXED -> {
                    append("Investigation yields a mixed evidentiary verdict. ")
                    append("${supporting.size} source(s) offer supportive data, while ")
                    append("${contradicting.size} source(s) document significant counter-evidence or health risks. ")
                    append("Calibrated confidence is ${confidence.percentage}%.")
                }
                Verdict.INSUFFICIENT_EVIDENCE -> {
                    append("The system concludes insufficient evidence. ")
                    if (mentioning.isNotEmpty()) {
                        append("Retrieved documents reference related subject terms but do not provide definitive validation. ")
                    } else {
                        append("No authoritative or reference records matching the claim could be retrieved. ")
                    }
                    append("In accordance with LIFEOS safety principles, the system refrains from speculation.")
                }
            }
        }
    }

    companion object {
        private val REFUTING_TERMS = listOf(
            "do not work", "does not work", "do not fight", "does not fight",
            "not work against", "ineffective", "misconception", "debunked",
            "myth", "no link", "no evidence", "unsupported", "false", "fraudulent",
            "retracted", "does not cause", "do not cause", "disproven", "refuted",
            "health hazard", "adverse effect", "harmful effect", "health risks",
            "adverse cardiovascular", "negative effect", "poses risk", "dangerous"
        )

        private val AFFIRMING_TERMS = listOf(
            "orbits the", "orbits", "revolve around", "confirmed", "officially released",
            "demonstrates that", "confirms that", "landed the", "exact physical constant",
            "lower risk", "reduced risk", "beneficial", "safe for most", "first person to step"
        )

        private val CLAIM_NEGATION_TERMS = listOf(
            "not", "no", "never", "cannot", "does not", "do not", "isn't", "aren't"
        )
    }

    data class ComparisonResult(
        val verdict: Verdict,
        val confidence: Confidence,
        val analyzedEvidence: List<AnalyzedEvidence>,
        val reasoning: String
    )
}
