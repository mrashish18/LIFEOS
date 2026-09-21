package com.mrashish18.lifeos.domain.usecase

import com.mrashish18.lifeos.core.model.BehaviorEvent
import com.mrashish18.lifeos.core.model.BehaviorEventType
import com.mrashish18.lifeos.core.model.RealityCheckInput
import com.mrashish18.lifeos.core.model.RealityCheckResult
import com.mrashish18.lifeos.core.realitycheck.RealityCheckEngine
import com.mrashish18.lifeos.core.model.toInvestigationRecord
import com.mrashish18.lifeos.domain.repository.BehaviorEventRepository
import com.mrashish18.lifeos.domain.repository.InvestigationRepository
import java.util.UUID

private val CONTROL_CHAR_REGEX = Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]")
private const val MAX_CLAIM_LENGTH = 500
private const val MAX_URL_LENGTH = 2048
private val SAFE_URL_SCHEMES = setOf("http", "https")

/**
 * Domain usecase executing the full RealityCheck pipeline and recording
 * learning loop behavior events and persistent investigation history.
 */
class PerformRealityCheckUseCase(
    private val realityCheckEngine: RealityCheckEngine,
    private val behaviorEventRepository: BehaviorEventRepository,
    private val investigationRepository: InvestigationRepository? = null
) {

    suspend operator fun invoke(input: RealityCheckInput): Result<RealityCheckResult> {
        val sanitizedClaim = input.text.replace(CONTROL_CHAR_REGEX, "").trim()
        if (sanitizedClaim.isBlank()) {
            return Result.failure(IllegalArgumentException("Claim input cannot be blank"))
        }
        if (sanitizedClaim.length > MAX_CLAIM_LENGTH) {
            return Result.failure(IllegalArgumentException("Claim text exceeds maximum allowable length of $MAX_CLAIM_LENGTH characters"))
        }

        val sanitizedUrl = input.sourceUrl?.trim()?.ifBlank { null }
        if (sanitizedUrl != null) {
            if (sanitizedUrl.length > MAX_URL_LENGTH) {
                return Result.failure(IllegalArgumentException("Source URL exceeds maximum allowable length ($MAX_URL_LENGTH characters)"))
            }
            if (sanitizedUrl.any { it.isISOControl() || it.isWhitespace() }) {
                return Result.failure(IllegalArgumentException("Source URL contains invalid whitespace or control characters"))
            }
            val scheme = sanitizedUrl.substringBefore("://", "").lowercase()
            if (scheme !in SAFE_URL_SCHEMES) {
                return Result.failure(IllegalArgumentException("Source URL must use http:// or https://"))
            }
        }

        val validatedInput = RealityCheckInput(
            text = sanitizedClaim,
            sourceUrl = sanitizedUrl
        )

        // 1. Log submission event into BehaviorEvent system
        runCatching {
            behaviorEventRepository.recordEvent(
                BehaviorEvent(
                    id = UUID.randomUUID().toString(),
                    type = BehaviorEventType.CLAIM_SUBMITTED,
                    metadata = mapOf(
                        "inputLength" to sanitizedClaim.length.toString(),
                        "hasUrl" to (sanitizedUrl != null).toString()
                    )
                )
            )
        }

        // 2. Perform analysis via engine
        return runCatching {
            val result = realityCheckEngine.analyze(validatedInput)

            // 3. Log completion event into BehaviorEvent system
            behaviorEventRepository.recordEvent(
                BehaviorEvent(
                    id = UUID.randomUUID().toString(),
                    type = BehaviorEventType.CLAIM_VERIFIED,
                    metadata = mapOf(
                        "resultId" to result.id,
                        "verdict" to result.verdict.name,
                        "confidence" to result.confidence.percentage.toString(),
                        "evidenceCount" to result.analyzedEvidence.size.toString(),
                        "claimType" to result.claim.claimType.name
                    )
                )
            )

            // 4. Persist investigation record
            investigationRepository?.saveInvestigation(result.toInvestigationRecord())

            result
        }
    }
}
