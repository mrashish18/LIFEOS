package com.mrashish18.lifeos.core.decision

import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.Recommendation
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.UserBehaviorModel

/**
 * Engine responsible for evaluating contextual signals and generating explainable recommendations.
 *
 * Core architectural principle:
 * Deterministic rules and transparent heuristics control authoritative decisions.
 */
interface DecisionEngine {
    /**
     * Evaluates the current [snapshot], available [candidateTasks], and optional [behaviorModel]
     * to produce zero or more explainable [Recommendation]s.
     */
    fun evaluate(
        snapshot: ContextSnapshot,
        candidateTasks: List<Task> = emptyList(),
        behaviorModel: UserBehaviorModel? = null
    ): List<Recommendation>
}
