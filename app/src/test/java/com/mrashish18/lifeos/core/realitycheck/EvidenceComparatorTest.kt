package com.mrashish18.lifeos.core.realitycheck

import com.mrashish18.lifeos.core.model.Claim
import com.mrashish18.lifeos.core.model.ClaimType
import com.mrashish18.lifeos.core.model.Evidence
import com.mrashish18.lifeos.core.model.EvidenceSource
import com.mrashish18.lifeos.core.model.SourceQuality
import com.mrashish18.lifeos.core.model.Verdict
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EvidenceComparatorTest {

    private lateinit var comparator: EvidenceComparator

    @Before
    fun setUp() {
        comparator = EvidenceComparator()
    }

    private fun sampleSource(quality: SourceQuality = SourceQuality.OFFICIAL): EvidenceSource {
        return EvidenceSource(
            name = "Test Authority",
            url = "https://example.gov/evidence",
            quality = quality
        )
    }

    @Test
    fun `empty evidence list produces INSUFFICIENT_EVIDENCE with low confidence`() {
        val claim = Claim("Water freezes at zero degrees", ClaimType.FACTUAL, listOf("water", "freezes", "zero", "degrees"))
        val result = comparator.compare(claim, emptyList())

        assertEquals(Verdict.INSUFFICIENT_EVIDENCE, result.verdict)
        assertTrue(result.confidence.score in 0.15..0.35)
        assertTrue(result.analyzedEvidence.isEmpty())
        assertTrue(result.reasoning.contains("No authoritative or reference records", ignoreCase = true))
    }

    @Test
    fun `corroborating evidence produces SUPPORTED with high confidence`() {
        val claim = Claim(
            rawText = "Earth orbits the Sun",
            claimType = ClaimType.FACTUAL,
            detectedKeywords = listOf("earth", "orbits", "sun")
        )

        val evidence = listOf(
            Evidence(
                id = "e1",
                title = "Planetary Motion",
                snippet = "Earth orbits the Sun at an average distance of 149.6 million kilometers.",
                source = sampleSource(SourceQuality.OFFICIAL)
            ),
            Evidence(
                id = "e2",
                title = "Heliocentric System",
                snippet = "The Earth and planets revolve around the Sun at the center of the Solar System.",
                source = sampleSource(SourceQuality.REFERENCE)
            )
        )

        val result = comparator.compare(claim, evidence)

        assertEquals(Verdict.SUPPORTED, result.verdict)
        assertTrue(result.confidence.score >= 0.70)
        assertTrue(result.confidence.score < 1.0)
        assertEquals(2, result.confidence.corroboratedSourcesCount)
        assertEquals(0, result.confidence.conflictingSourcesCount)
    }

    @Test
    fun `refuting evidence produces CONTRADICTED with high confidence`() {
        val claim = Claim(
            rawText = "Antibiotics kill viruses",
            claimType = ClaimType.FACTUAL,
            detectedKeywords = listOf("antibiotics", "kill", "viruses")
        )

        val evidence = listOf(
            Evidence(
                id = "e1",
                title = "Antibiotic Use",
                snippet = "Antibiotics only treat bacterial infections. Antibiotics do not work against viruses.",
                source = sampleSource(SourceQuality.OFFICIAL)
            ),
            Evidence(
                id = "e2",
                title = "Antimicrobial Resistance",
                snippet = "Taking antibiotics for viral illnesses is ineffective and does not fight viruses.",
                source = sampleSource(SourceQuality.OFFICIAL)
            )
        )

        val result = comparator.compare(claim, evidence)

        assertEquals(Verdict.CONTRADICTED, result.verdict)
        assertTrue(result.confidence.score >= 0.70)
        assertTrue(result.confidence.score < 1.0)
        assertEquals(2, result.confidence.conflictingSourcesCount)
    }

    @Test
    fun `conflicting evidence produces MIXED verdict`() {
        val claim = Claim(
            rawText = "Coffee is beneficial for human health",
            claimType = ClaimType.FACTUAL,
            detectedKeywords = listOf("coffee", "beneficial", "health")
        )

        val evidence = listOf(
            Evidence(
                id = "e1",
                title = "Coffee Benefits",
                snippet = "Moderate coffee consumption has been confirmed to be associated with a lower risk of cardiovascular disease.",
                source = sampleSource(SourceQuality.PRIMARY)
            ),
            Evidence(
                id = "e2",
                title = "Coffee and Caffeine Health Risks",
                snippet = "Excessive coffee and caffeine consumption produces adverse cardiovascular risks and harmful sleep disruption.",
                source = sampleSource(SourceQuality.REFERENCE)
            )
        )

        val result = comparator.compare(claim, evidence)

        assertEquals(Verdict.MIXED, result.verdict)
        assertTrue(result.confidence.score in 0.45..0.75)
    }

    @Test
    fun `higher quality sources contribute more to confidence than unknown sources`() {
        val claim = Claim("Mars has two moons", ClaimType.FACTUAL, listOf("mars", "two", "moons"))

        val primaryEvidence = listOf(
            Evidence("p1", "Moons of Mars", "Observations confirmed Mars orbits two moons: Phobos and Deimos.", sampleSource(SourceQuality.PRIMARY))
        )
        val unknownEvidence = listOf(
            Evidence("u1", "Moons of Mars", "Observations confirmed Mars orbits two moons: Phobos and Deimos.", sampleSource(SourceQuality.UNKNOWN))
        )

        val primaryResult = comparator.compare(claim, primaryEvidence)
        val unknownResult = comparator.compare(claim, unknownEvidence)

        assertTrue(
            "Primary source confidence (${primaryResult.confidence.score}) must exceed unknown source (${unknownResult.confidence.score})",
            primaryResult.confidence.score > unknownResult.confidence.score
        )
    }

    @Test
    fun `deterministic repeated execution guarantees identical outputs`() {
        val claim = Claim("Earth orbits the Sun", ClaimType.FACTUAL, listOf("earth", "orbits", "sun"))
        val evidence = listOf(
            Evidence("e1", "Earth Orbit", "Earth orbits the Sun at 149.6 million km.", sampleSource(SourceQuality.OFFICIAL))
        )

        val firstRun = comparator.compare(claim, evidence)

        repeat(20) {
            val nextRun = comparator.compare(claim, evidence)
            assertEquals(firstRun.verdict, nextRun.verdict)
            assertEquals(firstRun.confidence.score, nextRun.confidence.score, 0.0001)
            assertEquals(firstRun.reasoning, nextRun.reasoning)
            assertEquals(firstRun.analyzedEvidence.size, nextRun.analyzedEvidence.size)
        }
    }
}
