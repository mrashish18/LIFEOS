package com.mrashish18.lifeos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mrashish18.lifeos.core.context.AndroidNetworkContextProvider
import com.mrashish18.lifeos.core.context.DefaultContextEngine
import com.mrashish18.lifeos.core.decision.DeterministicDecisionEngine
import com.mrashish18.lifeos.data.local.LifeOsDatabase
import com.mrashish18.lifeos.data.repository.InMemoryTaskRepository
import com.mrashish18.lifeos.data.repository.RoomBehaviorEventRepository
import com.mrashish18.lifeos.data.repository.RoomTaskRepository
import com.mrashish18.lifeos.domain.usecase.CreateTaskUseCase
import com.mrashish18.lifeos.domain.usecase.DeleteTaskUseCase
import com.mrashish18.lifeos.domain.usecase.GetDashboardDataUseCase
import com.mrashish18.lifeos.domain.usecase.TransitionTaskStatusUseCase
import com.mrashish18.lifeos.domain.usecase.UpdateTaskUseCase
import com.mrashish18.lifeos.feature.dashboard.DashboardViewModel
import com.mrashish18.lifeos.feature.tasks.TasksViewModel
import com.mrashish18.lifeos.ui.navigation.LifeOsApp
import com.mrashish18.lifeos.ui.theme.LIFEOSTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Establish Room Persistence
        val database = LifeOsDatabase.getInstance(applicationContext)
        val taskDao = database.taskDao()
        val behaviorEventDao = database.behaviorEventDao()

        val taskRepository = RoomTaskRepository(taskDao)
        val behaviorEventRepository = RoomBehaviorEventRepository(behaviorEventDao)

        // Seed initial tasks if database is brand new
        lifecycleScope.launch {
            if (taskDao.getAllTasks().isEmpty()) {
                InMemoryTaskRepository.defaultSeedTasks().forEach { task ->
                    taskRepository.insertTask(task)
                }
            }
        }

        // 2. Task Use Cases
        val createTaskUseCase = CreateTaskUseCase(taskRepository, behaviorEventRepository)
        val updateTaskUseCase = UpdateTaskUseCase(taskRepository)
        val transitionTaskStatusUseCase = TransitionTaskStatusUseCase(taskRepository, behaviorEventRepository)
        val deleteTaskUseCase = DeleteTaskUseCase(taskRepository)

        // 3. Context & Decision Engines
        val networkContextProvider = AndroidNetworkContextProvider(applicationContext)
        val contextEngine = DefaultContextEngine(networkContextProvider = networkContextProvider)
        val decisionEngine = DeterministicDecisionEngine()

        // 4. Dashboard Use Case
        val getDashboardDataUseCase = GetDashboardDataUseCase(
            contextEngine = contextEngine,
            decisionEngine = decisionEngine,
            taskRepository = taskRepository,
            userBehaviorRepository = behaviorEventRepository
        )

        // 5. ViewModels
        val dashboardFactory = DashboardViewModel.Factory(
            getDashboardDataUseCase,
            transitionTaskStatusUseCase,
            behaviorEventRepository
        )
        val dashboardViewModel = ViewModelProvider(this, dashboardFactory)[DashboardViewModel::class.java]

        val tasksFactory = TasksViewModel.Factory(
            taskRepository,
            createTaskUseCase,
            updateTaskUseCase,
            transitionTaskStatusUseCase,
            deleteTaskUseCase
        )
        val tasksViewModel = ViewModelProvider(this, tasksFactory)[TasksViewModel::class.java]

        setContent {
            LIFEOSTheme {
                LifeOsApp(
                    dashboardViewModel = dashboardViewModel,
                    tasksViewModel = tasksViewModel
                )
            }
        }
    }
}