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
import java.time.Instant

@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
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
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "TASKS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E1B4B),
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = "PERSONAL INTELLIGENCE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4F46E5),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Turn intentions into action.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📋", fontSize = 14.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "···", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                    }
                }
            }

            // User Message Callout (if present)
            uiState.userMessage?.let { msg ->
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = LifeOsIndigo50,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsIndigo700.copy(alpha = 0.2f)),
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
                            color = LifeOsIndigo700,
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
                        targetValue = if (isSelected) Color(0xFF4338CA) else Color(0xFFF1F5F9),
                        label = "taskFilterBg"
                    )
                    val pillTextColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else Color(0xFF64748B),
                        label = "taskFilterText"
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(pillBg)
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
                    items(uiState.filteredTasks, key = { it.id }) { task ->
                        PremiumTaskCard(
                            task = task,
                            onStart = { viewModel.startTask(task.id) },
                            onComplete = { viewModel.completeTask(task.id) },
                            onPostpone = { viewModel.postponeTask(task.id) },
                            onAbandon = { viewModel.abandonTask(task.id) },
                            onEdit = { viewModel.openEditDialog(task) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                }
            }
        }

        // Floating Action Button - Gradient Capsule matching Screen 2
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(LifeOsGradients.primary)
                .clickable { viewModel.openCreateDialog() }
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "New Task",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "New Task",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
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
            }
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
            }
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
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shadowElevation = 1.dp
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
                        TaskPriority.URGENT, TaskPriority.HIGH -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
                        TaskPriority.MEDIUM -> Color(0xFFFEF3C7) to Color(0xFFD97706)
                        TaskPriority.LOW -> Color(0xFFF1F5F9) to Color(0xFF64748B)
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
                        TaskCategory.WORK -> Color(0xFFEEF2FF) to Color(0xFF4F46E5)
                        TaskCategory.PERSONAL -> Color(0xFFF3E8FF) to Color(0xFF9333EA)
                        TaskCategory.HEALTH -> Color(0xFFECFDF5) to Color(0xFF059669)
                        TaskCategory.LEARNING -> Color(0xFFEFF6FF) to Color(0xFF2563EB)
                        TaskCategory.GENERAL -> Color(0xFFF1F5F9) to Color(0xFF475569)
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
                            color = Color(0xFFEFF6FF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2563EB))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "In Progress",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2563EB)
                                )
                            }
                        }
                    }
                    TaskStatus.PENDING -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD97706))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Pending",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                            }
                        }
                    }
                    TaskStatus.COMPLETED -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "✓", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Completed",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A)
                                )
                            }
                        }
                    }
                    else -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = task.status.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
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
                color = Color(0xFF0F172A),
                letterSpacing = (-0.2).sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            // Row 3: Calendar Icon + Today • duration
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📅", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Today • ${task.estimatedMinutes ?: 30} min",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 4: Wide Action Button + 3-Dot Overflow Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (task.status) {
                    TaskStatus.IN_PROGRESS -> {
                        Box(
                            modifier = Modifier
                                .weight(1f)
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
                    }
                    TaskStatus.PENDING, TaskStatus.POSTPONED -> {
                        Box(
                            modifier = Modifier
                                .weight(1f)
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
                    }
                    TaskStatus.COMPLETED, TaskStatus.ABANDONED -> {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
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
                                    color = Color(0xFF475569)
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

/**
 * Clean & Focused New Task Modal matching Screen 3 reference:
 * Top handle, "NEW TASK: What needs your attention?",
 * Task Title *, Description (Optional), Priority (Low, Medium, High, Urgent),
 * Category (Work, Personal, Health, Learning), Estimated Duration, Due Date (Optional),
 * Cancel and + Create Task buttons.
 */
@Composable
private fun TaskEditorBottomSheet(
    isEdit: Boolean,
    initialTask: Task?,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, priority: TaskPriority, category: TaskCategory, estimatedMinutes: Int?) -> Unit
) {
    var taskTitle by remember { mutableStateOf(initialTask?.title ?: "") }
    var taskDescription by remember { mutableStateOf(initialTask?.description ?: "") }
    var selectedPriority by remember { mutableStateOf(initialTask?.priority ?: TaskPriority.MEDIUM) }
    var selectedCategory by remember { mutableStateOf(initialTask?.category ?: TaskCategory.WORK) }
    var estimatedMinutesStr by remember { mutableStateOf(initialTask?.estimatedMinutes?.toString() ?: "30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCBD5E1))
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isEdit) "EDIT TASK" else "NEW TASK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4F46E5),
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (isEdit) "Update Task Details" else "What needs your attention?",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Task Title *
                Column {
                    Text(
                        text = "Task Title *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        placeholder = { Text("e.g. Prepare for exam", fontSize = 12.sp, color = Color(0xFF94A3B8)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Description (Optional)
                Column {
                    Text(
                        text = "Description (Optional)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = taskDescription,
                        onValueChange = { taskDescription = it },
                        placeholder = { Text("Add more context...", fontSize = 12.sp, color = Color(0xFF94A3B8)) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }

                // Priority: Low, Medium, High, Urgent
                Column {
                    Text(
                        text = "Priority",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            TaskPriority.LOW to "Low",
                            TaskPriority.MEDIUM to "Medium",
                            TaskPriority.HIGH to "High",
                            TaskPriority.URGENT to "Urgent"
                        ).forEach { (priority, label) ->
                            val isSelected = selectedPriority == priority
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF4338CA) else Color(0xFFF1F5F9))
                                    .clickable { selectedPriority = priority },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                // Category: Work, Personal, Health, Learning
                Column {
                    Text(
                        text = "Category",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple(TaskCategory.WORK, "Work", "💼"),
                            Triple(TaskCategory.PERSONAL, "Personal", "🛡️"),
                            Triple(TaskCategory.HEALTH, "Health", "🤍"),
                            Triple(TaskCategory.LEARNING, "Learning", "🎓")
                        ).forEach { (category, label, icon) ->
                            val isSelected = selectedCategory == category
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF8FAFC))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF4F46E5) else Color(0xFFE2E8F0),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedCategory = category }
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = icon, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = label,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF4F46E5) else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                // Estimated Duration
                Column {
                    Text(
                        text = "Estimated Duration",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
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
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF4338CA) else Color(0xFFF1F5F9))
                                    .clickable { estimatedMinutesStr = mins.toString() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${mins}m",
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                // Due Date (Optional)
                Column {
                    Text(
                        text = "Due Date (Optional)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4F46E5)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Select date & time",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(text = "📅", fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            LifeOsGradientButton(
                text = if (isEdit) "Save Changes" else "+ Create Task",
                gradient = LifeOsGradients.primary,
                onClick = {
                    if (taskTitle.isNotBlank()) {
                        onSave(
                            taskTitle,
                            taskDescription,
                            selectedPriority,
                            selectedCategory,
                            estimatedMinutesStr.toIntOrNull()
                        )
                    }
                },
                enabled = taskTitle.isNotBlank()
            )
        },
        dismissButton = {
            LifeOsSecondaryButton(
                text = "Cancel",
                onClick = onDismiss
            )
        }
    )
}
