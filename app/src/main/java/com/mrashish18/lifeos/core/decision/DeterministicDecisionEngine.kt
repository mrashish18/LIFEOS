package com.mrashish18.lifeos.core.decision

import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.Recommendation
import com.mrashish18.lifeos.core.model.RecommendationFactor
import com.mrashish18.lifeos.core.model.RecommendationType
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.core.model.UserAvailability
import com.mrashish18.lifeos.core.model.UserBehaviorModel
import com.mrashish18.lifeos.core.model.WorkloadLevel
import com.mrashish18.lifeos.core.model.TaskCategory
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

/**
 * Deterministic, explainable implementation of [DecisionEngine].
 *
 * Scoring and ranking use transparent deterministic heuristics:
 * - Urgency (due dates / overdue status)
 * - Priority weighting
 * - Effort / Quick Win fit
 * - Observed behavioral adaptation (momentum in preferred categories, completion rate)
 * - Contextual guardrails (workload pacing, offline mode)
 */
class DeterministicDecisionEngine(
    private val idGenerator: () -> String = { UUID.randomUUID().toString() },
    private val clock: () -> Instant = { Instant.now() },
    private val zoneId: ZoneId = ZoneId.systemDefault()
) : DecisionEngine {

    override fun evaluate(
        snapshot: ContextSnapshot,
        candidateTasks: List<Task>,
        behaviorModel: UserBehaviorModel?
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()
        val now = clock()

        // Rule 1: High/Critical Cognitive Workload Alert
        if (snapshot.workloadLevel == WorkloadLevel.CRITICAL || snapshot.workloadLevel == WorkloadLevel.HIGH) {
            val factors = listOf(
                RecommendationFactor(
                    name = "High Workload State",
                    description = "Workload assessed as ${snapshot.workloadLevel.name.lowercase()}",
                    scoreContribution = 0.50
                ),
                RecommendationFactor(
                    name = "Cognitive Pacing",
                    description = "Rest interval recommended before initiating new demanding tasks",
                    scoreContribution = 0.45
                )
            )
            recommendations.add(
                Recommendation(
                    id = idGenerator(),
                    type = RecommendationType.BREAK_SUGGESTION,
                    title = "Workload Pace Alert",
                    reason = "Assessed workload level is ${snapshot.workloadLevel.name.lowercase()}. A short cognitive break is recommended.",
                    confidence = 0.95,
                    factors = factors,
                    createdAt = now
                )
            )
        }

        // Rule 2: Offline Resilience Mode Alert
        if (snapshot.networkState == NetworkState.DISCONNECTED) {
            val factors = listOf(
                RecommendationFactor(
                    name = "Offline Connectivity",
                    description = "Device is currently disconnected from network",
                    scoreContribution = 0.50
                ),
                RecommendationFactor(
                    name = "Local-First Storage",
                    description = "Task edits and events are safely stored in Room SQLite",
                    scoreContribution = 0.40
                )
            )
            recommendations.add(
                Recommendation(
                    id = idGenerator(),
                    type = RecommendationType.RESILIENCE_ALERT,
                    title = "Offline Resilience Mode",
                    reason = "Device is offline. Running in local-first resilience mode; changes are safely queued on-device.",
                    confidence = 0.90,
                    factors = factors,
                    createdAt = now
                )
            )
        }

        // Rule 3: Active In-Progress Task Focus
        val activeTask = snapshot.activeTask ?: candidateTasks.find { it.status == TaskStatus.IN_PROGRESS }
        if (activeTask != null) {
            val factors = listOf(
                RecommendationFactor(
                    name = "Active Task",
                    description = "Task '${activeTask.title}' is currently checked out",
                    scoreContribution = 0.50
                ),
                RecommendationFactor(
                    name = "Context Protection",
                    description = "Guarding against fragmentation and parallel multitasking",
                    scoreContribution = 0.35
                )
            )
            recommendations.add(
                Recommendation(
                    id = idGenerator(),
                    type = RecommendationType.TASK_FOCUS,
                    title = "Focus: ${activeTask.title}",
                    reason = "Active task '${activeTask.title}' is in progress. Minimize context switches.",
                    confidence = 0.85,
                    factors = factors,
                    targetTaskId = activeTask.id,
                    createdAt = now
                )
            )
        } else {
            // Rule 4: Rank Pending/Postponed Tasks to recommend the next best action
            val actionableTasks = candidateTasks.filter {
                it.status == TaskStatus.PENDING || it.status == TaskStatus.POSTPONED
            }

            if (actionableTasks.isNotEmpty()) {
                val scoredTasks = actionableTasks.map { task ->
                    scoreTask(task, now, behaviorModel, snapshot)
                }.sortedByDescending { it.totalScore }

                val topCandidate = scoredTasks.first()
                val confidence = (topCandidate.totalScore / 100.0).coerceIn(0.60, 0.95)

                recommendations.add(
                    Recommendation(
                        id = idGenerator(),
                        type = RecommendationType.TASK_FOCUS,
                        title = "Recommended Focus: ${topCandidate.task.title}",
                        reason = topCandidate.factors.joinToString(" • ") { it.description },
                        confidence = confidence,
                        factors = topCandidate.factors,
                        targetTaskId = topCandidate.task.id,
                        createdAt = now
                    )
                )
            } else if (snapshot.userAvailability == UserAvailability.AVAILABLE) {
                // Rule 5: Idle with no tasks in queue
                val factors = listOf(
                    RecommendationFactor(
                        name = "Availability",
                        description = "Marked as available with clear queue",
                        scoreContribution = 0.80
                    )
                )
                recommendations.add(
                    Recommendation(
                        id = idGenerator(),
                        type = RecommendationType.TASK_FOCUS,
                        title = "Select Next Priority Task",
                        reason = "You are currently available with no active task checked out.",
                        confidence = 0.80,
                        factors = factors,
                        createdAt = now
                    )
                )
            }
        }

        return recommendations
    }

    private data class ScoredTask(
        val task: Task,
        val totalScore: Double,
        val factors: List<RecommendationFactor>
    )

    private fun scoreTask(
        task: Task,
        now: Instant,
        behaviorModel: UserBehaviorModel?,
        snapshot: ContextSnapshot
    ): ScoredTask {
        var score = 0.0
        val factors = mutableListOf<RecommendationFactor>()

        // 1. Priority Weight
        when (task.priority) {
            TaskPriority.URGENT -> {
                score += 40.0
                factors.add(RecommendationFactor("Priority", "Urgent priority task", 0.40))
            }
            TaskPriority.HIGH -> {
                score += 30.0
                factors.add(RecommendationFactor("Priority", "High priority item", 0.30))
            }
            TaskPriority.MEDIUM -> {
                score += 20.0
                factors.add(RecommendationFactor("Priority", "Standard priority item", 0.20))
            }
            TaskPriority.LOW -> {
                score += 10.0
                factors.add(RecommendationFactor("Priority", "Low priority backlog", 0.10))
            }
        }

        // 2. Deadline Urgency
        if (task.dueAt != null) {
            val durationToDue = Duration.between(now, task.dueAt)
            when {
                durationToDue.isNegative -> {
                    score += 40.0
                    factors.add(RecommendationFactor("Overdue", "Task past target deadline", 0.40))
                }
                durationToDue.toHours() <= 24 -> {
                    score += 30.0
                    factors.add(RecommendationFactor("Due Soon", "Due within 24 hours", 0.30))
                }
                durationToDue.toDays() <= 3 -> {
                    score += 15.0
                    factors.add(RecommendationFactor("Upcoming", "Due within 3 days", 0.15))
                }
            }
        }

        // 3. Effort Fit / Task Size Adaptation
        val minutes = task.estimatedMinutes
        if (behaviorModel?.preferredTaskSize != null) {
            val matchesPreferred = when (behaviorModel.preferredTaskSize) {
                com.mrashish18.lifeos.core.model.TaskSizePreference.MICRO -> (minutes ?: 30) <= 20
                com.mrashish18.lifeos.core.model.TaskSizePreference.STANDARD -> (minutes ?: 30) in 21..45
                com.mrashish18.lifeos.core.model.TaskSizePreference.DEEP -> (minutes ?: 30) > 45
            }
            if (matchesPreferred) {
                score += 15.0
                factors.add(RecommendationFactor("Effort Fit", "Matches preferred focus duration (${behaviorModel.preferredTaskSize.label})", 0.15))
            }
        } else if (minutes != null && minutes <= 30) {
            score += 15.0
            factors.add(RecommendationFactor("Quick Win", "Estimated effort <= 30 minutes ($minutes min)", 0.15))
        }

        // 4. Circadian Energy / Peak Productivity Alignment
        if (behaviorModel?.peakProductivityTimeOfDay != null) {
            val currentBucket = com.mrashish18.lifeos.core.model.TimeOfDayBucket.fromInstant(snapshot.currentTime, zoneId)
            if (currentBucket == behaviorModel.peakProductivityTimeOfDay) {
                score += 15.0
                factors.add(RecommendationFactor("Circadian Peak", "Matches observed peak energy window (${behaviorModel.peakProductivityTimeOfDay.name.lowercase()})", 0.15))
            }
        }

        // 5. Postponement Recovery
        if (task.status == TaskStatus.POSTPONED) {
            score += 15.0
            factors.add(RecommendationFactor("Postponement Recovery", "Prioritizing previously postponed item to prevent backlog decay", 0.15))
        }

        // 6. Behavioral Adaptation (only when sufficient observations exist)
        if (behaviorModel != null && behaviorModel.hasSufficientData) {
            // Category momentum bonus: award to top preferred category
            val topCategory = behaviorModel.preferredCategories.maxByOrNull { it.value }?.key
            val completedInCat = behaviorModel.preferredCategories[task.category] ?: 0
            if (task.category == topCategory && completedInCat > 0) {
                score += 15.0
                factors.add(RecommendationFactor("Category Habit", "Demonstrated momentum in ${task.category.name}", 0.15))
            } else if (completedInCat > 0) {
                score += 5.0
                factors.add(RecommendationFactor("Familiar Category", "Historical completion in ${task.category.name}", 0.05))
            }

            // Category completion rate consistency
            val catCompletionRate = behaviorModel.categoryCompletionRates[task.category]
            if (catCompletionRate != null && catCompletionRate >= 0.70) {
                score += 10.0
                factors.add(RecommendationFactor("Category Consistency", "${(catCompletionRate * 100).toInt()}% historical completion in ${task.category.name}", 0.10))
            }

            // High completion rate positive reinforcement
            val completionRate = behaviorModel.completionRate
            if (completionRate != null && completionRate >= 0.70) {
                score += 5.0
                factors.add(RecommendationFactor("High Completion Rate", "${(completionRate * 100).toInt()}% historical task completion", 0.05))
            }
        }

        // 7. Strategic Alignment
        val strategicFocus = when (task.category) {
            TaskCategory.WORK -> "Autonomous Productivity"
            TaskCategory.HEALTH -> "Circadian & Pacing"
            TaskCategory.LEARNING -> "Deep Habituation"
            TaskCategory.PERSONAL, TaskCategory.GENERAL -> null
        }
        if (strategicFocus != null) {
            score += 5.0
            factors.add(RecommendationFactor("Strategic Alignment", "Supports $strategicFocus", 0.05))
        }

        return ScoredTask(task, score, factors)
    }
}
