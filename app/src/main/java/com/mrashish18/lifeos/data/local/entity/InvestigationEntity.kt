package com.mrashish18.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrashish18.lifeos.core.model.ClaimType
import com.mrashish18.lifeos.core.model.DomainCategory
import com.mrashish18.lifeos.core.model.InvestigationRecord
import com.mrashish18.lifeos.core.model.Verdict

/**
 * Room database entity representing a completed RealityCheck investigation.
 */
@Entity(tableName = "investigation_records")
data class InvestigationEntity(
    @PrimaryKey val id: String,
    val originalClaim: String,
    val normalizedClaim: String,
    val claimType: String,
    val domainCategory: String,
    val verdict: String,
    val confidenceScore: Double,
    val confidencePercentage: Int,
    val reasoning: String,
    val evidenceCount: Int,
    val topSourceNamesJson: String,
    val timestampEpochMillis: Long
) {
    fun toDomain(): InvestigationRecord {
        val sources = if (topSourceNamesJson.isBlank()) {
            emptyList()
        } else {
            topSourceNamesJson.split(SOURCE_DELIMITER).filter { it.isNotBlank() }
        }

        val cType = try { ClaimType.valueOf(claimType) } catch (e: Exception) { ClaimType.FACTUAL }
        val dCategory = try { DomainCategory.valueOf(domainCategory) } catch (e: Exception) { DomainCategory.GENERAL }
        val vVerdict = try { Verdict.valueOf(verdict) } catch (e: Exception) { Verdict.INSUFFICIENT_EVIDENCE }

        return InvestigationRecord(
            id = id,
            originalClaim = originalClaim,
            normalizedClaim = normalizedClaim,
            claimType = cType,
            domainCategory = dCategory,
            verdict = vVerdict,
            confidenceScore = confidenceScore,
            confidencePercentage = confidencePercentage,
            reasoning = reasoning,
            evidenceCount = evidenceCount,
            topSourceNames = sources,
            timestampEpochMillis = timestampEpochMillis
        )
    }

    companion object {
        private const val SOURCE_DELIMITER = "\u001F"

        fun fromDomain(record: InvestigationRecord): InvestigationEntity {
            return InvestigationEntity(
                id = record.id,
                originalClaim = record.originalClaim,
                normalizedClaim = record.normalizedClaim,
                claimType = record.claimType.name,
                domainCategory = record.domainCategory.name,
                verdict = record.verdict.name,
                confidenceScore = record.confidenceScore,
                confidencePercentage = record.confidencePercentage,
                reasoning = record.reasoning,
                evidenceCount = record.evidenceCount,
                topSourceNamesJson = record.topSourceNames.joinToString(SOURCE_DELIMITER),
                timestampEpochMillis = record.timestampEpochMillis
            )
        }
    }
}
