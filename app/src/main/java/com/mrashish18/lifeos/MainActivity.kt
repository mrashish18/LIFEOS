package com.mrashish18.lifeos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.mrashish18.lifeos.core.context.AndroidNetworkContextProvider
import com.mrashish18.lifeos.core.context.DefaultContextEngine
import com.mrashish18.lifeos.core.decision.DeterministicDecisionEngine
import com.mrashish18.lifeos.data.repository.InMemoryTaskRepository
import com.mrashish18.lifeos.domain.usecase.GetDashboardDataUseCase
import com.mrashish18.lifeos.feature.dashboard.DashboardViewModel
import com.mrashish18.lifeos.ui.navigation.LifeOsApp
import com.mrashish18.lifeos.ui.theme.LIFEOSTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Establish foundational dependencies (Hilt-ready constructor injection pattern)
        val networkContextProvider = AndroidNetworkContextProvider(applicationContext)
        val contextEngine = DefaultContextEngine(networkContextProvider = networkContextProvider)
        val decisionEngine = DeterministicDecisionEngine()
        val taskRepository = InMemoryTaskRepository()
        val getDashboardDataUseCase = GetDashboardDataUseCase(
            contextEngine = contextEngine,
            decisionEngine = decisionEngine,
            taskRepository = taskRepository
        )

        val viewModelFactory = DashboardViewModel.Factory(getDashboardDataUseCase)
        val dashboardViewModel = ViewModelProvider(this, viewModelFactory)[DashboardViewModel::class.java]

        setContent {
            LIFEOSTheme {
                LifeOsApp(dashboardViewModel = dashboardViewModel)
            }
        }
    }
}