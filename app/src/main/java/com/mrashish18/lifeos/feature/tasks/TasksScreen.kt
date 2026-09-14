package com.mrashish18.lifeos.feature.tasks

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskCategory
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import java.time.Instant

@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Personal Intelligence: Tasks",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            uiState.userMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = msg, style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = { viewModel.clearUserMessage() }) {
                            Text("Dismiss", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskFilter.values().forEach { filter ->
                    FilterChip(
                        selected = uiState.selectedFilter == filter,
                        onClick = { viewModel.setFilter(filter) },
                        label = { Text(filter.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No tasks found in '${uiState.selectedFilter.label}'",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.openCreateDialog() }) {
                            Text("Create Task")
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.filteredTasks, key = { it.id }) { task ->
                        TaskItemCard(
                            task = task,
                            onStart = { viewModel.startTask(task.id) },
                            onComplete = { viewModel.completeTask(task.id) },
                            onPostpone = { viewModel.postponeTask(task.id) },
                            onAbandon = { viewModel.abandonTask(task.id) },
                            onEdit = { viewModel.openEditDialog(task) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (uiState.isCreateDialogOpen) {
        TaskEditorDialog(
            title = "Create Task",
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
        TaskEditorDialog(
            title = "Edit Task",
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

@Composable
private fun TaskItemCard(
    task: Task,
    onStart: () -> Unit,
    onComplete: () -> Unit,
    onPostpone: () -> Unit,
    onAbandon: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = when (task.priority) {
                        TaskPriority.URGENT -> MaterialTheme.colorScheme.errorContainer
                        TaskPriority.HIGH -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = task.priority.name,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status: ${task.status.name}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "Cat: ${task.category.name}",
                    style = MaterialTheme.typography.labelSmall
                )
                task.estimatedMinutes?.let { minutes ->
                    Text(text = "•", style = MaterialTheme.typography.labelSmall)
                    Text(text = "$minutes min", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (task.status) {
                    TaskStatus.PENDING -> {
                        Button(onClick = onStart) { Text("Start") }
                        OutlinedButton(onClick = onPostpone) { Text("Postpone") }
                        OutlinedButton(onClick = onAbandon) { Text("Abandon") }
                        TextButton(onClick = onEdit) { Text("Edit") }
                        TextButton(onClick = onDelete) { Text("Delete") }
                    }
                    TaskStatus.IN_PROGRESS -> {
                        Button(onClick = onComplete) { Text("Complete") }
                        OutlinedButton(onClick = onPostpone) { Text("Postpone") }
                        OutlinedButton(onClick = onAbandon) { Text("Abandon") }
                        TextButton(onClick = onEdit) { Text("Edit") }
                    }
                    TaskStatus.POSTPONED -> {
                        Button(onClick = onStart) { Text("Resume") }
                        OutlinedButton(onClick = onAbandon) { Text("Abandon") }
                        TextButton(onClick = onEdit) { Text("Edit") }
                        TextButton(onClick = onDelete) { Text("Delete") }
                    }
                    TaskStatus.COMPLETED, TaskStatus.ABANDONED -> {
                        TextButton(onClick = onDelete) { Text("Delete") }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskEditorDialog(
    title: String,
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
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Priority:", style = MaterialTheme.typography.labelMedium)
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
                            label = { Text(priority.name) }
                        )
                    }
                }

                Text("Category:", style = MaterialTheme.typography.labelMedium)
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
                            label = { Text(category.name) }
                        )
                    }
                }

                OutlinedTextField(
                    value = estimatedMinutesStr,
                    onValueChange = { estimatedMinutesStr = it },
                    label = { Text("Estimated Duration (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
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
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
