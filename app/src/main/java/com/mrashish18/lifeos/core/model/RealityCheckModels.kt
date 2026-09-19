package com.mrashish18.lifeos.core.model

import java.time.Instant

/**
 * Raw input provided by the user for investigation.
 */
data class RealityCheckInput(
    val text: String,
    val sourceUrl: String? = null,
    val submittedAt: Instant = Instant.now()
)

/**
 * Deterministic domain classification of a claim.
 */
enum class DomainCategory(val label: String) {
    MEDICINE("Medicine & Health"),
    ASTRONOMY("Astronomy & Space"),
    TECHNOLOGY("Technology & Computing"),
    NEUROSCIENCE("Neuroscience"),
    PHYSICS("Physics & Nature"),
    GENERAL("General Knowledge")
}

/**
 * Deterministic classification of a claim's semantic type.
 */
enum class ClaimType {
    FACTUAL,       // Objective verifiable assertion
    NUMERICAL,     // Involves specific quantities, percentages, or statistics
    TEMPORAL,      // Involves specific dates, years, eras, or chronologies
    CAUSAL,        // Involves cause-and-effect relationships ("causes", "leads to")
    OPINION,       // Subjective judgment, aesthetic preference, or value statement
    UNSUPPORTED    // Vague, unparseable, or nonsensical input
}

/**
 * Extracted, structured claim representation.
 */
data class Claim(
    val rawText: String,
    val claimType: ClaimType,
    val detectedKeywords: List<String> = emptyList(),
    val extractedSubject: String = "",
    val normalizedText: String = rawText.trim(),
    val domainCategory: DomainCategory = DomainCategory.GENERAL
)

/**
 * Quality and credibility category for evidence sources.
 *
 * NOTE: Source category reflects historical reliability and institutional editorial standards.
 * It does NOT guarantee that any single claim from that source is infallible truth.
 */
enum class SourceQuality(val weight: Double, val label: String) {
    PRIMARY(1.0, "Primary / Academic"),
    OFFICIAL(0.9, "Official / Government"),
    REPUTABLE_NEWS(0.75, "Reputable News Organization"),
    REFERENCE(0.7, "Established Reference"),
    UNKNOWN(0.4, "Unverified Source")
}

/**
 * Metadata describing an evidence publisher or origin.
 */
data class EvidenceSource(
    val name: String,
    val url: String,
    val quality: SourceQuality,
    val description: String = "",
    val authorityRationale: String = quality.label
)

/**
 * Stance of a piece of evidence with respect to the analyzed claim.
 */
enum class EvidenceStance {
    SUPPORTS,     // Evidence corroborates the claim
    CONTRADICTS,  // Evidence refutes, disproves, or opposes the claim
    MENTIONS      // Evidence discusses the topic without a definitive stance
}

/**
 * Individual item of evidence retrieved from an evidence repository.
 *
 * CRITICAL PRINCIPLE:
 * Evidence represents external data as retrieved, completely separate from system interpretation.
 */
data class Evidence(
    val id: String,
    val title: String,
    val snippet: String,
    val source: EvidenceSource,
    val publicationDate: String? = null,
    val retrievedAtEpochMillis: Long = System.currentTimeMillis()
)

/**
 * System interpretation of an individual evidence item.
 *
 * Explicitly separates what the source stated from how the system evaluated it.
 */
data class AnalyzedEvidence(
    val evidence: Evidence,
    val stance: EvidenceStance,
    val relevanceScore: Double,      // 0.0 to 1.0 relevance to claim
    val weightContribution: Double,  // Calculated from sourceQuality.weight * relevanceScore
    val analysisNotes: String
)

/**
 * Verdict representing the relationship between the retrieved evidence and the claim.
 *
 * IMPORTANT: Verdicts are NOT absolute metaphysical truth claims.
 * They describe the objective evidentiary balance currently available to the system.
 */
enum class Verdict(val label: String, val summary: String) {
    SUPPORTED(
        "Supported",
        "The available evidence predominantly corroborates the claim."
    ),
    CONTRADICTED(
        "Contradicted",
        "The available evidence predominantly refutes or disproves the claim."
    ),
    MIXED(
        "Mixed Evidence",
        "The available evidence contains significant conflicting signals or caveats."
    ),
    INSUFFICIENT_EVIDENCE(
        "Insufficient Evidence",
        "Available reliable sources do not provide enough conclusive evidence to evaluate this claim."
    )
}

/**
 * Calibrated confidence score.
 *
 * Always clamped: never 1.0 (100%), because empirical evidence never guarantees absolute certainty.
 */
data class Confidence(
    val score: Double,             // 0.0 to 0.95
    val rationale: String,
    val corroboratedSourcesCount: Int,
    val conflictingSourcesCount: Int
) {
    init {
        require(score in 0.0..1.0) { "Confidence score must be between 0.0 and 1.0, was $score" }
    }

    val percentage: Int get() = (score * 100).toInt()
}

/**
 * Complete, human-readable investigation result produced by RealityCheck.
 */
data class RealityCheckResult(
    val id: String,
    val input: RealityCheckInput,
    val claim: Claim,
    val verdict: Verdict,
    val confidence: Confidence,
    val analyzedEvidence: List<AnalyzedEvidence>,
    val reasoning: String,
    val interpretation: String = reasoning,
    val disclaimer: String = "Confidence reflects the available evidence and does not guarantee that the claim is true.",
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)

/**
 * Persistent historical record of a completed RealityCheck investigation.
 */
data class InvestigationRecord(
    val id: String,
    val originalClaim: String,
    val normalizedClaim: String,
    val claimType: ClaimType,
    val domainCategory: DomainCategory,
    val verdict: Verdict,
    val confidenceScore: Double,
    val confidencePercentage: Int,
    val reasoning: String,
    val evidenceCount: Int,
    val topSourceNames: List<String>,
    val timestampEpochMillis: Long
) {
    val claimText: String get() = originalClaim
    val sourcesCount: Int get() = evidenceCount
}

fun RealityCheckResult.toInvestigationRecord(): InvestigationRecord = InvestigationRecord(
    id = id,
    originalClaim = input.text.trim(),
    normalizedClaim = claim.normalizedText,
    claimType = claim.claimType,
    domainCategory = claim.domainCategory,
    verdict = verdict,
    confidenceScore = confidence.score,
    confidencePercentage = confidence.percentage,
    reasoning = reasoning,
    evidenceCount = analyzedEvidence.size,
    topSourceNames = analyzedEvidence.map { it.evidence.source.name }.distinct().take(3),
    timestampEpochMillis = createdAtEpochMillis
)
