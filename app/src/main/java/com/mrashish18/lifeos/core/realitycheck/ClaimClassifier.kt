package com.mrashish18.lifeos.core.realitycheck

import com.mrashish18.lifeos.core.model.Claim
import com.mrashish18.lifeos.core.model.ClaimType

/**
 * Deterministic classifier that analyzes raw input strings to identify
 * the claim's semantic type, key terms, and subject matter.
 *
 * NOTE: Designed as a clear architectural boundary for future hybrid AI / LLM enhancement,
 * while maintaining 100% deterministic, reproducible output for the core system.
 */
class ClaimClassifier {

    fun classify(rawInput: String): Claim {
        val trimmed = rawInput.trim()
        if (trimmed.length < 4 || !trimmed.any { it.isLetter() }) {
            return Claim(
                rawText = trimmed,
                claimType = ClaimType.UNSUPPORTED,
                detectedKeywords = emptyList(),
                extractedSubject = ""
            )
        }

        val lower = trimmed.lowercase()

        // 1. Detect OPINION (subjective values, aesthetic or preference markers)
        val isOpinion = OPINION_MARKERS.any { lower.contains(it) }

        // 2. Detect CAUSAL (explicit cause-and-effect relationship)
        val isCausal = CAUSAL_MARKERS.any { lower.contains(it) }

        // 3. Detect TEMPORAL (explicit chronological references or calendar years)
        val isTemporal = YEAR_REGEX.containsMatchIn(lower) || TEMPORAL_MARKERS.any { lower.contains(it) }

        // 4. Detect NUMERICAL (quantities, percentages, measurement statistics)
        val isNumerical = NUMERICAL_REGEX.containsMatchIn(lower) || PERCENT_REGEX.containsMatchIn(lower)

        // Select primary claim type by specificity order: Opinion > Causal > Temporal > Numerical > Factual
        val claimType = when {
            isOpinion -> ClaimType.OPINION
            isCausal -> ClaimType.CAUSAL
            isTemporal -> ClaimType.TEMPORAL
            isNumerical -> ClaimType.NUMERICAL
            else -> ClaimType.FACTUAL
        }

        // Extract meaningful content tokens (keywords)
        val keywords = lower.split(Regex("[\\s,;:.?!'\"()/\\[\\]]+"))
            .filter { it.length > 2 && it !in STOPWORDS }
            .distinct()

        // Extract presumptive subject (first 1-3 content words)
        val subject = keywords.take(3).joinToString(" ")

        return Claim(
            rawText = trimmed,
            claimType = claimType,
            detectedKeywords = keywords,
            extractedSubject = subject
        )
    }

    companion object {
        private val YEAR_REGEX = Regex("\\b(1[6-9]\\d{2}|20\\d{2})\\b")
        private val NUMERICAL_REGEX = Regex("\\b\\d+(\\.\\d+)?\\s*(million|billion|thousand|trillion|km|miles|meters|percent|kg|hours|days|years|degrees)?\\b")
        private val PERCENT_REGEX = Regex("\\b\\d+([.,]\\d+)?%")

        private val OPINION_MARKERS = listOf(
            "in my opinion", "i believe", "i think", "i feel", "best", "worst",
            "better than", "worse than", "should be", "ought to", "overrated",
            "underrated", "ugly", "beautiful", "superior", "inferior", "favorite"
        )

        private val CAUSAL_MARKERS = listOf(
            "causes", "caused by", "causing", "leads to", "leading to",
            "results in", "resulting in", "because of", "due to", "triggers",
            "triggered by"
        )

        private val TEMPORAL_MARKERS = listOf(
            "century", "decade", "in the year", "yesterday", "tomorrow",
            "ancient", "modern day", "during the", "before christ", "after christ"
        )

        private val STOPWORDS = setOf(
            "the", "and", "that", "this", "with", "from", "for", "are", "was",
            "were", "will", "have", "has", "had", "does", "did", "can", "could",
            "about", "into", "over", "after", "then", "them", "they", "what", "which"
        )
    }
}
