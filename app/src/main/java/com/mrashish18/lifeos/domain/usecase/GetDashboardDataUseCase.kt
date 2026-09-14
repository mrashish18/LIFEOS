package com.mrashish18.lifeos.domain.usecase

import com.mrashish18.lifeos.core.context.ContextEngine
import com.mrashish18.lifeos.core.decision.DecisionEngine
import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.Recommendation
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Domain data bundle required to populate the foundational Dashboard.
 */
data class DashboardData(
    val snapshot: ContextSnapshot,
    val recommendations: List<Recommendation>,
    val tasks: List<Task>
)

/**
 * Use case that orchestrates context observation, task retrieval, and decision evaluation.
 */
class GetDashboardDataUseCase(
    private val contextEngine: ContextEngine,
    private val decisionEngine: DecisionEngine,
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<DashboardData> {
        return combine(
            contextEngine.observeSnapshot(),
            taskRepository.getTasks()
        ) { snapshot, tasks ->
            // Enriched snapshot with active task if present
            val activeTask = tasks.find { it.status == com.mrashish18.lifeos.core.model.TaskStatus.IN_PROGRESS }
            val enrichedSnapshot = snapshot.copy(activeTask = activeTask)
            val recommendations = decisionEngine.evaluate(enrichedSnapshot)

            DashboardData(
                snapshot = enrichedSnapshot,
                recommendations = recommendations,
                tasks = tasks
            )
        }
    }
}
