package com.mrashish18.lifeos.domain.realitycheck

import com.mrashish18.lifeos.core.model.ClaimType
import com.mrashish18.lifeos.core.model.DomainCategory
import com.mrashish18.lifeos.core.realitycheck.ClaimClassifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    fun `normalizes interrogative prefix and trailing question mark`() {
        val input = "Antibiotics cure viral infections?"
        val normalized = classifier.normalize(input)
        assertEquals("Antibiotics cure viral infections", normalized)

        val input2 = "Is drinking 8 glasses of water necessary?"
        val normalized2 = classifier.normalize(input2)
        assertEquals("Drinking 8 glasses of water is necessary", normalized2)

        val input3 = "Do vaccines cause autism?"
        val normalized3 = classifier.normalize(input3)
        assertEquals("Vaccines cause autism", normalized3)

        val input4 = "Does regular exercise improve mental health?"
        val normalized4 = classifier.normalize(input4)
        assertEquals("Regular exercise improve mental health", normalized4)
    }

    @Test
    fun `classifies domain categories accurately based on vocabulary`() {
        assertEquals(DomainCategory.ASTRONOMY, classifier.classify("Earth orbits the Sun in 365 days").domainCategory)
        assertEquals(DomainCategory.ASTRONOMY, classifier.classify("Apollo 11 landed on the Moon in 1969").domainCategory)
        assertEquals(DomainCategory.MEDICINE, classifier.classify("Antibiotics kill bacteria not viruses").domainCategory)
        assertEquals(DomainCategory.MEDICINE, classifier.classify("Drinking 8 glasses of water is necessary for hydration").domainCategory)
        assertEquals(DomainCategory.NEUROSCIENCE, classifier.classify("Humans use 100 percent of their brain capacity").domainCategory)
        assertEquals(DomainCategory.TECHNOLOGY, classifier.classify("Kotlin 1.0 released in 2016 for Android development").domainCategory)
        assertEquals(DomainCategory.TECHNOLOGY, classifier.classify("5G wireless networks cause harmful biological radiation").domainCategory)
        assertEquals(DomainCategory.GENERAL, classifier.classify("All swans in Europe were believed to be white").domainCategory)
    }

    @Test
    fun `extracts claim type and keywords accurately`() {
        val claim = classifier.classify("Apollo 11 landed humans on the Moon in 1969")
        assertEquals(DomainCategory.ASTRONOMY, claim.domainCategory)
        assertEquals(ClaimType.TEMPORAL, claim.claimType)
        assertTrue(claim.detectedKeywords.contains("apollo"))
        assertTrue(claim.detectedKeywords.contains("moon"))
        assertEquals("Apollo 11 landed humans on the Moon in 1969", claim.normalizedText)

        val causalClaim = classifier.classify("Smoking causes lung cancer")
        assertEquals(ClaimType.CAUSAL, causalClaim.claimType)
        assertEquals(DomainCategory.MEDICINE, causalClaim.domainCategory)
    }
}
