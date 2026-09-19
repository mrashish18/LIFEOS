package com.mrashish18.lifeos.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mrashish18.lifeos.core.decision.LearningLoop
import com.mrashish18.lifeos.core.decision.UserFeedback
import com.mrashish18.lifeos.core.decision.UserResponseAction
import com.mrashish18.lifeos.core.model.BehaviorEvent
import com.mrashish18.lifeos.core.model.BehaviorEventType
import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.Recommendation
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.core.model.UserBehaviorModel
import com.mrashish18.lifeos.domain.repository.BehaviorEventRepository
import com.mrashish18.lifeos.domain.usecase.GetDashboardDataUseCase
import com.mrashish18.lifeos.domain.usecase.TransitionTaskStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

/**
 * UI State for the LIFEOS foundational dashboard with real persisted data.
 */
data class DashboardUiState(
    val isLoading: Boolean = true,
    val systemStatus: String = "Active • Operational",
    val contextSnapshot: ContextSnapshot? = null,
    val recommendations: List<Recommendation> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val pendingCount: Int = 0,
    val completedCount: Int = 0,
    val behaviorModel: UserBehaviorModel? = null,
    val lastFeedbackMessage: String? = null
)

/**
 * MVVM ViewModel for the central LIFEOS Dashboard.
 */
class DashboardViewModel(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val transitionTaskStatusUseCase: TransitionTaskStatusUseCase,
    private val behaviorEventRepository: BehaviorEventRepository,
    private val learningLoop: LearningLoop? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeDashboardData()
    }

    private fun observeDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getDashboardDataUseCase().collect { data ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        contextSnapshot = data.snapshot,
                        recommendations = data.recommendations,
                        tasks = data.tasks,
                        pendingCount = data.pendingCount,
                        completedCount = data.completedCount,
                        behaviorModel = data.behaviorModel
                    )
                }
            }
        }
    }

    fun acceptRecommendation(recommendation: Recommendation) {
        viewModelScope.launch {
            if (learningLoop != null) {
                learningLoop.onUserResponse(
                    UserFeedback(
                        recommendationId = recommendation.id,
                        action = UserResponseAction.ACCEPTED,
                        notes = recommendation.title
                    )
                )
            } else {
                // Log behavior event directly
                behaviorEventRepository.recordEvent(
                    BehaviorEvent(
                        id = UUID.randomUUID().toString(),
                        type = BehaviorEventType.RECOMMENDATION_ACCEPTED,
                        timestamp = Instant.now(),
                        metadata = mapOf(
                            "recommendationId" to recommendation.id,
                            "type" to recommendation.type.name,
                            "title" to recommendation.title,
                            "targetTaskId" to (recommendation.targetTaskId ?: "none")
                        )
                    )
                )
            }

            // If this recommendation has a target task, start it!
            recommendation.targetTaskId?.let { taskId ->
                transitionTaskStatusUseCase(taskId, TaskStatus.IN_PROGRESS)
            }

            _uiState.update { current ->
                current.copy(
                    recommendations = current.recommendations.filterNot { it.id == recommendation.id },
                    lastFeedbackMessage = "Accepted: \"${recommendation.title}\""
                )
            }
        }
    }

    fun dismissRecommendation(recommendation: Recommendation) {
        viewModelScope.launch {
            if (learningLoop != null) {
                learningLoop.onUserResponse(
                    UserFeedback(
                        recommendationId = recommendation.id,
                        action = UserResponseAction.DISMISSED,
                        notes = recommendation.title
                    )
                )
            } else {
                // Log behavior event directly
                behaviorEventRepository.recordEvent(
                    BehaviorEvent(
                        id = UUID.randomUUID().toString(),
                        type = BehaviorEventType.RECOMMENDATION_REJECTED,
                        timestamp = Instant.now(),
                        metadata = mapOf(
                            "recommendationId" to recommendation.id,
                            "type" to recommendation.type.name,
                            "title" to recommendation.title
                        )
                    )
                )
            }

            _uiState.update { current ->
                current.copy(
                    recommendations = current.recommendations.filterNot { it.id == recommendation.id },
                    lastFeedbackMessage = "Dismissed: \"${recommendation.title}\""
                )
            }
        }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(lastFeedbackMessage = null) }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val getDashboardDataUseCase: GetDashboardDataUseCase,
        private val transitionTaskStatusUseCase: TransitionTaskStatusUseCase,
        private val behaviorEventRepository: BehaviorEventRepository,
        private val learningLoop: LearningLoop? = null
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel(
                    getDashboardDataUseCase,
                    transitionTaskStatusUseCase,
                    behaviorEventRepository,
                    learningLoop
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
