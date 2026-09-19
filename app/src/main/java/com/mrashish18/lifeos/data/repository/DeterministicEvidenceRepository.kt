package com.mrashish18.lifeos.data.repository

import com.mrashish18.lifeos.core.model.Evidence
import com.mrashish18.lifeos.core.model.EvidenceSource
import com.mrashish18.lifeos.core.model.SourceQuality
import com.mrashish18.lifeos.domain.repository.EvidenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Deterministic, offline-first evidence repository populated with verified,
 * curated reference documents across science, medicine, technology, and common myths.
 *
 * Adheres strictly to the LIFEOS architectural rule:
 * No fake web-scraping or ungrounded generative retrieval.
 */
class DeterministicEvidenceRepository(
    private val corpus: List<Evidence> = defaultCorpus()
) : EvidenceRepository {

    override suspend fun search(query: String): Result<List<Evidence>> = withContext(Dispatchers.Default) {
        runCatching {
            val normalizedQuery = query.lowercase().trim()
            if (normalizedQuery.isBlank()) {
                return@runCatching emptyList()
            }

            // Tokenize query into meaningful search tokens (skip common stopwords)
            val tokens = normalizedQuery.split(Regex("[\\s,;:.?!'\"()-]+"))
                .filter { it.length > 2 && it !in STOPWORDS }

            if (tokens.isEmpty()) {
                return@runCatching emptyList()
            }

            // Score each evidence document by token overlap in title and snippet
            val matchesWithScores = corpus.mapNotNull { evidence ->
                val titleLower = evidence.title.lowercase()
                val snippetLower = evidence.snippet.lowercase()
                val titleTokens = titleLower.split(Regex("[\\s,;:.?!'\"()-]+")).toSet()
                val snippetTokens = snippetLower.split(Regex("[\\s,;:.?!'\"()-]+")).toSet()

                var matchScore = 0
                for (token in tokens) {
                    if (titleTokens.contains(token)) {
                        matchScore += 4 // Direct title keyword match
                    } else if (snippetTokens.contains(token)) {
                        matchScore += 2 // Direct snippet keyword match
                    } else if (token.length >= 4 && (titleLower.contains(token) || snippetLower.contains(token))) {
                        matchScore += 1 // Substring root match
                    }
                }

                if (matchScore >= 2) {
                    evidence to matchScore
                } else {
                    null
                }
            }

            // Sort by match score descending and take top matching documents
            matchesWithScores
                .sortedByDescending { it.second }
                .map { it.first }
                .take(6)
        }
    }

    companion object {
        private val STOPWORDS = setOf(
            "the", "and", "that", "this", "with", "from", "for", "are", "was",
            "were", "will", "have", "has", "had", "does", "did", "can", "could"
        )

        fun defaultCorpus(): List<Evidence> = listOf(
            // 1. Medicine: Antibiotics vs Viruses
            Evidence(
                id = "med_antibiotics_cdc",
                title = "Antibiotics Do Not Fight Viruses",
                snippet = "Antibiotics only treat certain infections caused by bacteria, such as strep throat and urinary tract infections. Antibiotics do not work against viruses like those that cause colds, flu, or most sore throats.",
                source = EvidenceSource(
                    name = "U.S. Centers for Disease Control and Prevention (CDC)",
                    url = "https://www.cdc.gov/antibiotic-use/about/index.html",
                    quality = SourceQuality.OFFICIAL,
                    description = "Federal agency responsible for public health and infectious disease control"
                ),
                publicationDate = "2023-10-04"
            ),
            Evidence(
                id = "med_antibiotics_who",
                title = "Antimicrobial Resistance and Viral Infections",
                snippet = "Taking antibiotics for viral illnesses like colds or acute respiratory infections is ineffective. Misuse of antibiotics promotes bacterial resistance without providing any therapeutic benefit against viral pathogens.",
                source = EvidenceSource(
                    name = "World Health Organization (WHO)",
                    url = "https://www.who.int/news-room/fact-sheets/detail/antimicrobial-resistance",
                    quality = SourceQuality.OFFICIAL,
                    description = "Specialized United Nations agency for international public health"
                ),
                publicationDate = "2023-11-21"
            ),

            // 2. Astronomy: Heliocentrism & Earth Orbit
            Evidence(
                id = "astro_earth_orbit_nasa",
                title = "Earth's Orbital Characteristics Around the Sun",
                snippet = "Earth orbits the Sun at an average distance of approximately 149.6 million kilometers (93 million miles) in an elliptical orbit, taking 365.256 days to complete a full revolution.",
                source = EvidenceSource(
                    name = "NASA Solar System Exploration",
                    url = "https://solarsystem.nasa.gov/planets/earth/in-depth/",
                    quality = SourceQuality.OFFICIAL,
                    description = "United States space exploration and aeronautics administration"
                ),
                publicationDate = "2022-08-15"
            ),
            Evidence(
                id = "astro_earth_orbit_britannica",
                title = "Heliocentric System and Planetary Motion",
                snippet = "The astronomical model in which the Earth and planets revolve around the Sun at the center of the Solar System is supported by extensive celestial mechanics and observations initiated by Copernicus, Kepler, and Galileo.",
                source = EvidenceSource(
                    name = "Encyclopaedia Britannica",
                    url = "https://www.britannica.com/science/heliocentric-system",
                    quality = SourceQuality.REFERENCE,
                    description = "General knowledge and academic reference encyclopedia"
                ),
                publicationDate = "2023-01-10"
            ),

            // 3. Technology: Kotlin Language Origins
            Evidence(
                id = "tech_kotlin_jetbrains",
                title = "Kotlin Programming Language Specification and History",
                snippet = "Kotlin is an open-source, statically typed programming language developed primarily by JetBrains with help from external contributors. Kotlin 1.0 was officially released on February 15, 2016.",
                source = EvidenceSource(
                    name = "JetBrains",
                    url = "https://blog.jetbrains.com/kotlin/2016/02/kotlin-1-0-released-pragmatic-language-for-jvm-and-android/",
                    quality = SourceQuality.PRIMARY,
                    description = "Creator and primary sponsor organization of Kotlin"
                ),
                publicationDate = "2016-02-15"
            ),
            Evidence(
                id = "tech_android_kotlin_google",
                title = "Android's Kotlin-First Approach",
                snippet = "At Google I/O 2017, Google announced first-class support for Kotlin on Android. Later in May 2019, Google announced that Android development would become increasingly Kotlin-first.",
                source = EvidenceSource(
                    name = "Android Developers Blog",
                    url = "https://android-developers.googleblog.com/2019/05/google-io-2019-empowering-android-developers.html",
                    quality = SourceQuality.OFFICIAL,
                    description = "Official news and announcements for Android platform developers"
                ),
                publicationDate = "2019-05-07"
            ),

            // 4. Neuroscience: The "10% Brain Myth"
            Evidence(
                id = "neuro_ten_percent_sciam",
                title = "Do People Only Use 10 Percent of Their Brains?",
                snippet = "Neuroimaging evidence including PET and fMRI scans demonstrates that vast areas of the brain remain active even during sleep, and nearly all parts of the brain have identified functions. The 10 percent myth is unsupported by neuroscience.",
                source = EvidenceSource(
                    name = "Scientific American",
                    url = "https://www.scientificamerican.com/article/do-people-only-use-10-percent-of-their-brains/",
                    quality = SourceQuality.REPUTABLE_NEWS,
                    description = "Popular science magazine covering peer-reviewed scientific discoveries"
                ),
                publicationDate = "2008-02-07"
            ),
            Evidence(
                id = "neuro_ten_percent_sfn",
                title = "Neuromyths: Debunking Brain Usage Myths",
                snippet = "Damage to even small, localized regions of the brain almost invariably results in noticeable cognitive, sensory, or motor deficits, proving that humans utilize far more than an arbitrary 10 percent of their neural tissue.",
                source = EvidenceSource(
                    name = "Society for Neuroscience (BrainFacts.org)",
                    url = "https://www.brainfacts.org/thinking-sensing-and-behaving/brain-development/2012/the-ten-percent-myth",
                    quality = SourceQuality.PRIMARY,
                    description = "Non-profit organization representing scientists and physicians studying the brain"
                ),
                publicationDate = "2012-04-01"
            ),

            // 5. Health: Vaccines and Autism Myth
            Evidence(
                id = "health_vaccine_autism_cdc",
                title = "Vaccine Safety: Autism and MMR Vaccine",
                snippet = "Extensive scientific studies have investigated whether there is any relationship between the MMR vaccine and autism. There is no link between vaccines and autism. Multiple rigorously controlled studies conducted over two decades confirm that vaccines do not cause autism.",
                source = EvidenceSource(
                    name = "U.S. Centers for Disease Control and Prevention (CDC)",
                    url = "https://www.cdc.gov/vaccinesafety/concerns/autism.html",
                    quality = SourceQuality.OFFICIAL,
                    description = "Federal public health authority"
                ),
                publicationDate = "2022-09-09"
            ),
            Evidence(
                id = "health_vaccine_autism_lancet",
                title = "Full Retraction: Ileal-lymphoid-nodular hyperplasia, non-specific colitis, and pervasive developmental disorder in children",
                snippet = "The editors of The Lancet fully retracted the 1998 paper by Wakefield et al. following findings of fraudulent data, methodological flaws, and undisclosed conflicts of interest by the UK General Medical Council.",
                source = EvidenceSource(
                    name = "The Lancet",
                    url = "https://www.thelancet.com/journals/lancet/article/PIIS0140-6736(10)60175-4/fulltext",
                    quality = SourceQuality.PRIMARY,
                    description = "Weekly peer-reviewed general medical journal"
                ),
                publicationDate = "2010-02-06"
            ),

            // 6. Nutrition / Health: Coffee and Health (Mixed Evidence)
            Evidence(
                id = "nutrition_coffee_harvard",
                title = "Coffee and Health: Potential Benefits and Risks",
                snippet = "Moderate coffee consumption (3 to 5 cups daily) has been consistently associated with a lower risk of cardiovascular disease, type 2 diabetes, and certain cancers due to rich polyphenol antioxidants. However, excessive caffeine intake can cause anxiety, insomnia, palpitations, and blood pressure spikes in sensitive individuals.",
                source = EvidenceSource(
                    name = "Harvard T.H. Chan School of Public Health",
                    url = "https://www.hsph.harvard.edu/nutritionsource/food-features/coffee/",
                    quality = SourceQuality.PRIMARY,
                    description = "Academic institution specializing in public health and nutritional research"
                ),
                publicationDate = "2021-04-12"
            ),
            Evidence(
                id = "nutrition_coffee_mayo",
                title = "Caffeine: How much is too much?",
                snippet = "Up to 400 milligrams of caffeine a day appears to be safe for most healthy adults. However, high doses can lead to restlessness, tremor, fast heartbeat, and sleep disruption. Pregnant women and individuals with arrhythmias are advised to limit consumption.",
                source = EvidenceSource(
                    name = "Mayo Clinic",
                    url = "https://www.mayoclinic.org/healthy-lifestyle/nutrition-and-healthy-eating/in-depth/caffeine/art-20045678",
                    quality = SourceQuality.REFERENCE,
                    description = "Nonprofit American academic medical center"
                ),
                publicationDate = "2022-03-19"
            ),

            // 7. Space History: Apollo 11 Moon Landing
            Evidence(
                id = "history_moon_landing_nasa",
                title = "Apollo 11 Mission Overview",
                snippet = "On July 20, 1969, American astronauts Neil Armstrong and Buzz Aldrin landed the Apollo Lunar Module Eagle on the Moon, and Armstrong became the first person to step onto the lunar surface at 02:56 UTC on July 21, 1969.",
                source = EvidenceSource(
                    name = "NASA National Space Science Data Center",
                    url = "https://nssdc.gsfc.nasa.gov/planetary/lunar/apollo11.html",
                    quality = SourceQuality.OFFICIAL,
                    description = "Official NASA archive of lunar missions and scientific telemetry"
                ),
                publicationDate = "2019-07-08"
            ),

            // 8. Physics: Speed of Light
            Evidence(
                id = "physics_speed_of_light_nist",
                title = "Fundamental Physical Constants: Speed of Light in Vacuum",
                snippet = "The speed of light in a vacuum, denoted as c, is an exact physical constant defined by the International System of Units as 299,792,458 meters per second.",
                source = EvidenceSource(
                    name = "National Institute of Standards and Technology (NIST)",
                    url = "https://physics.nist.gov/cgi-bin/cuu/Value?c",
                    quality = SourceQuality.OFFICIAL,
                    description = "National measurement science standards laboratory",
                    authorityRationale = "National metrology institute establishing fundamental physical constants"
                ),
                publicationDate = "2019-05-20"
            ),

            // 9. Hydration / Physiology: 8 Glasses of Water Myth
            Evidence(
                id = "health_water_harvard",
                title = "How Much Water Should You Drink?",
                snippet = "There is no single formula that fits everyone. The popular advice to drink eight glasses of water daily is a reasonable goal but has no firm scientific basis; fluid requirements vary by body weight, physical activity, climate, and water consumed in foods.",
                source = EvidenceSource(
                    name = "Harvard Health Publishing",
                    url = "https://www.health.harvard.edu/staying-healthy/how-much-water-should-you-drink",
                    quality = SourceQuality.PRIMARY,
                    description = "Consumer health publishing division of Harvard Medical School",
                    authorityRationale = "Academic medical school publication with peer-review oversight"
                ),
                publicationDate = "2023-05-15"
            ),

            // 10. Technology / Telecommunications: 5G and Health
            Evidence(
                id = "tech_5g_who",
                title = "5G Mobile Networks and Public Health",
                snippet = "To date, and after much research conducted, no adverse health effect has been causally linked with exposure to wireless technologies. Provided that the overall exposure remains below international guidelines, no consequences for public health are anticipated.",
                source = EvidenceSource(
                    name = "World Health Organization (WHO)",
                    url = "https://www.who.int/news-room/questions-and-answers/item/radiation-5g-mobile-networks-and-health",
                    quality = SourceQuality.OFFICIAL,
                    description = "Specialized United Nations agency for international public health",
                    authorityRationale = "International public health authority conducting global epidemiological reviews"
                ),
                publicationDate = "2020-02-27"
            )
        )
    }
}
