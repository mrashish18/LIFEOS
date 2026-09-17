package com.mrashish18.lifeos.domain.repository

import com.mrashish18.lifeos.core.model.Evidence

/**
 * Domain abstraction for evidence retrieval.
 *
 * Decouples the domain and presentation layers from specific storage
 * mechanisms (local corpus, cached fact databases, or future external APIs).
 */
interface EvidenceRepository {
    /**
     * Search for evidence items relevant to a query string.
     *
     * @param query The search query or extracted claim terms.
     * @return Result wrapping a list of retrieved evidence, or an error.
     */
    suspend fun search(query: String): Result<List<Evidence>>
}
