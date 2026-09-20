package com.mrashish18.lifeos.feature.tasks

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskCategory
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.ui.components.*
import com.mrashish18.lifeos.ui.theme.*
import com.mrashish18.lifeos.ui.components.LifeOsNotificationBell
import java.time.Instant

@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    unreadNotificationCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    isDarkMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Section Header matching Screen 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LifeOsMenuButton(
                        onClick = onOpenDrawer,
                        isDarkMode = isDarkMode
                    )
                    Column {
                        Text(
                            text = "Tasks",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            text = "Actions today. Objectives tomorrow.",
                            fontSize = 11.5.sp,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }

                LifeOsNotificationBell(
                    unreadCount = unreadNotificationCount,
                    onClick = onOpenNotifications,
                    isDarkMode = isDarkMode
                )
            }

            // User Message Callout (if present)
            uiState.userMessage?.let { msg ->
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = if (isDarkMode) Color(0xFF1E293B) else LifeOsIndigo50,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDarkMode) Color(0xFF4338CA) else LifeOsIndigo700.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkMode) Color(0xFF818CF8) else LifeOsIndigo700,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearUserMessage() }) {
                            Text("Dismiss", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Chips Strip - 4 Primary Filters matching Screen 2: All, Pending, In Progress, Done
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    TaskFilter.ALL to "All",
                    TaskFilter.PENDING to "Pending",
                    TaskFilter.IN_PROGRESS to "In Progress",
                    TaskFilter.COMPLETED to "Done"
                ).forEach { (filter, label) ->
                    val isSelected = uiState.selectedFilter == filter
                    val pillBg by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF4338CA) else if (isDarkMode) Color(0xFF172033) else Color(0xFFF1F5F9),
                        label = "taskFilterBg"
                    )
                    val pillTextColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF64748B),
                        label = "taskFilterText"
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(pillBg)
                            .then(
                                if (isDarkMode && !isSelected) Modifier.border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                                else Modifier
                            )
                            .clickable { viewModel.setFilter(filter) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = pillTextColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                }
            } else if (uiState.allTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LifeOsEmptyState(
                        iconSymbol = "📋",
                        title = "No tasks yet",
                        description = "Create your first task to begin building your LIFEOS behavior profile.",
                        actionButton = {
                            LifeOsPrimaryButton(
                                text = "+ Create Task",
                                onClick = { viewModel.openCreateDialog() }
                            )
                        }
                    )
                }
            } else if (uiState.filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LifeOsEmptyState(
                        iconSymbol = "✦",
                        title = "YOUR DAY IS CLEAR",
                        description = "No tasks found in '${uiState.selectedFilter.label}'. Create a task to set focus or let the Decision Engine schedule your next priority.",
                        actionButton = {
                            LifeOsPrimaryButton(
                                text = "+ Create Task",
                                onClick = { viewModel.openCreateDialog() }
                            )
                        }
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = "TODAY • ${uiState.filteredTasks.size} TASKS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(uiState.filteredTasks, key = { it.id }) { task ->
                        PremiumTaskCard(
                            task = task,
                            onStart = { viewModel.startTask(task.id) },
                            onComplete = { viewModel.completeTask(task.id) },
                            onPostpone = { viewModel.postponeTask(task.id) },
                            onAbandon = { viewModel.abandonTask(task.id) },
                            onEdit = { viewModel.openEditDialog(task) },
                            onDelete = { viewModel.deleteTask(task.id) },
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }
        }

        // Floating Action Button - Circular Gradient FAB matching Screen 2
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 20.dp)
                .size(52.dp)
                .clip(CircleShape)
                .background(LifeOsGradients.primary)
                .clickable { viewModel.openCreateDialog() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "New Task",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }

    if (uiState.isCreateDialogOpen) {
        TaskEditorBottomSheet(
            isEdit = false,
            initialTask = null,
            onDismiss = { viewModel.closeCreateDialog() },
            onSave = { title, desc, priority, category, estMinutes ->
                viewModel.createTask(
                    title = title,
                    description = desc,
                    priority = priority,
                    category = category,
                    estimatedMinutes = estMinutes,
                    dueAt = null
                )
            },
            isDarkMode = isDarkMode
        )
    }

    uiState.editingTask?.let { taskToEdit ->
        TaskEditorBottomSheet(
            isEdit = true,
            initialTask = taskToEdit,
            onDismiss = { viewModel.closeEditDialog() },
            onSave = { title, desc, priority, category, estMinutes ->
                viewModel.updateTask(
                    taskToEdit.copy(
                        title = title,
                        description = desc,
                        priority = priority,
                        category = category,
                        estimatedMinutes = estMinutes,
                        updatedAt = Instant.now()
                    )
                )
            },
            isDarkMode = isDarkMode
        )
    }
}

/**
 * Compact Task Card matching Screen 2 reference:
 * Priority pill, Category pill, Status badge on right.
 * Bold title, Today • duration.
 * Wide action button + 3-dot overflow menu.
 */
