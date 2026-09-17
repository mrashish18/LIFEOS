package com.mrashish18.lifeos.core.realitycheck

import com.mrashish18.lifeos.core.model.ClaimType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ClaimClassifierTest {

    private lateinit var classifier: ClaimClassifier

    @Before
    fun setUp() {
        classifier = ClaimClassifier()
    }

    @Test
    fun `classify returns UNSUPPORTED for blank or too short inputs`() {
        assertEquals(ClaimType.UNSUPPORTED, classifier.classify("").claimType)
        assertEquals(ClaimType.UNSUPPORTED, classifier.classify("   ").claimType)
        assertEquals(ClaimType.UNSUPPORTED, classifier.classify("abc").claimType)
        assertEquals(ClaimType.UNSUPPORTED, classifier.classify("123").claimType)
        assertEquals(ClaimType.UNSUPPORTED, classifier.classify("---").claimType)
    }

    @Test
    fun `classify returns OPINION for subjective or aesthetic claims`() {
        val claim1 = classifier.classify("Kotlin is the best programming language")
        assertEquals(ClaimType.OPINION, claim1.claimType)

        val claim2 = classifier.classify("In my opinion the new design looks ugly")
        assertEquals(ClaimType.OPINION, claim2.claimType)

        val claim3 = classifier.classify("This software is overrated compared to alternatives")
        assertEquals(ClaimType.OPINION, claim3.claimType)
    }

    @Test
    fun `classify returns CAUSAL for cause and effect propositions`() {
        val claim1 = classifier.classify("Smoking causes lung disease")
        assertEquals(ClaimType.CAUSAL, claim1.claimType)

        val claim2 = classifier.classify("High inflation leads to decreased purchasing power")
        assertEquals(ClaimType.CAUSAL, claim2.claimType)

        val claim3 = classifier.classify("Excessive screen time triggers sleep disturbances")
        assertEquals(ClaimType.CAUSAL, claim3.claimType)
    }

    @Test
    fun `classify returns NUMERICAL for claims containing explicit quantities or percentages`() {
        val claim1 = classifier.classify("Humans only use 10 percent of their brain")
        assertEquals(ClaimType.NUMERICAL, claim1.claimType)

        val claim2 = classifier.classify("Global temperatures rose by 1.5 degrees")
        assertEquals(ClaimType.NUMERICAL, claim2.claimType)

        val claim3 = classifier.classify("Over 50% of surveyed developers use Kotlin")
        assertEquals(ClaimType.NUMERICAL, claim3.claimType)
    }

    @Test
    fun `classify returns TEMPORAL for claims with calendar years and dates`() {
        val claim1 = classifier.classify("Apollo 11 landed on the Moon in 1969")
        assertEquals(ClaimType.TEMPORAL, claim1.claimType)

        val claim2 = classifier.classify("The printing press was invented in the 15th century")
        assertEquals(ClaimType.TEMPORAL, claim2.claimType)
    }

    @Test
    fun `classify returns FACTUAL for objective declarative statements`() {
        val claim1 = classifier.classify("Earth orbits the Sun")
        assertEquals(ClaimType.FACTUAL, claim1.claimType)

        val claim2 = classifier.classify("Water is composed of hydrogen and oxygen")
        assertEquals(ClaimType.FACTUAL, claim2.claimType)
    }

    @Test
    fun `classify extracts meaningful keywords and filters stopwords`() {
        val claim = classifier.classify("Earth orbits the Sun at high velocity")
        assertTrue(claim.detectedKeywords.contains("earth"))
        assertTrue(claim.detectedKeywords.contains("orbits"))
        assertTrue(claim.detectedKeywords.contains("sun"))
        assertTrue(claim.detectedKeywords.contains("velocity"))
        // Check stopwords were excluded
        assertTrue(!claim.detectedKeywords.contains("the"))
        assertTrue(!claim.detectedKeywords.contains("at"))
    }
}
