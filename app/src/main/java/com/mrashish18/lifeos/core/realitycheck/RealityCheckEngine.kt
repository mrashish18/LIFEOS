package com.mrashish18.lifeos.core.realitycheck

import com.mrashish18.lifeos.core.model.Confidence
import com.mrashish18.lifeos.core.model.RealityCheckInput
import com.mrashish18.lifeos.core.model.RealityCheckResult
import com.mrashish18.lifeos.core.model.Verdict
import com.mrashish18.lifeos.domain.repository.EvidenceRepository
import java.util.UUID

/**
 * Primary engine for Trust Intelligence (RealityCheck).
 *
 * Orchestrates the full pipeline:
 * Input ──► Claim Extraction ──► Source Classification ──► Evidence Retrieval ──► Evidence Comparison ──► Investigation Report
 */
class RealityCheckEngine(
    private val evidenceRepository: EvidenceRepository,
    private val claimClassifier: ClaimClassifier = ClaimClassifier(),
    private val sourceClassifier: SourceClassifier = SourceClassifier(),
    private val evidenceComparator: EvidenceComparator = EvidenceComparator()
) {

    suspend fun analyze(input: RealityCheckInput): RealityCheckResult {
        // 1. Claim extraction and classification
        val claim = claimClassifier.classify(input.text)

        // If input is unsupported/empty, return early with insufficient evidence
        if (claim.claimType == com.mrashish18.lifeos.core.model.ClaimType.UNSUPPORTED) {
            return RealityCheckResult(
                id = UUID.randomUUID().toString(),
                input = input,
                claim = claim,
                verdict = Verdict.INSUFFICIENT_EVIDENCE,
                confidence = Confidence(
                    score = 0.15,
                    rationale = "The input provided is too brief or lacks clear semantic propositions to evaluate.",
                    corroboratedSourcesCount = 0,
                    conflictingSourcesCount = 0
                ),
                analyzedEvidence = emptyList(),
                reasoning = "Input could not be parsed into a verifiable factual or causal proposition."
            )
        }

        // 2. Retrieve evidence from repository using claim terms
        val searchQuery = if (claim.detectedKeywords.isNotEmpty()) {
            claim.detectedKeywords.joinToString(" ")
        } else {
            claim.rawText
        }

        val evidenceResult = evidenceRepository.search(searchQuery)
        val rawEvidence = evidenceResult.getOrElse { emptyList() }

        // 3. Ensure source quality classification is grounded
        val calibratedEvidence = rawEvidence.map { item ->
            val detectedQuality = sourceClassifier.classify(item.source.name, item.source.url)
            if (item.source.quality != detectedQuality && detectedQuality != com.mrashish18.lifeos.core.model.SourceQuality.UNKNOWN) {
                item.copy(source = item.source.copy(quality = detectedQuality))
            } else {
                item
            }
        }

        // 4. Compare evidence against claim
        val comparison = evidenceComparator.compare(claim, calibratedEvidence)

        // 5. Construct investigation report
        return RealityCheckResult(
            id = UUID.randomUUID().toString(),
            input = input,
            claim = claim,
            verdict = comparison.verdict,
            confidence = comparison.confidence,
            analyzedEvidence = comparison.analyzedEvidence,
            reasoning = comparison.reasoning
        )
    }
}