@Composable
private fun PremiumTaskCard(
    task: Task,
    onStart: () -> Unit,
    onComplete: () -> Unit,
    onPostpone: () -> Unit,
    onAbandon: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isDarkMode: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (isDarkMode) Color(0xFF111827) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkMode) Color(0xFF334155) else Color(0xFFF1F5F9)
        ),
        shadowElevation = if (isDarkMode) 0.dp else 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Priority + Category + Status on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Priority Pill
                    val (prioBg, prioColor) = when (task.priority) {
                        TaskPriority.URGENT, TaskPriority.HIGH -> {
                            if (isDarkMode) Color(0xFF7F1D1D).copy(alpha = 0.5f) to Color(0xFFFCA5A5)
                            else Color(0xFFFEE2E2) to Color(0xFFDC2626)
                        }
                        TaskPriority.MEDIUM -> {
                            if (isDarkMode) Color(0xFF78350F).copy(alpha = 0.5f) to Color(0xFFFCD34D)
                            else Color(0xFFFEF3C7) to Color(0xFFD97706)
                        }
                        TaskPriority.LOW -> {
                            if (isDarkMode) Color(0xFF1E293B) to Color(0xFF94A3B8)
                            else Color(0xFFF1F5F9) to Color(0xFF64748B)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(prioBg)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.priority.name,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = prioColor
                        )
                    }

                    // Category Pill
                    val (catBg, catColor) = when (task.category) {
                        TaskCategory.WORK -> {
                            if (isDarkMode) Color(0xFF312E81).copy(alpha = 0.5f) to Color(0xFFA5B4FC)
                            else Color(0xFFEEF2FF) to Color(0xFF4F46E5)
                        }
                        TaskCategory.PERSONAL -> {
                            if (isDarkMode) Color(0xFF581C87).copy(alpha = 0.5f) to Color(0xFFD8B4FE)
                            else Color(0xFFF3E8FF) to Color(0xFF9333EA)
                        }
                        TaskCategory.HEALTH -> {
                            if (isDarkMode) Color(0xFF064E3B).copy(alpha = 0.5f) to Color(0xFF6EE7B7)
                            else Color(0xFFECFDF5) to Color(0xFF059669)
                        }
                        TaskCategory.LEARNING -> {
                            if (isDarkMode) Color(0xFF1E3A8A).copy(alpha = 0.5f) to Color(0xFF93C5FD)
                            else Color(0xFFEFF6FF) to Color(0xFF2563EB)
                        }
                        TaskCategory.GENERAL -> {
                            if (isDarkMode) Color(0xFF1E293B) to Color(0xFFCBD5E1)
                            else Color(0xFFF1F5F9) to Color(0xFF475569)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(catBg)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.category.name,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = catColor
                        )
                    }
                }

                // Status Badge
                when (task.status) {
                    TaskStatus.IN_PROGRESS -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDarkMode) Color(0xFF1E3A8A).copy(alpha = 0.4f) else Color(0xFFEFF6FF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(if (isDarkMode) Color(0xFF60A5FA) else Color(0xFF2563EB))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "In Progress",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFF93C5FD) else Color(0xFF2563EB)
                                )
                            }
                        }
                    }
                    TaskStatus.PENDING -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDarkMode) Color(0xFF78350F).copy(alpha = 0.4f) else Color(0xFFFEF3C7)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(if (isDarkMode) Color(0xFFFBBF24) else Color(0xFFD97706))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Pending",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFFFCD34D) else Color(0xFFD97706)
                                )
                            }
                        }
                    }
                    TaskStatus.COMPLETED -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDarkMode) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFFDCFCE7)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✓",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFF4ADE80) else Color(0xFF16A34A)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Completed",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFF4ADE80) else Color(0xFF16A34A)
                                )
                            }
                        }
                    }
                    else -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = task.status.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF64748B),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Title
            Text(
                text = task.title,
                fontSize = 16.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                letterSpacing = (-0.2).sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            // Row 3: Calendar Icon + Duration + Strategic Goal Alignment
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📅", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Today • ${task.estimatedMinutes ?: 30} min",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }

                val goalTag = when (task.category) {
                    TaskCategory.WORK -> "Goal: Productivity"
                    TaskCategory.HEALTH -> "Goal: Circadian"
                    TaskCategory.LEARNING -> "Goal: Deep Work"
                    else -> null
                }
                if (goalTag != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = goalTag,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 4: Wide Action Button + Quick Postpone + 3-Dot Overflow Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (task.status) {
                    TaskStatus.IN_PROGRESS -> {
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(LifeOsGradients.primary)
                                .clickable(onClick = onComplete),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓  Complete",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(0.9f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF8FAFC))
                                .border(
                                    1.dp,
                                    if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable(onClick = onPostpone),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⏳ Postpone",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569)
                            )
                        }
                    }
                    TaskStatus.PENDING, TaskStatus.POSTPONED -> {
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(LifeOsGradients.primary)
                                .clickable(onClick = onStart),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "▶  Start",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(0.9f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF8FAFC))
                                .border(
                                    1.dp,
                                    if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable(onClick = onPostpone),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⏳ Postpone",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569)
                            )
                        }
                    }
                    TaskStatus.COMPLETED, TaskStatus.ABANDONED -> {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                                .clickable(onClick = onEdit),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "👁", fontSize = 12.sp)
                                Text(
                                    text = "View Details",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                // Overflow Menu
                LifeOsOverflowMenu(
                    onPostpone = onPostpone,
                    onAbandon = onAbandon,
                    onEdit = onEdit,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun TaskEditorBottomSheet(
    isEdit: Boolean,
    initialTask: Task?,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, priority: TaskPriority, category: TaskCategory, estimatedMinutes: Int?) -> Unit,
    isDarkMode: Boolean = false
) {
    var taskTitle by remember { mutableStateOf(initialTask?.title ?: "") }
    var taskDescription by remember { mutableStateOf(initialTask?.description ?: "") }
    var selectedPriority by remember { mutableStateOf(initialTask?.priority ?: TaskPriority.MEDIUM) }
    var selectedCategory by remember { mutableStateOf(initialTask?.category ?: TaskCategory.WORK) }
    var estimatedMinutesStr by remember { mutableStateOf(initialTask?.estimatedMinutes?.toString() ?: "25") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = if (isDarkMode) Color(0xFF111827) else Color.White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isEdit) "Edit Task" else "Create Task",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "A small action today. A bigger tomorrow.",
                        fontSize = 11.5.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 13.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Task Title
                Column {
                    Text(
                        text = "Task Title",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        placeholder = { Text("What would you like to accomplish?", fontSize = 12.sp, color = Color(0xFF94A3B8)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // PRIORITY (Reference Image 2 Screen 3)
                Column {
                    Text(
                        text = "PRIORITY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple(TaskPriority.LOW, "● Low", Color(0xFF16A34A)),
                            Triple(TaskPriority.MEDIUM, "Medium", Color(0xFFEA580C)),
                            Triple(TaskPriority.HIGH, "● High", Color(0xFFE11D48)),
                            Triple(TaskPriority.URGENT, "Critical", Color(0xFF9333EA))
                        ).forEach { (priority, label, accentColor) ->
                            val isSelected = selectedPriority == priority
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(17.dp))
                                    .background(
                                        if (isSelected) accentColor
                                        else if (isDarkMode) Color(0xFF1E293B)
                                        else Color(0xFFF8FAFC)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) accentColor
                                        else if (isDarkMode) Color(0xFF334155)
                                        else Color(0xFFE2E8F0),
                                        RoundedCornerShape(17.dp)
                                    )
                                    .clickable { selectedPriority = priority },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else accentColor
                                )
                            }
                        }
                    }
                }

                // DURATION (Reference Image 2 Screen 3)
                Column {
                    Text(
                        text = "DURATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(15, 25, 45, 60).forEach { mins ->
                            val isSelected = estimatedMinutesStr == mins.toString()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(17.dp))
                                    .background(
                                        if (isSelected) Color(0xFF4338CA)
                                        else if (isDarkMode) Color(0xFF1E293B)
                                        else Color(0xFFF8FAFC)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF4338CA)
                                        else if (isDarkMode) Color(0xFF334155)
                                        else Color(0xFFE2E8F0),
                                        RoundedCornerShape(17.dp)
                                    )
                                    .clickable { estimatedMinutesStr = mins.toString() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${mins}m",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                // CATEGORY (Reference Image 2 Screen 3)
                Column {
                    Text(
                        text = "CATEGORY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple(TaskCategory.WORK, "Work", "💼"),
                            Triple(TaskCategory.HEALTH, "Health", "❤️"),
                            Triple(TaskCategory.LEARNING, "Learning", "📖"),
                            Triple(TaskCategory.PERSONAL, "Life", "🏠")
                        ).forEach { (category, label, icon) ->
                            val isSelected = selectedCategory == category
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) {
                                            if (isDarkMode) Color(0xFF312E81) else Color(0xFFEEF2FF)
                                        } else {
                                            if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF8FAFC)
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF4F46E5) else if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedCategory = category }
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) Color(0xFF4F46E5)
                                            else if (isDarkMode) Color(0xFF334155)
                                            else Color(0xFFE2E8F0)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = icon, fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = label,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) {
                                        if (isDarkMode) Color(0xFFA5B4FC) else Color(0xFF4F46E5)
                                    } else {
                                        if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF64748B)
                                    }
                                )
                            }
                        }
                    }
                }

                // NOTES (Reference Image 2 Screen 3)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NOTES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "${taskDescription.length}/200",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = taskDescription,
                        onValueChange = { if (it.length <= 200) taskDescription = it },
                        placeholder = { Text("Add optional notes...", fontSize = 12.sp, color = Color(0xFF94A3B8)) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (taskTitle.isNotBlank()) LifeOsGradients.primary
                        else androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8))
                        )
                    )
                    .clickable(enabled = taskTitle.isNotBlank()) {
                        onSave(
                            taskTitle,
                            taskDescription,
                            selectedPriority,
                            selectedCategory,
                            estimatedMinutesStr.toIntOrNull()
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEdit) "Save Changes" else "Create Task",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = null
    )
}
