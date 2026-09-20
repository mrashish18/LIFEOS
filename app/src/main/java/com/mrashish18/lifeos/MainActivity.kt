package com.mrashish18.lifeos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mrashish18.lifeos.core.common.DefaultDispatcherProvider
import com.mrashish18.lifeos.core.context.AndroidNetworkContextProvider
import com.mrashish18.lifeos.core.context.DefaultContextEngine
import com.mrashish18.lifeos.core.decision.DeterministicDecisionEngine
import com.mrashish18.lifeos.data.local.LifeOsDatabase
import com.mrashish18.lifeos.data.repository.InMemoryTaskRepository
import com.mrashish18.lifeos.data.repository.RoomBehaviorEventRepository
import com.mrashish18.lifeos.data.repository.RoomInvestigationRepository
import com.mrashish18.lifeos.data.repository.RoomTaskRepository
import com.mrashish18.lifeos.domain.usecase.CreateTaskUseCase
import com.mrashish18.lifeos.domain.usecase.DeleteTaskUseCase
import com.mrashish18.lifeos.domain.usecase.GetDashboardDataUseCase
import com.mrashish18.lifeos.domain.usecase.TransitionTaskStatusUseCase
import com.mrashish18.lifeos.domain.usecase.UpdateTaskUseCase
import com.mrashish18.lifeos.feature.dashboard.DashboardViewModel
import com.mrashish18.lifeos.core.realitycheck.RealityCheckEngine
import com.mrashish18.lifeos.core.resilience.RescueMeshEngine
import com.mrashish18.lifeos.core.resilience.transport.LocalStoreAndForwardTransport
import com.mrashish18.lifeos.core.resilience.transport.NetworkGatewayTransport
import com.mrashish18.lifeos.data.repository.DeterministicEvidenceRepository
import com.mrashish18.lifeos.data.repository.RoomEmergencyMessageRepository
import com.mrashish18.lifeos.domain.usecase.CreateEmergencyMessageUseCase
import com.mrashish18.lifeos.domain.usecase.GetEmergencyQueueUseCase
import com.mrashish18.lifeos.domain.usecase.PerformRealityCheckUseCase
import com.mrashish18.lifeos.domain.usecase.RelayEmergencyMessageUseCase
import com.mrashish18.lifeos.domain.usecase.SyncEmergencyQueueUseCase
import com.mrashish18.lifeos.feature.realitycheck.RealityCheckViewModel
import com.mrashish18.lifeos.feature.resilience.ResilienceViewModel
import com.mrashish18.lifeos.feature.tasks.TasksViewModel
import com.mrashish18.lifeos.ui.navigation.LifeOsApp
import com.mrashish18.lifeos.ui.theme.LIFEOSTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dispatcherProvider = DefaultDispatcherProvider()

        // 1. Establish Room Persistence
        val database = LifeOsDatabase.getInstance(applicationContext)
        val taskDao = database.taskDao()
        val behaviorEventDao = database.behaviorEventDao()

        val taskRepository = RoomTaskRepository(taskDao)
        val behaviorEventRepository = RoomBehaviorEventRepository(
            behaviorEventDao = behaviorEventDao,
            dispatcherProvider = dispatcherProvider
        )

        // Seed initial tasks if database is brand new (off-main-thread IO)
        lifecycleScope.launch(dispatcherProvider.io) {
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
            userBehaviorRepository = behaviorEventRepository,
            dispatcherProvider = dispatcherProvider
        )

        // 5. RealityCheck Pipeline
        val evidenceRepository = DeterministicEvidenceRepository()
        val realityCheckEngine = RealityCheckEngine(evidenceRepository)
        val investigationDao = database.investigationDao()
        val investigationRepository = RoomInvestigationRepository(investigationDao, dispatcherProvider)
        val performRealityCheckUseCase = PerformRealityCheckUseCase(
            realityCheckEngine = realityCheckEngine,
            behaviorEventRepository = behaviorEventRepository,
            investigationRepository = investigationRepository
        )

        // 6. RescueMesh Resilience Pipeline
        val emergencyMessageDao = database.emergencyMessageDao()
        val emergencyRepository = RoomEmergencyMessageRepository(emergencyMessageDao)
        val rescueMeshEngine = RescueMeshEngine()
        val localTransport = LocalStoreAndForwardTransport(emergencyRepository, rescueMeshEngine)
        val networkGatewayTransport = NetworkGatewayTransport(emergencyRepository, rescueMeshEngine)

        // Seed initial emergency messages if database is brand new
        lifecycleScope.launch(dispatcherProvider.io) {
            if (emergencyMessageDao.getAllMessages().isEmpty()) {
                com.mrashish18.lifeos.core.model.EmergencyMessage.defaultSeedMessages().forEach { msg ->
                    emergencyRepository.insertMessage(msg)
                }
            }
        }

        val createEmergencyMessageUseCase = CreateEmergencyMessageUseCase(
            emergencyRepository = emergencyRepository,
            engine = rescueMeshEngine,
            behaviorEventRepository = behaviorEventRepository,
            networkTransport = networkGatewayTransport,
            localTransport = localTransport
        )
        val getEmergencyQueueUseCase = GetEmergencyQueueUseCase(emergencyRepository)
        val relayEmergencyMessageUseCase = RelayEmergencyMessageUseCase(
            emergencyRepository = emergencyRepository,
            engine = rescueMeshEngine,
            behaviorEventRepository = behaviorEventRepository
        )
        val syncEmergencyQueueUseCase = SyncEmergencyQueueUseCase(
            emergencyRepository = emergencyRepository,
            networkTransport = networkGatewayTransport,
            engine = rescueMeshEngine,
            behaviorEventRepository = behaviorEventRepository
        )

        // 7. ViewModels
        val learningLoop = com.mrashish18.lifeos.core.decision.DefaultLearningLoop(behaviorEventRepository)
        val dashboardFactory = DashboardViewModel.Factory(
            getDashboardDataUseCase = getDashboardDataUseCase,
            transitionTaskStatusUseCase = transitionTaskStatusUseCase,
            behaviorEventRepository = behaviorEventRepository,
            learningLoop = learningLoop,
            investigationRepository = investigationRepository,
            emergencyRepository = emergencyRepository
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

        val realityCheckFactory = RealityCheckViewModel.Factory(
            performRealityCheckUseCase,
            investigationRepository
        )
        val realityCheckViewModel = ViewModelProvider(this, realityCheckFactory)[RealityCheckViewModel::class.java]

        val resilienceFactory = ResilienceViewModel.Factory(
            contextEngine = contextEngine,
            createEmergencyMessageUseCase = createEmergencyMessageUseCase,
            getEmergencyQueueUseCase = getEmergencyQueueUseCase,
            relayEmergencyMessageUseCase = relayEmergencyMessageUseCase,
            syncEmergencyQueueUseCase = syncEmergencyQueueUseCase
        )
        val resilienceViewModel = ViewModelProvider(this, resilienceFactory)[ResilienceViewModel::class.java]

        setContent {
            LIFEOSTheme {
                LifeOsApp(
                    dashboardViewModel = dashboardViewModel,
                    tasksViewModel = tasksViewModel,
                    realityCheckViewModel = realityCheckViewModel,
                    resilienceViewModel = resilienceViewModel
                )
            }
        }
    }
}