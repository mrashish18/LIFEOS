package com.mrashish18.lifeos.feature.tasks

import androidx.compose.foundation.background
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
import com.mrashish18.lifeos.ui.components.LifeOsCard
import com.mrashish18.lifeos.ui.components.LifeOsCategoryBadge
import com.mrashish18.lifeos.ui.components.LifeOsEmptyState
import com.mrashish18.lifeos.ui.components.LifeOsEyebrow
import com.mrashish18.lifeos.ui.components.LifeOsPrimaryButton
import com.mrashish18.lifeos.ui.components.LifeOsPriorityBadge
import com.mrashish18.lifeos.ui.components.LifeOsSecondaryButton
import com.mrashish18.lifeos.ui.components.LifeOsSectionHeader
import com.mrashish18.lifeos.ui.components.LifeOsStatusChip
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

            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    LifeOsEyebrow(text = "PERSONAL INTELLIGENCE", color = LifeOsIndigo700)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Action Queue",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "${uiState.filteredTasks.size} tasks",
                    style = MaterialTheme.typography.labelMedium,
                    color = LifeOsSlate600,
                    fontWeight = FontWeight.SemiBold
                )
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

            // Filter Chips Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskFilter.values().forEach { filter ->
                    val isSelected = uiState.selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setFilter(filter) },
                        label = {
                            Text(
                                text = filter.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LifeOsIndigo50,
                            selectedLabelColor = LifeOsIndigo700
                        )
                    )
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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
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

        // Floating Action Button - Positioned above bottom bar with safe padding
        ExtendedFloatingActionButton(
            onClick = { viewModel.openCreateDialog() },
            containerColor = LifeOsIndigo700,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
            icon = {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "New Task",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            },
            text = {
                Text(
                    text = "New Task",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 20.dp)
        )
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
 * Premium Task Card with strong visual hierarchy:
 * Priority, Status, Bold Title, Quiet Description, Duration.
 * Clear primary action + quiet secondary options row (no 6 giant buttons).
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
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (task.status == TaskStatus.IN_PROGRESS) LifeOsIndigo700.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        shadowElevation = if (task.status == TaskStatus.IN_PROGRESS) 2.dp else 0.5.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row 1: Priority + Category + Duration + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LifeOsPriorityBadge(priority = task.priority)
                    LifeOsCategoryBadge(category = task.category)
                    task.estimatedMinutes?.let { min ->
                        Text(
                            text = "· ${min}m",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                LifeOsStatusChip(status = task.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Title
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Row 3: Description (if any)
            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            // Row 4: Obvious Primary Action + Quiet Secondary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Obvious Primary Action Button
                Box {
                    when (task.status) {
                        TaskStatus.PENDING -> {
                            Button(
                                onClick = onStart,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LifeOsIndigo700,
                                    contentColor = Color.White
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Text("▶  Start Focus", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        TaskStatus.IN_PROGRESS -> {
                            Button(
                                onClick = onComplete,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LifeOsGreen700,
                                    contentColor = Color.White
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Text("✓  Complete", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        TaskStatus.POSTPONED -> {
                            Button(
                                onClick = onStart,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LifeOsIndigo700,
                                    contentColor = Color.White
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Text("▶  Resume", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        TaskStatus.COMPLETED, TaskStatus.ABANDONED -> {
                            Text(
                                text = if (task.status == TaskStatus.COMPLETED) "Archived" else "Dismissed",
                                style = MaterialTheme.typography.labelMedium,
                                color = LifeOsSlate600,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Quiet Secondary Actions Row with high-contrast text
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (task.status == TaskStatus.PENDING || task.status == TaskStatus.IN_PROGRESS) {
                        TextButton(
                            onClick = onPostpone,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Postpone", style = MaterialTheme.typography.labelSmall, color = LifeOsSlate700, fontWeight = FontWeight.Medium)
                        }
                        TextButton(
                            onClick = onAbandon,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Abandon", style = MaterialTheme.typography.labelSmall, color = LifeOsSlate700, fontWeight = FontWeight.Medium)
                        }
                    }

                    TextButton(
                        onClick = onEdit,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Edit", style = MaterialTheme.typography.labelSmall, color = LifeOsIndigo700, fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = onDelete,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Delete", style = MaterialTheme.typography.labelSmall, color = LifeOsRed700, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

/**
 * Polished Create / Edit Task Modal Experience.
 * Formulated with "NEW TASK: What needs your attention?"
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
    var estimatedMinutesStr by remember { mutableStateOf(initialTask?.estimatedMinutes?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Column {
                LifeOsEyebrow(text = if (isEdit) "EDIT TASK" else "NEW TASK")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isEdit) "Update Task Details" else "What needs your attention?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task Title *") },
                    placeholder = { Text("e.g. Implement OAuth Authentication") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Context & Objectives (Optional)") },
                    placeholder = { Text("What defines success for this task?") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Column {
                    Text(
                        text = "PRIORITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TaskPriority.values().forEach { priority ->
                            FilterChip(
                                selected = selectedPriority == priority,
                                onClick = { selectedPriority = priority },
                                label = { Text(priority.name, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = "CATEGORY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TaskCategory.values().forEach { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category.name, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = estimatedMinutesStr,
                    onValueChange = { estimatedMinutesStr = it },
                    label = { Text("Estimated Duration (minutes)") },
                    placeholder = { Text("e.g. 45") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            LifeOsPrimaryButton(
                text = if (isEdit) "Save Changes" else "+ Create Task",
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
