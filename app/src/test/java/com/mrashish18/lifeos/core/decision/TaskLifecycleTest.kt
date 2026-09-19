package com.mrashish18.lifeos.core.decision

import com.mrashish18.lifeos.core.model.BehaviorEvent
import com.mrashish18.lifeos.core.model.BehaviorEventType
import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.data.repository.InMemoryTaskRepository
import com.mrashish18.lifeos.domain.repository.BehaviorEventRepository
import com.mrashish18.lifeos.domain.usecase.CreateTaskUseCase
import com.mrashish18.lifeos.domain.usecase.TransitionTaskStatusUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TaskLifecycleTest {

    private lateinit var taskRepository: InMemoryTaskRepository
    private lateinit var recordedEvents: MutableList<BehaviorEvent>
    private lateinit var behaviorEventRepository: BehaviorEventRepository

    private lateinit var createTaskUseCase: CreateTaskUseCase
    private lateinit var transitionTaskStatusUseCase: TransitionTaskStatusUseCase

    @Before
    fun setup() {
        taskRepository = InMemoryTaskRepository(initialTasks = emptyList())
        recordedEvents = mutableListOf()
        behaviorEventRepository = object : BehaviorEventRepository {
            override suspend fun recordEvent(event: BehaviorEvent) {
                recordedEvents.add(event)
            }

            override fun observeRecentEvents(limit: Int): Flow<List<BehaviorEvent>> {
                return MutableStateFlow(recordedEvents.takeLast(limit))
            }

            override suspend fun getAllEvents(): List<BehaviorEvent> = recordedEvents
        }

        createTaskUseCase = CreateTaskUseCase(taskRepository, behaviorEventRepository)
        transitionTaskStatusUseCase = TransitionTaskStatusUseCase(taskRepository, behaviorEventRepository)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createTask_rejectsBlankTitle() {
        runBlocking {
            createTaskUseCase(
                title = "   ",
                description = "Invalid task"
            )
        }
    }

    @Test
    fun createTask_insertsTaskAndLogsBehaviorEvent() {
        runBlocking {
            val task = createTaskUseCase(
                title = "Write Unit Tests",
                description = "Test lifecycle transitions",
                priority = TaskPriority.HIGH,
                estimatedMinutes = 45
            )

            assertNotNull(task.id)
            assertEquals("Write Unit Tests", task.title)
            assertEquals(TaskStatus.PENDING, task.status)

            // Verify task in repository
            val storedTasks = taskRepository.getTasks().first()
            assertEquals(1, storedTasks.size)
            assertEquals("Write Unit Tests", storedTasks.first().title)

            // Verify event logged
            assertEquals(1, recordedEvents.size)
            val event = recordedEvents.first()
            assertEquals(BehaviorEventType.TASK_CREATED, event.type)
            assertEquals(task.id, event.metadata["taskId"])
            assertEquals("Write Unit Tests", event.metadata["title"])
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun createTask_blankTitle_throwsException() {
        runBlocking {
            createTaskUseCase(title = "   ")
        }
    }

    @Test
    fun transitionTaskStatus_recordsCorrespondingEvents() {
        runBlocking {
            val task = createTaskUseCase(title = "Feature Implementation")

            // 1. Start task
            val inProgressTask = transitionTaskStatusUseCase(task.id, TaskStatus.IN_PROGRESS)
            assertEquals(TaskStatus.IN_PROGRESS, inProgressTask?.status)
            assertTrue(recordedEvents.any { it.type == BehaviorEventType.TASK_STARTED && it.metadata["taskId"] == task.id })

            // 2. Postpone task
            val postponedTask = transitionTaskStatusUseCase(task.id, TaskStatus.POSTPONED)
            assertEquals(TaskStatus.POSTPONED, postponedTask?.status)
            assertTrue(recordedEvents.any { it.type == BehaviorEventType.TASK_POSTPONED && it.metadata["taskId"] == task.id })

            // 3. Complete task
            val completedTask = transitionTaskStatusUseCase(task.id, TaskStatus.COMPLETED)
            assertEquals(TaskStatus.COMPLETED, completedTask?.status)
            val completionEvent = recordedEvents.find { it.type == BehaviorEventType.TASK_COMPLETED && it.metadata["taskId"] == task.id }
            assertNotNull(completionEvent)
            assertNotNull(completionEvent?.metadata?.get("durationMinutes"))
        }
    }
}
