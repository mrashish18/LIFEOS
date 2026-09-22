package com.mrashish18.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskCategory
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import java.time.Instant

import androidx.room.Index

/**
 * Room database entity representing the persisted task record.
 */
@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["status", "createdAtEpochMillis"]),
        Index(value = ["category"])
    ]
)
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    val dueAtEpochMillis: Long?,
    val estimatedMinutes: Int?,
    val category: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long
) {
    fun toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            priority = try { TaskPriority.valueOf(priority) } catch (e: Exception) { TaskPriority.MEDIUM },
            status = try { TaskStatus.valueOf(status) } catch (e: Exception) { TaskStatus.PENDING },
            dueAt = dueAtEpochMillis?.let { Instant.ofEpochMilli(it) },
            estimatedMinutes = estimatedMinutes,
            category = try { TaskCategory.valueOf(category) } catch (e: Exception) { TaskCategory.GENERAL },
            createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
            updatedAt = Instant.ofEpochMilli(updatedAtEpochMillis)
        )
    }

    companion object {
        fun fromDomain(task: Task): TaskEntity {
            return TaskEntity(
                id = task.id,
                title = task.title,
                description = task.description,
                priority = task.priority.name,
                status = task.status.name,
                dueAtEpochMillis = task.dueAt?.toEpochMilli(),
                estimatedMinutes = task.estimatedMinutes,
                category = task.category.name,
                createdAtEpochMillis = task.createdAt.toEpochMilli(),
                updatedAtEpochMillis = task.updatedAt.toEpochMilli()
            )
        }
    }
}
