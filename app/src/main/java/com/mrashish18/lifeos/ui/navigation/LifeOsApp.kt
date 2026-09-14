package com.mrashish18.lifeos.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrashish18.lifeos.feature.dashboard.DashboardScreen
import com.mrashish18.lifeos.feature.dashboard.DashboardViewModel
import com.mrashish18.lifeos.feature.goals.GoalsScreen
import com.mrashish18.lifeos.feature.intelligence.IntelligenceScreen
import com.mrashish18.lifeos.feature.realitycheck.RealityCheckScreen
import com.mrashish18.lifeos.feature.resilience.ResilienceScreen
import com.mrashish18.lifeos.feature.tasks.TasksScreen
import com.mrashish18.lifeos.feature.tasks.TasksViewModel

@Composable
fun LifeOsApp(
    dashboardViewModel: DashboardViewModel,
    tasksViewModel: TasksViewModel,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(LifeOsDestination.DASHBOARD) }
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                LifeOsDestination.values().forEach { destination ->
                    val selected = currentDestination == destination
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentDestination = destination },
                        icon = {
                            DestinationIconBadge(
                                destination = destination,
                                isSelected = selected
                            )
                        },
                        label = {
                            Text(
                                text = when (destination) {
                                    LifeOsDestination.DASHBOARD -> "Home"
                                    LifeOsDestination.TASKS -> "Tasks"
                                    LifeOsDestination.GOALS -> "Goals"
                                    LifeOsDestination.INTELLIGENCE -> "Intel"
                                    LifeOsDestination.REALITY_CHECK -> "Truth"
                                    LifeOsDestination.RESILIENCE -> "Mesh"
                                },
                                maxLines = 1,
                                fontSize = 10.sp
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentDestination) {
            LifeOsDestination.DASHBOARD -> DashboardScreen(
                uiState = dashboardUiState,
                onAcceptRecommendation = { dashboardViewModel.acceptRecommendation(it) },
                onDismissRecommendation = { dashboardViewModel.dismissRecommendation(it) },
                onNavigateToTasks = { currentDestination = LifeOsDestination.TASKS },
                modifier = Modifier.padding(innerPadding)
            )
            LifeOsDestination.TASKS -> TasksScreen(
                viewModel = tasksViewModel,
                modifier = Modifier.padding(innerPadding)
            )
            LifeOsDestination.GOALS -> GoalsScreen(modifier = Modifier.padding(innerPadding))
            LifeOsDestination.INTELLIGENCE -> IntelligenceScreen(modifier = Modifier.padding(innerPadding))
            LifeOsDestination.REALITY_CHECK -> RealityCheckScreen(modifier = Modifier.padding(innerPadding))
            LifeOsDestination.RESILIENCE -> ResilienceScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
private fun DestinationIconBadge(
    destination: LifeOsDestination,
    isSelected: Boolean
) {
    val symbol = when (destination) {
        LifeOsDestination.DASHBOARD -> "⌂"
        LifeOsDestination.TASKS -> "✓"
        LifeOsDestination.GOALS -> "◎"
        LifeOsDestination.INTELLIGENCE -> "◈"
        LifeOsDestination.REALITY_CHECK -> "⚖"
        LifeOsDestination.RESILIENCE -> "⛨"
    }

    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
