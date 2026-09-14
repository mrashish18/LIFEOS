package com.mrashish18.lifeos.core.decision

import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.Recommendation

/**
 * Engine responsible for evaluating contextual signals and generating explainable recommendations.
 *
 * Core architectural principle:
 * Deterministic rules control authoritative decisions. AI/LLMs will later assist with
 * interpretation, decomposition, explanations, and pattern discovery without blindly
 * controlling application execution.
 */
interface DecisionEngine {
    /**
     * Evaluates the current [snapshot] and produces zero or more explainable [Recommendation]s.
     */
    fun evaluate(snapshot: ContextSnapshot): List<Recommendation>
}
