package com.mrashish18.lifeos.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
                        unreadNotificationCount = notificationUiState.unreadCount,
                        onOpenNotifications = { notificationViewModel.openNotificationCenter() },
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
 * Flagship Hackathon-Grade LIFEOS bottom navigation dock.
 * Features:
 * - Fluid curved top geometry with luminous dynamic gradient accent boundary.
 * - Per-pillar signature brand gradients (Indigo, Emerald, Amber, Violet, Sky, Rose).
 * - Multi-layered glowing pill with subtle translucent border and spring scale bounce.
 * - Illuminated micro-indicator bar beneath active tab.
 * - High-contrast tactile typography and WCAG AA accessibility compliance.
 */
@Composable
private fun LifeOsBottomNavigationBar(
    currentDestination: LifeOsDestination,
    isDarkMode: Boolean = false,
    onSelectDestination: (LifeOsDestination) -> Unit
) {
    val navBarColor = if (isDarkMode) {
        Color(0xFF0F172A).copy(alpha = 0.98f)
    } else {
        Color(0xFFFFFFFF).copy(alpha = 0.98f)
    }
    val topBorderGradient = if (isDarkMode) {
        Brush.horizontalGradient(
            listOf(
                Color(0xFF6366F1).copy(alpha = 0.30f),
                Color(0xFF38BDF8).copy(alpha = 0.70f),
                Color(0xFFA855F7).copy(alpha = 0.70f),
                Color(0xFFF43F5E).copy(alpha = 0.40f)
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                Color(0xFF4F46E5).copy(alpha = 0.25f),
                Color(0xFF0284C7).copy(alpha = 0.60f),
                Color(0xFF7C3AED).copy(alpha = 0.60f),
                Color(0xFFE11D48).copy(alpha = 0.30f)
            )
        )
    }
    val surfaceBorderColor = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0)

    Surface(
        color = navBarColor,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        tonalElevation = 6.dp,
        shadowElevation = 14.dp,
        border = BorderStroke(1.dp, surfaceBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            // Luminous top accent hairline
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(topBorderGradient)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
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
    val label: String,
    val brandGradient: List<Color>,
    val lightAccentColor: Color,
    val darkAccentColor: Color
)

@Composable
private fun LifeOsNavigationTab(
    destination: LifeOsDestination,
    isSelected: Boolean,
    isDarkMode: Boolean = false,
    onClick: () -> Unit
) {
    val spec = when (destination) {
        LifeOsDestination.DASHBOARD -> NavDestinationSpec(
            filledIcon = Icons.Filled.Home,
            outlinedIcon = Icons.Outlined.Home,
            label = "Home",
            brandGradient = listOf(Color(0xFF4338CA), Color(0xFF6366F1)),
            lightAccentColor = Color(0xFF4338CA),
            darkAccentColor = Color(0xFF818CF8)
        )
        LifeOsDestination.TASKS -> NavDestinationSpec(
            filledIcon = Icons.Filled.CheckCircle,
            outlinedIcon = Icons.Outlined.CheckCircle,
            label = "Tasks",
            brandGradient = listOf(Color(0xFF059669), Color(0xFF10B981)),
            lightAccentColor = Color(0xFF047857),
            darkAccentColor = Color(0xFF34D399)
        )
        LifeOsDestination.GOALS -> NavDestinationSpec(
            filledIcon = Icons.Filled.TrackChanges,
            outlinedIcon = Icons.Outlined.TrackChanges,
            label = "Goals",
            brandGradient = listOf(Color(0xFFD97706), Color(0xFFF59E0B)),
            lightAccentColor = Color(0xFFB45309),
            darkAccentColor = Color(0xFFFBBF24)
        )
        LifeOsDestination.INTELLIGENCE -> NavDestinationSpec(
            filledIcon = Icons.Filled.AutoAwesome,
            outlinedIcon = Icons.Outlined.AutoAwesome,
            label = "Intel",
            brandGradient = listOf(Color(0xFF7C3AED), Color(0xFFA855F7)),
            lightAccentColor = Color(0xFF6D28D9),
            darkAccentColor = Color(0xFFC084FC)
        )
        LifeOsDestination.REALITY_CHECK -> NavDestinationSpec(
            filledIcon = Icons.AutoMirrored.Filled.FactCheck,
            outlinedIcon = Icons.AutoMirrored.Outlined.FactCheck,
            label = "Truth",
            brandGradient = listOf(Color(0xFF0284C7), Color(0xFF38BDF8)),
            lightAccentColor = Color(0xFF0369A1),
            darkAccentColor = Color(0xFF38BDF8)
        )
        LifeOsDestination.RESILIENCE -> NavDestinationSpec(
            filledIcon = Icons.Filled.Hub,
            outlinedIcon = Icons.Outlined.Hub,
            label = "Mesh",
            brandGradient = listOf(Color(0xFFE11D48), Color(0xFFF43F5E)),
            lightAccentColor = Color(0xFFBE123C),
            darkAccentColor = Color(0xFFFB7185)
        )
    }

    val activeTextColor = if (isDarkMode) spec.darkAccentColor else spec.lightAccentColor
    val inactiveColor = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.10f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "navTabScale"
    )

    val animatedIndicatorWidth by animateDpAsState(
        targetValue = if (isSelected) 14.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "navIndicatorWidth"
    )

    Column(
        modifier = Modifier
            .defaultMinSize(minWidth = 50.dp, minHeight = 52.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .padding(horizontal = 2.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Active glowing pill or unselected minimal container
        Box(
            modifier = Modifier
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isSelected) Brush.linearGradient(spec.brandGradient)
                    else SolidColor(Color.Transparent)
                )
                .then(
                    if (isSelected) {
                        Modifier.border(
                            BorderStroke(1.dp, Color.White.copy(alpha = 0.40f)),
                            RoundedCornerShape(14.dp)
                        )
                    } else Modifier
                )
                .padding(
                    horizontal = if (isSelected) 13.dp else 8.dp,
                    vertical = 4.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) spec.filledIcon else spec.outlinedIcon,
                contentDescription = spec.label,
                tint = if (isSelected) Color.White else inactiveColor,
                modifier = Modifier
                    .size(18.dp)
                    .scale(animatedScale)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = spec.label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) activeTextColor else inactiveColor,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Illuminated micro-indicator bar
        Box(
            modifier = Modifier
                .width(animatedIndicatorWidth)
                .height(2.5.dp)
                .clip(RoundedCornerShape(1.25.dp))
                .background(
                    if (isSelected) Brush.horizontalGradient(spec.brandGradient)
                    else SolidColor(Color.Transparent)
                )
        )
    }
}
