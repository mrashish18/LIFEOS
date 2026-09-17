package com.mrashish18.lifeos.domain.usecase

import com.mrashish18.lifeos.core.common.DefaultDispatcherProvider
import com.mrashish18.lifeos.core.common.DispatcherProvider
import com.mrashish18.lifeos.core.context.ContextEngine
import com.mrashish18.lifeos.core.decision.DecisionEngine
import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.Recommendation
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.core.model.UserBehaviorModel
import com.mrashish18.lifeos.domain.repository.TaskRepository
import com.mrashish18.lifeos.domain.repository.UserBehaviorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

/**
 * Real aggregated dashboard domain state bundle.
 */
data class DashboardData(
    val snapshot: ContextSnapshot,
    val recommendations: List<Recommendation>,
    val tasks: List<Task>,
    val pendingCount: Int,
    val completedCount: Int,
    val behaviorModel: UserBehaviorModel
)

/**
 * Use case that aggregates real persisted task data, live context, behavior metrics, and explainable recommendations.
 */
class GetDashboardDataUseCase(
    private val contextEngine: ContextEngine,
    private val decisionEngine: DecisionEngine,
    private val taskRepository: TaskRepository,
    private val userBehaviorRepository: UserBehaviorRepository,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) {
    operator fun invoke(): Flow<DashboardData> {
        return combine(
            contextEngine.observeSnapshot(),
            taskRepository.getTasks(),
            userBehaviorRepository.observeUserBehaviorModel()
        ) { snapshot, tasks, behaviorModel ->
            val activeTask = tasks.find { it.status == TaskStatus.IN_PROGRESS }
            val enrichedSnapshot = snapshot.copy(activeTask = activeTask)
            val recommendations = decisionEngine.evaluate(
                snapshot = enrichedSnapshot,
                candidateTasks = tasks,
                behaviorModel = behaviorModel
            )

            val pendingCount = tasks.count { it.status == TaskStatus.PENDING || it.status == TaskStatus.IN_PROGRESS }
            val completedCount = tasks.count { it.status == TaskStatus.COMPLETED }

            DashboardData(
                snapshot = enrichedSnapshot,
                recommendations = recommendations,
                tasks = tasks,
                pendingCount = pendingCount,
                completedCount = completedCount,
                behaviorModel = behaviorModel
            )
        }.flowOn(dispatcherProvider.default)
    }
}
