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

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import com.mrashish18.lifeos.feature.notifications.NotificationViewModel
import com.mrashish18.lifeos.feature.settings.DataStorageCounts
import com.mrashish18.lifeos.feature.settings.SettingsModalContainer
import com.mrashish18.lifeos.feature.settings.SettingsViewModel
import com.mrashish18.lifeos.ui.components.LifeOsDrawerContent
import com.mrashish18.lifeos.ui.components.NotificationCenterBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeOsApp(
    dashboardViewModel: DashboardViewModel,
    tasksViewModel: TasksViewModel,
    realityCheckViewModel: RealityCheckViewModel,
    resilienceViewModel: ResilienceViewModel,
    notificationViewModel: NotificationViewModel,
    settingsViewModel: SettingsViewModel,
    isDarkMode: Boolean = false,
    versionName: String = "1.0",
    versionCode: Int = 1,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(LifeOsDestination.DASHBOARD) }
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()
    val notificationUiState by notificationViewModel.uiState.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val userSettings by settingsViewModel.settings.collectAsState()
    val activeModal by settingsViewModel.activeModal.collectAsState()
    val isResetConfirmationVisible by settingsViewModel.isResetConfirmationVisible.collectAsState()
    val resetSuccessMessage by settingsViewModel.resetSuccessMessage.collectAsState()

    val tasksCount by settingsViewModel.tasksCount.collectAsState()
    val behaviorEventsCount by settingsViewModel.behaviorEventsCount.collectAsState()
    val investigationsCount by settingsViewModel.investigationsCount.collectAsState()
    val emergencyMessagesCount by settingsViewModel.emergencyMessagesCount.collectAsState()
    val notificationsCount by settingsViewModel.notificationsCount.collectAsState()

    val counts = DataStorageCounts(
        tasksCount = tasksCount,
        goalsCount = 5,
        behaviorEventsCount = behaviorEventsCount,
        investigationsCount = investigationsCount,
        emergencyMessagesCount = emergencyMessagesCount,
        notificationsCount = notificationsCount
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            LifeOsDrawerContent(
                currentDestination = currentDestination,
                isDarkMode = isDarkMode,
                versionName = versionName,
                versionCode = versionCode,
                systemStatus = dashboardUiState.systemStatus,
                onSelectDestination = { currentDestination = it },
                onOpenModal = { settingsViewModel.openModal(it) },
                onOpenNotifications = { notificationViewModel.openNotificationCenter() },
                onCloseDrawer = { coroutineScope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                LifeOsBottomNavigationBar(
                    currentDestination = currentDestination,
                    isDarkMode = isDarkMode,
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
                        onNavigateToTasks = { currentDestination = LifeOsDestination.TASKS },
                        onNavigateToTruth = { currentDestination = LifeOsDestination.REALITY_CHECK },
                        onNavigateToResilience = { currentDestination = LifeOsDestination.RESILIENCE },
                        unreadNotificationCount = notificationUiState.unreadCount,
                        onOpenNotifications = { notificationViewModel.openNotificationCenter() },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        isDarkMode = isDarkMode
                    )
                    LifeOsDestination.TASKS -> TasksScreen(
                        viewModel = tasksViewModel,
                        unreadNotificationCount = notificationUiState.unreadCount,
                        onOpenNotifications = { notificationViewModel.openNotificationCenter() },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        isDarkMode = isDarkMode
                    )
                    LifeOsDestination.GOALS -> GoalsScreen(
                        unreadNotificationCount = notificationUiState.unreadCount,
                        onOpenNotifications = { notificationViewModel.openNotificationCenter() },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        isDarkMode = isDarkMode
                    )
                    LifeOsDestination.INTELLIGENCE -> IntelligenceScreen(
                        onNavigateToPersonal = { currentDestination = LifeOsDestination.TASKS },
                        onNavigateToTruth = { currentDestination = LifeOsDestination.REALITY_CHECK },
                        onNavigateToResilience = { currentDestination = LifeOsDestination.RESILIENCE },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        isDarkMode = isDarkMode
                    )
                    LifeOsDestination.REALITY_CHECK -> RealityCheckScreen(
                        viewModel = realityCheckViewModel,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        isDarkMode = isDarkMode
                    )
                    LifeOsDestination.RESILIENCE -> ResilienceScreen(
                        viewModel = resilienceViewModel,
                        unreadNotificationCount = notificationUiState.unreadCount,
                        onOpenNotifications = { notificationViewModel.openNotificationCenter() },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        isDarkMode = isDarkMode
                    )
                }

                // Notification Center Modal Bottom Sheet
                if (notificationUiState.isSheetVisible) {
                    NotificationCenterBottomSheet(
                        notifications = notificationUiState.filteredNotifications,
                        unreadCount = notificationUiState.unreadCount,
                        selectedCategory = notificationUiState.selectedCategory,
                        isDarkMode = isDarkMode,
                        onSelectCategory = { notificationViewModel.setCategoryFilter(it) },
                        onMarkAsRead = { notificationViewModel.markAsRead(it) },
                        onMarkAllAsRead = { notificationViewModel.markAllAsRead() },
                        onNavigateTo = { destination ->
                            currentDestination = destination
                            notificationViewModel.closeNotificationCenter()
                        },
                        onDismiss = { notificationViewModel.closeNotificationCenter() }
                    )
                }

                // Settings Modals Container
                SettingsModalContainer(
                    activeModal = activeModal,
                    userSettings = userSettings,
                    isDarkMode = isDarkMode,
                    versionName = versionName,
                    versionCode = versionCode,
                    counts = counts,
                    isResetConfirmationVisible = isResetConfirmationVisible,
                    resetSuccessMessage = resetSuccessMessage,
                    onClose = { settingsViewModel.closeModal() },
                    onSetThemeMode = { settingsViewModel.setThemeMode(it) },
                    onSetAutoDayNight = { settingsViewModel.setAutoDayNight(it) },
                    onSetInAppNotifications = { settingsViewModel.setInAppNotifications(it) },
                    onToggleCategory = { cat, enabled -> settingsViewModel.toggleNotificationCategory(cat, enabled) },
                    onOpenNotificationCenter = { notificationViewModel.openNotificationCenter() },
                    onShowResetConfirmation = { settingsViewModel.showResetConfirmation(it) },
                    onConfirmReset = { settingsViewModel.performResetData() },
                    onClearResetSuccessMessage = { settingsViewModel.clearResetSuccessMessage() }
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
    isDarkMode: Boolean = false,
    onSelectDestination: (LifeOsDestination) -> Unit
) {
    val navBarColor = if (isDarkMode) Color(0xFF111827).copy(alpha = 0.96f) else Color.White.copy(alpha = 0.95f)
    val borderColor = if (isDarkMode) Color(0xFF1F2937) else Color(0xFFE2E8F0)

    Surface(
        color = navBarColor,
        tonalElevation = 2.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LifeOsDestination.values().forEach { destination ->
                    val isSelected = currentDestination == destination
                    LifeOsNavigationTab(
                        destination = destination,
                        isSelected = isSelected,
                        isDarkMode = isDarkMode,
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
    isDarkMode: Boolean = false,
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

    val activeColor = if (destination == LifeOsDestination.RESILIENCE) {
        if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF2563EB)
    } else {
        if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4338CA)
    }
    val inactiveColor = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
    val activeTextColor = if (isDarkMode) Color(0xFFE0E7FF) else (if (destination == LifeOsDestination.RESILIENCE) Color(0xFF2563EB) else Color(0xFF4338CA))

    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else Color.Transparent,
        label = "navTabBg"
    )
    val animatedIconTint by animateColorAsState(
        targetValue = if (isSelected) Color.White else inactiveColor,
        label = "navTabIconTint"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) activeTextColor else inactiveColor,
        label = "navTabTextColor"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "navTabScale"
    )

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
                .background(animatedBgColor)
                .padding(horizontal = 14.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) spec.filledIcon else spec.outlinedIcon,
                contentDescription = spec.label,
                tint = animatedIconTint,
                modifier = Modifier
                    .size(18.dp)
                    .scale(animatedScale)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = spec.label,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            color = animatedTextColor,
            maxLines = 1
        )
    }
}
