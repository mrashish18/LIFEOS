package com.mrashish18.lifeos.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mrashish18.lifeos.core.context.ContextEngine
import com.mrashish18.lifeos.core.decision.DecisionEngine
import com.mrashish18.lifeos.core.model.ContextSnapshot
import com.mrashish18.lifeos.core.model.Recommendation
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.domain.repository.TaskRepository
import com.mrashish18.lifeos.domain.usecase.GetDashboardDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for the LIFEOS foundational dashboard.
 */
data class DashboardUiState(
    val isLoading: Boolean = true,
    val systemStatus: String = "Active • Operational",
    val contextSnapshot: ContextSnapshot? = null,
    val recommendations: List<Recommendation> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val lastFeedbackMessage: String? = null
)

/**
 * MVVM ViewModel for the central LIFEOS Dashboard.
 */
class DashboardViewModel(
    private val getDashboardDataUseCase: GetDashboardDataUseCase
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
                        tasks = data.tasks
                    )
                }
            }
        }
    }

    fun acceptRecommendation(recommendation: Recommendation) {
        _uiState.update { current ->
            current.copy(
                recommendations = current.recommendations.filterNot { it.id == recommendation.id },
                lastFeedbackMessage = "Action recorded: Accepted \"${recommendation.title}\""
            )
        }
    }

    fun dismissRecommendation(recommendation: Recommendation) {
        _uiState.update { current ->
            current.copy(
                recommendations = current.recommendations.filterNot { it.id == recommendation.id },
                lastFeedbackMessage = "Action recorded: Dismissed \"${recommendation.title}\""
            )
        }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(lastFeedbackMessage = null) }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val getDashboardDataUseCase: GetDashboardDataUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel(getDashboardDataUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
