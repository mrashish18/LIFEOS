package com.mrashish18.lifeos.feature.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskCategory
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.domain.repository.TaskRepository
import com.mrashish18.lifeos.domain.usecase.CreateTaskUseCase
import com.mrashish18.lifeos.domain.usecase.DeleteTaskUseCase
import com.mrashish18.lifeos.domain.usecase.TransitionTaskStatusUseCase
import com.mrashish18.lifeos.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

enum class TaskFilter(val label: String) {
    ALL("All"),
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Done"),
    POSTPONED("Postponed"),
    ABANDONED("Abandoned")
}

data class TasksUiState(
    val allTasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val isLoading: Boolean = true,
    val isCreateDialogOpen: Boolean = false,
    val editingTask: Task? = null,
    val userMessage: String? = null
)

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val transitionTaskStatusUseCase: TransitionTaskStatusUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    init {
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            taskRepository.getTasks().collect { tasks ->
                _uiState.update { current ->
                    current.copy(
                        allTasks = tasks,
                        filteredTasks = applyFilter(tasks, current.selectedFilter),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun setFilter(filter: TaskFilter) {
        _uiState.update { current ->
            current.copy(
                selectedFilter = filter,
                filteredTasks = applyFilter(current.allTasks, filter)
            )
        }
    }

    private fun applyFilter(tasks: List<Task>, filter: TaskFilter): List<Task> {
        return when (filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.PENDING -> tasks.filter { it.status == TaskStatus.PENDING }
            TaskFilter.IN_PROGRESS -> tasks.filter { it.status == TaskStatus.IN_PROGRESS }
            TaskFilter.COMPLETED -> tasks.filter { it.status == TaskStatus.COMPLETED }
            TaskFilter.POSTPONED -> tasks.filter { it.status == TaskStatus.POSTPONED }
            TaskFilter.ABANDONED -> tasks.filter { it.status == TaskStatus.ABANDONED }
        }
    }

    fun openCreateDialog() {
        _uiState.update { it.copy(isCreateDialogOpen = true) }
    }

    fun closeCreateDialog() {
        _uiState.update { it.copy(isCreateDialogOpen = false) }
    }

    fun openEditDialog(task: Task) {
        _uiState.update { it.copy(editingTask = task) }
    }

    fun closeEditDialog() {
        _uiState.update { it.copy(editingTask = null) }
    }

    fun createTask(
        title: String,
        description: String,
        priority: TaskPriority,
        category: TaskCategory,
        estimatedMinutes: Int?,
        dueAt: Instant?
    ) {
        if (title.isBlank()) {
            _uiState.update { it.copy(userMessage = "Title cannot be empty") }
            return
        }

        viewModelScope.launch {
            try {
                createTaskUseCase(
                    title = title,
                    description = description,
                    priority = priority,
                    category = category,
                    estimatedMinutes = estimatedMinutes,
                    dueAt = dueAt
                )
                _uiState.update { it.copy(isCreateDialogOpen = false, userMessage = "Task created • Candidate queued for Decision Engine") }
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Failed to create task: ${e.message}") }
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                updateTaskUseCase(task)
                _uiState.update { it.copy(editingTask = null, userMessage = "Task updated") }
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Failed to update task: ${e.message}") }
            }
        }
    }

    fun startTask(taskId: String) {
        viewModelScope.launch {
            transitionTaskStatusUseCase(taskId, TaskStatus.IN_PROGRESS)
            _uiState.update { it.copy(userMessage = "Focus initiated • Tracking circadian & duration signals") }
        }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            transitionTaskStatusUseCase(taskId, TaskStatus.COMPLETED)
            _uiState.update { it.copy(userMessage = "Task completed • Behavior recorded for learning loop") }
        }
    }

    fun postponeTask(taskId: String) {
        viewModelScope.launch {
            transitionTaskStatusUseCase(taskId, TaskStatus.POSTPONED)
            _uiState.update { it.copy(userMessage = "Task postponed • Learning loop recorded postponement") }
        }
    }

    fun abandonTask(taskId: String) {
        viewModelScope.launch {
            transitionTaskStatusUseCase(taskId, TaskStatus.ABANDONED)
            _uiState.update { it.copy(userMessage = "Task abandoned • Terminal outcome recorded") }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            deleteTaskUseCase(taskId)
            _uiState.update { it.copy(userMessage = "Task deleted") }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val taskRepository: TaskRepository,
        private val createTaskUseCase: CreateTaskUseCase,
        private val updateTaskUseCase: UpdateTaskUseCase,
        private val transitionTaskStatusUseCase: TransitionTaskStatusUseCase,
        private val deleteTaskUseCase: DeleteTaskUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
                return TasksViewModel(
                    taskRepository,
                    createTaskUseCase,
                    updateTaskUseCase,
                    transitionTaskStatusUseCase,
                    deleteTaskUseCase
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
