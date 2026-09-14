package com.mrashish18.lifeos.core.context

import com.mrashish18.lifeos.core.model.ContextSnapshot
import kotlinx.coroutines.flow.Flow

/**
 * Central engine responsible for aggregating available context signals into an immutable [ContextSnapshot].
 */
interface ContextEngine {
    /**
     * Synchronously captures a point-in-time snapshot of the current context.
     */
    fun captureSnapshot(): ContextSnapshot

    /**
     * Observes contextual changes over time as a reactive stream.
     */
    fun observeSnapshot(): Flow<ContextSnapshot>
}
