package com.mrashish18.lifeos.core.realitycheck

import com.mrashish18.lifeos.core.model.SourceQuality

/**
 * Deterministic classifier that evaluates the historical editorial quality
 * and category of an evidence source based on its name and origin URL.
 *
 * NOTE: Source category is a prior heuristic for weighting; it does not imply
 * that any source is infallible or immune to correction.
 */
class SourceClassifier {

    fun classify(sourceName: String, url: String): SourceQuality {
        val lowerUrl = url.lowercase().trim()
        val lowerName = sourceName.lowercase().trim()

        // 1. Check for OFFICIAL (Government, international public health bodies, national agencies)
        if (OFFICIAL_DOMAINS.any { lowerUrl.contains(it) } ||
            OFFICIAL_NAME_KEYWORDS.any { lowerName.contains(it) }) {
            return SourceQuality.OFFICIAL
        }

        // 2. Check for PRIMARY (Peer-reviewed journals, primary academic repositories, creator organizations)
        if (PRIMARY_DOMAINS.any { lowerUrl.contains(it) } ||
            PRIMARY_NAME_KEYWORDS.any { lowerName.contains(it) }) {
            return SourceQuality.PRIMARY
        }

        // 3. Check for REPUTABLE_NEWS (Established journalistic organizations with public editorial correction policies)
        if (NEWS_DOMAINS.any { lowerUrl.contains(it) } ||
            NEWS_NAME_KEYWORDS.any { lowerName.contains(it) }) {
            return SourceQuality.REPUTABLE_NEWS
        }

        // 4. Check for REFERENCE (Encyclopedias, verified clinical reference portals, established dictionaries)
        if (REFERENCE_DOMAINS.any { lowerUrl.contains(it) } ||
            REFERENCE_NAME_KEYWORDS.any { lowerName.contains(it) }) {
            return SourceQuality.REFERENCE
        }

        // 5. Default to UNKNOWN
        return SourceQuality.UNKNOWN
    }

    companion object {
        private val OFFICIAL_DOMAINS = listOf(
            ".gov", ".mil", "who.int", "nasa.gov", "cdc.gov", "nih.gov", "nist.gov",
            "europa.eu", "un.org"
        )

        private val OFFICIAL_NAME_KEYWORDS = listOf(
            "centers for disease control", "world health organization", "nasa",
            "national institute", "ministry of", "department of", "european commission",
            "united nations"
        )

        private val PRIMARY_DOMAINS = listOf(
            "thelancet.com", "nature.com", "sciencemag.org", "nejm.org", "cell.com",
            "ieee.org", "acm.org", "pnas.org", "pubmed.ncbi.nlm.nih.gov", "jetbrains.com",
            "brainfacts.org", "hsph.harvard.edu"
        )

        private val PRIMARY_NAME_KEYWORDS = listOf(
            "the lancet", "nature", "science", "new england journal", "proceedings of the national academy",
            "society for neuroscience", "jetbrains", "harvard t.h. chan", "peer-reviewed"
        )

        private val NEWS_DOMAINS = listOf(
            "reuters.com", "apnews.com", "bbc.com", "bbc.co.uk", "scientificamerican.com",
            "nytimes.com", "theguardian.com", "wsj.com", "bloomberg.com", "economist.com"
        )

        private val NEWS_NAME_KEYWORDS = listOf(
            "reuters", "associated press", "bbc", "scientific american",
            "new york times", "the guardian", "wall street journal", "the economist"
        )

        private val REFERENCE_DOMAINS = listOf(
            "britannica.com", "wikipedia.org", "mayoclinic.org", "merriam-webster.com",
            "plato.stanford.edu", "iep.utm.edu"
        )

        private val REFERENCE_NAME_KEYWORDS = listOf(
            "britannica", "encyclopaedia", "mayo clinic", "stanford encyclopedia",
            "dictionary", "reference"
        )
    }
}
