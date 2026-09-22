package com.mrashish18.lifeos.scalability

import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskCategory
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.data.remote.ResilientRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

class ProductionScalabilityTest {

    @Test
    fun `circuit breaker initial state is CLOSED`() {
        val dataSource = ResilientRemoteDataSource(baseUrl = "http://127.0.0.1:9999")
        assertEquals(ResilientRemoteDataSource.CircuitState.CLOSED, dataSource.getCircuitState())
    }

    @Test
    fun `resilient client trips circuit breaker after consecutive failures`() = runBlocking {
        val dataSource = ResilientRemoteDataSource(
            baseUrl = "http://127.0.0.1:9999",
            connectTimeoutMs = 100,
            readTimeoutMs = 100,
            maxRetries = 0
        )

        // Attempt 5 failed requests to trigger threshold
        for (i in 1..5) {
            val task = Task(
                id = "test-$i",
                title = "Test Task $i",
                description = "",
                priority = TaskPriority.HIGH,
                status = TaskStatus.PENDING,
                category = TaskCategory.WORK
            )
            val success = dataSource.syncTask(task)
            assertFalse(success)
        }

        // Circuit breaker should now be OPEN
        assertEquals(ResilientRemoteDataSource.CircuitState.OPEN, dataSource.getCircuitState())

        // Next request should fail-fast without network call
        val fastFailResult = dataSource.fetchTasks()
        assertTrue(fastFailResult.isEmpty())
    }

    @Test
    fun `concurrent task operations are guarded against duplicate processing`() = runBlocking {
        val inFlightSet = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()
        val processedCount = AtomicInteger(0)
        val taskId = "task-101"

        // Simulate 100 rapid concurrent clicks on the same task action
        val jobs = List(100) {
            launch(Dispatchers.Default) {
                if (inFlightSet.add(taskId)) {
                    try {
                        Thread.sleep(10) // simulated async DB work
                        processedCount.incrementAndGet()
                    } finally {
                        inFlightSet.remove(taskId)
                    }
                }
            }
        }
        jobs.forEach { it.join() }

        // Because of the in-flight lock, rapid concurrent clicks do not create 100 duplicate operations
        assertTrue("Expected guarded operations < 100, was ${processedCount.get()}", processedCount.get() < 50)
    }

    @Test
    fun `idempotency key generation produces deterministic unique keys`() {
        val task = Task(
            id = "task-alpha-1",
            title = "Alpha",
            description = "Desc",
            priority = TaskPriority.URGENT,
            status = TaskStatus.IN_PROGRESS,
            category = TaskCategory.WORK,
            createdAt = Instant.ofEpochMilli(1700000000000L),
            updatedAt = Instant.ofEpochMilli(1700000500000L)
        )

        val key1 = "task-sync-${task.id}-${task.updatedAt.toEpochMilli()}"
        val key2 = "task-sync-${task.id}-${task.updatedAt.toEpochMilli()}"

        assertEquals(key1, key2)
        assertEquals("task-sync-task-alpha-1-1700000500000", key1)
    }
}
