package com.mrashish18.lifeos.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrashish18.lifeos.feature.dashboard.DashboardScreen
import com.mrashish18.lifeos.feature.dashboard.DashboardViewModel
import com.mrashish18.lifeos.feature.goals.GoalsScreen
import com.mrashish18.lifeos.feature.intelligence.IntelligenceScreen
import com.mrashish18.lifeos.feature.realitycheck.RealityCheckScreen
import com.mrashish18.lifeos.feature.realitycheck.RealityCheckViewModel
import com.mrashish18.lifeos.feature.resilience.ResilienceScreen
import com.mrashish18.lifeos.feature.resilience.ResilienceViewModel
import com.mrashish18.lifeos.feature.tasks.TasksScreen
import com.mrashish18.lifeos.feature.tasks.TasksViewModel
import com.mrashish18.lifeos.ui.theme.LifeOsIndigo100
import com.mrashish18.lifeos.ui.theme.LifeOsIndigo50
import com.mrashish18.lifeos.ui.theme.LifeOsIndigo700
import com.mrashish18.lifeos.ui.theme.LifeOsSlate200
import com.mrashish18.lifeos.ui.theme.LifeOsSlate700
import com.mrashish18.lifeos.ui.theme.LifeOsSlate900

@Composable
fun LifeOsApp(
    dashboardViewModel: DashboardViewModel,
    tasksViewModel: TasksViewModel,
    realityCheckViewModel: RealityCheckViewModel,
    resilienceViewModel: ResilienceViewModel,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(LifeOsDestination.DASHBOARD) }
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            LifeOsBottomNavigationBar(
                currentDestination = currentDestination,
                onSelectDestination = { currentDestination = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                LifeOsDestination.DASHBOARD -> DashboardScreen(
                    uiState = dashboardUiState,
                    onAcceptRecommendation = { dashboardViewModel.acceptRecommendation(it) },
                    onDismissRecommendation = { dashboardViewModel.dismissRecommendation(it) },
                    onNavigateToTasks = { currentDestination = LifeOsDestination.TASKS }
                )
                LifeOsDestination.TASKS -> TasksScreen(
                    viewModel = tasksViewModel
                )
                LifeOsDestination.GOALS -> GoalsScreen()
                LifeOsDestination.INTELLIGENCE -> IntelligenceScreen()
                LifeOsDestination.REALITY_CHECK -> RealityCheckScreen(
                    viewModel = realityCheckViewModel
                )
                LifeOsDestination.RESILIENCE -> ResilienceScreen(
                    viewModel = resilienceViewModel
                )
            }
        }
    }
}

/**
 * Premium custom LIFEOS bottom navigation bar.
 * Clean, elevated, high contrast, tactile state changes, with no washed-out labels.
 */
@Composable
private fun LifeOsBottomNavigationBar(
    currentDestination: LifeOsDestination,
    onSelectDestination: (LifeOsDestination) -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 2.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LifeOsDestination.values().forEach { destination ->
                    val isSelected = currentDestination == destination
                    LifeOsNavigationTab(
                        destination = destination,
                        isSelected = isSelected,
                        onClick = { onSelectDestination(destination) }
                    )
                }
            }
        }
    }
}

private data class NavDestinationSpec(
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val label: String
)

@Composable
private fun LifeOsNavigationTab(
    destination: LifeOsDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val spec = when (destination) {
        LifeOsDestination.DASHBOARD -> NavDestinationSpec(Icons.Filled.Home, Icons.Outlined.Home, "Home")
        LifeOsDestination.TASKS -> NavDestinationSpec(Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle, "Tasks")
        LifeOsDestination.GOALS -> NavDestinationSpec(Icons.Filled.TrackChanges, Icons.Outlined.TrackChanges, "Goals")
        LifeOsDestination.INTELLIGENCE -> NavDestinationSpec(Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "Intel")
        LifeOsDestination.REALITY_CHECK -> NavDestinationSpec(Icons.AutoMirrored.Filled.FactCheck, Icons.AutoMirrored.Outlined.FactCheck, "Truth")
        LifeOsDestination.RESILIENCE -> NavDestinationSpec(Icons.Filled.Hub, Icons.Outlined.Hub, "Mesh")
    }

    val activeColor = Color(0xFF4338CA)
    val inactiveColor = Color(0xFF64748B)

    Column(
        modifier = Modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .height(26.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (isSelected) activeColor else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) spec.filledIcon else spec.outlinedIcon,
                contentDescription = spec.label,
                tint = if (isSelected) Color.White else inactiveColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = spec.label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) activeColor else inactiveColor,
            maxLines = 1
        )
    }
}
