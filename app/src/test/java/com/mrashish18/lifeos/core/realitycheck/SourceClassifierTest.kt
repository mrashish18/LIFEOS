package com.mrashish18.lifeos.core.realitycheck

import com.mrashish18.lifeos.core.model.SourceQuality
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SourceClassifierTest {

    private lateinit var classifier: SourceClassifier

    @Before
    fun setUp() {
        classifier = SourceClassifier()
    }

    @Test
    fun `classify identifies official and government domains`() {
        assertEquals(
            SourceQuality.OFFICIAL,
            classifier.classify("CDC", "https://www.cdc.gov/vaccinesafety")
        )
        assertEquals(
            SourceQuality.OFFICIAL,
            classifier.classify("NASA Exploration", "https://solarsystem.nasa.gov/planets")
        )
        assertEquals(
            SourceQuality.OFFICIAL,
            classifier.classify("World Health Organization", "https://www.who.int/news-room")
        )
    }

    @Test
    fun `classify identifies primary academic publishers and creators`() {
        assertEquals(
            SourceQuality.PRIMARY,
            classifier.classify("The Lancet", "https://www.thelancet.com/article/123")
        )
        assertEquals(
            SourceQuality.PRIMARY,
            classifier.classify("Nature Publishing", "https://www.nature.com/articles/456")
        )
        assertEquals(
            SourceQuality.PRIMARY,
            classifier.classify("JetBrains Blog", "https://blog.jetbrains.com/kotlin")
        )
        assertEquals(
            SourceQuality.PRIMARY,
            classifier.classify("Harvard T.H. Chan School of Public Health", "https://www.hsph.harvard.edu/coffee")
        )
    }

    @Test
    fun `classify identifies reputable news organizations`() {
        assertEquals(
            SourceQuality.REPUTABLE_NEWS,
            classifier.classify("Reuters", "https://www.reuters.com/world")
        )
        assertEquals(
            SourceQuality.REPUTABLE_NEWS,
            classifier.classify("BBC News", "https://www.bbc.com/news")
        )
        assertEquals(
            SourceQuality.REPUTABLE_NEWS,
            classifier.classify("Scientific American", "https://www.scientificamerican.com/article/10-percent")
        )
    }

    @Test
    fun `classify identifies established reference authorities`() {
        assertEquals(
            SourceQuality.REFERENCE,
            classifier.classify("Encyclopaedia Britannica", "https://www.britannica.com/science")
        )
        assertEquals(
            SourceQuality.REFERENCE,
            classifier.classify("Mayo Clinic", "https://www.mayoclinic.org/caffeine")
        )
    }

    @Test
    fun `classify defaults unknown sources to UNKNOWN`() {
        assertEquals(
            SourceQuality.UNKNOWN,
            classifier.classify("Random Tech Blog", "https://randomtechblog.xyz/post/123")
        )
        assertEquals(
            SourceQuality.UNKNOWN,
            classifier.classify("User Forum", "https://someforum.io/thread/999")
        )
    }
}
