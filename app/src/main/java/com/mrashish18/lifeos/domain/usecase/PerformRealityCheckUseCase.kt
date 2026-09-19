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
        val trimmed = input.text.trim()
        if (trimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("Claim input cannot be blank"))
        }

        // 1. Log submission event into BehaviorEvent system
        runCatching {
            behaviorEventRepository.recordEvent(
                BehaviorEvent(
                    id = UUID.randomUUID().toString(),
                    type = BehaviorEventType.CLAIM_SUBMITTED,
                    metadata = mapOf(
                        "inputLength" to trimmed.length.toString(),
                        "hasUrl" to (input.sourceUrl != null).toString()
                    )
                )
            )
        }

        // 2. Perform analysis via engine
        return runCatching {
            val result = realityCheckEngine.analyze(input)

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
