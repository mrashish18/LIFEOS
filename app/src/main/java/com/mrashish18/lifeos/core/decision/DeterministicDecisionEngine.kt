package com.mrashish18.lifeos.core.decision

import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.Recommendation
import com.mrashish18.lifeos.core.model.RecommendationType
import com.mrashish18.lifeos.core.model.UserAvailability
import com.mrashish18.lifeos.core.model.WorkloadLevel
import java.time.Instant
import java.util.UUID

/**
 * Deterministic implementation of [DecisionEngine].
 *
 * Rules are explicitly codified, predictable, and explainable:
 * 1. High/Critical Workload -> Recommends a restorative break.
 * 2. Network Disconnection -> Informs user of offline-first local queue resilience mode.
 * 3. Active Task in progress -> Suggests sustained focus and context-switch minimization.
 * 4. Available with no active task -> Suggests task selection from queue.
 */
class DeterministicDecisionEngine(
    private val idGenerator: () -> String = { UUID.randomUUID().toString() },
    private val clock: () -> Instant = { Instant.now() }
) : DecisionEngine {

    override fun evaluate(snapshot: ContextSnapshot): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // Rule 1: High Cognitive / Task Workload
        if (snapshot.workloadLevel == WorkloadLevel.CRITICAL || snapshot.workloadLevel == WorkloadLevel.HIGH) {
            recommendations.add(
                Recommendation(
                    id = idGenerator(),
                    type = RecommendationType.BREAK_SUGGESTION,
                    title = "Workload Pace Alert",
                    reason = "Assessed workload level is ${snapshot.workloadLevel.name.lowercase()}. A short cognitive break is recommended to sustain performance.",
                    confidence = 0.95,
                    createdAt = clock()
                )
            )
        }

        // Rule 2: Offline Resilience Alert
        if (snapshot.networkState == NetworkState.DISCONNECTED) {
            recommendations.add(
                Recommendation(
                    id = idGenerator(),
                    type = RecommendationType.RESILIENCE_ALERT,
                    title = "Offline Resilience Mode",
                    reason = "Device is offline. Local-first operations are engaged; all changes will be queued and synchronized upon reconnection.",
                    confidence = 0.90,
                    createdAt = clock()
                )
            )
        }

        // Rule 3: Active Task Focus
        val currentTask = snapshot.activeTask
        if (currentTask != null) {
            recommendations.add(
                Recommendation(
                    id = idGenerator(),
                    type = RecommendationType.TASK_FOCUS,
                    title = "Focus: ${currentTask.title}",
                    reason = "Active task '${currentTask.title}' is in progress. Guard against context switching until current milestone is reached.",
                    confidence = 0.85,
                    createdAt = clock()
                )
            )
        } else if (snapshot.userAvailability == UserAvailability.AVAILABLE) {
            // Rule 4: Idle availability with no active task
            recommendations.add(
                Recommendation(
                    id = idGenerator(),
                    type = RecommendationType.TASK_FOCUS,
                    title = "Select Next Priority Task",
                    reason = "You are currently available with no active task checked out. Review prioritized pending items.",
                    confidence = 0.80,
                    createdAt = clock()
                )
            )
        }

        return recommendations
    }
}
