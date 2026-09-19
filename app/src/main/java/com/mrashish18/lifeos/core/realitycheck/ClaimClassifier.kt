package com.mrashish18.lifeos.core.realitycheck

import com.mrashish18.lifeos.core.model.Claim
import com.mrashish18.lifeos.core.model.ClaimType
import com.mrashish18.lifeos.core.model.DomainCategory

/**
 * Deterministic classifier that analyzes raw input strings to identify
 * the claim's semantic type, domain category, key terms, and normalized assertion.
 *
 * Adheres strictly to the principle:
 * Normalization standardizes inquiry formats without fabricating or altering semantic intent.
 */
class ClaimClassifier {

    fun normalize(rawInput: String): String {
        var text = rawInput.trim().replace(Regex("\\s+"), " ")
        if (text.isEmpty()) return ""

        // Strip trailing punctuation
        text = text.trimEnd('?', '!', '.', ',', ';', ':').trim()

        // Normalize common interrogative query frames into canonical assertions
        val lower = text.lowercase()
        val normalized = when {
            lower.startsWith("do ") -> text.substring(3).trim()
            lower.startsWith("does ") -> text.substring(5).trim()
            lower.startsWith("did ") -> text.substring(4).trim()
            lower.startsWith("can ") -> text.substring(4).trim()
            lower.startsWith("could ") -> text.substring(6).trim()
            lower.startsWith("will ") -> text.substring(5).trim()
            lower.startsWith("is ") && (lower.endsWith(" necessary") || lower.endsWith(" safe") || lower.endsWith(" true")) -> {
                val lastWord = text.substringAfterLast(" ")
                val middle = text.substring(3, text.length - lastWord.length).trim()
                "$middle is $lastWord"
            }
            lower.startsWith("are ") -> text.substring(4).trim()
            else -> text
        }

        return if (normalized.isNotEmpty()) {
            normalized.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } else {
            text
        }
    }

    fun classify(rawInput: String): Claim {
        val trimmed = rawInput.trim()
        if (trimmed.length < 4 || !trimmed.any { it.isLetter() }) {
            return Claim(
                rawText = trimmed,
                normalizedText = trimmed,
                claimType = ClaimType.UNSUPPORTED,
                domainCategory = DomainCategory.GENERAL,
                detectedKeywords = emptyList(),
                extractedSubject = ""
            )
        }

        val normalized = normalize(trimmed)
        val lower = normalized.lowercase()

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

        // Classify domain category
        val domainCategory = classifyDomain(keywords, lower)

        // Extract presumptive subject (first 1-3 content words)
        val subject = keywords.take(3).joinToString(" ")

        return Claim(
            rawText = trimmed,
            normalizedText = normalized,
            claimType = claimType,
            domainCategory = domainCategory,
            detectedKeywords = keywords,
            extractedSubject = subject
        )
    }

    private fun classifyDomain(keywords: List<String>, text: String): DomainCategory {
        return when {
            MEDICINE_KEYWORDS.any { text.contains(it) } -> DomainCategory.MEDICINE
            ASTRONOMY_KEYWORDS.any { text.contains(it) } -> DomainCategory.ASTRONOMY
            NEUROSCIENCE_KEYWORDS.any { text.contains(it) } -> DomainCategory.NEUROSCIENCE
            PHYSICS_KEYWORDS.any { text.contains(it) } -> DomainCategory.PHYSICS
            TECHNOLOGY_KEYWORDS.any { text.contains(it) } -> DomainCategory.TECHNOLOGY
            else -> DomainCategory.GENERAL
        }
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

        private val MEDICINE_KEYWORDS = listOf(
            "antibiotic", "virus", "viral", "infection", "vaccine", "autism", "mmr",
            "cardiovascular", "caffeine", "coffee", "bacterial", "bacteria", "health",
            "disease", "medical", "water", "hydration", "cancer", "smoking", "tobacco", "lung"
        )

        private val ASTRONOMY_KEYWORDS = listOf(
            "orbit", "orbits", "sun", "earth", "moon", "apollo", "planetary",
            "heliocentric", "solar", "space", "astronomy", "celestial", "mars"
        )

        private val NEUROSCIENCE_KEYWORDS = listOf(
            "brain", "ten percent", "10 percent", "10%", "neuron", "cognitive",
            "fmri", "neuroimaging", "neural", "neuromyth"
        )

        private val PHYSICS_KEYWORDS = listOf(
            "speed of light", "vacuum", "meters per second", "physical constant",
            "physics", "gravity", "quantum", "constant"
        )

        private val TECHNOLOGY_KEYWORDS = listOf(
            "kotlin", "jetbrains", "android", "programming", "language", "compiler",
            "5g", "software", "code", "jvm", "computer"
        )

        private val STOPWORDS = setOf(
            "the", "and", "that", "this", "with", "from", "for", "are", "was",
            "were", "will", "have", "has", "had", "does", "did", "can", "could",
            "about", "into", "over", "after", "then", "them", "they", "what", "which",
            "how", "why", "where", "when", "who"
        )
    }
}
