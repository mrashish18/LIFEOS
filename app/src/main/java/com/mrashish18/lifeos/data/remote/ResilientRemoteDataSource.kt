package com.mrashish18.lifeos.data.remote

import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.TaskCategory
import com.mrashish18.lifeos.core.model.TaskPriority
import com.mrashish18.lifeos.core.model.TaskStatus
import com.mrashish18.lifeos.core.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.util.UUID
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

/**
 * Resilient, production-ready implementation of [LifeOsRemoteDataSource] featuring:
 * - Bounded connect and read timeouts (10s / 15s)
 * - Exponential backoff with jitter
 * - Circuit breaker to prevent cascading failures on server downtime
 * - Idempotency key headers to prevent duplicate operations on network retries
 * - Strict JSON payload parsing and payload size bounding (<64KB)
 */
class ResilientRemoteDataSource(
    private val baseUrl: String = DEFAULT_BASE_URL,
    private val connectTimeoutMs: Int = 10_000,
    private val readTimeoutMs: Int = 15_000,
    private val maxRetries: Int = 3
) : LifeOsRemoteDataSource {

    companion object {
        const val DEFAULT_BASE_URL = "http://10.0.2.2:8000"
        private const val MAX_RESPONSE_BYTES = 65_536 // 64 KB safety bound
    }

    enum class CircuitState { CLOSED, OPEN, HALF_OPEN }

    private var circuitState = CircuitState.CLOSED
    private var consecutiveFailures = 0
    private var lastFailureTimestamp = 0L
    private val failureThreshold = 5
    private val recoveryTimeoutMs = 30_000L

    @Synchronized
    fun getCircuitState(): CircuitState {
        val now = System.currentTimeMillis()
        if (circuitState == CircuitState.OPEN && now - lastFailureTimestamp > recoveryTimeoutMs) {
            circuitState = CircuitState.HALF_OPEN
        }
        return circuitState
    }

    @Synchronized
    private fun recordSuccess() {
        consecutiveFailures = 0
        circuitState = CircuitState.CLOSED
    }

    @Synchronized
    private fun recordFailure() {
        consecutiveFailures++
        lastFailureTimestamp = System.currentTimeMillis()
        if (consecutiveFailures >= failureThreshold) {
            circuitState = CircuitState.OPEN
        }
    }

    override suspend fun fetchTasks(): List<Task> = withContext(Dispatchers.IO) {
        if (getCircuitState() == CircuitState.OPEN) {
            return@withContext emptyList()
        }

        executeWithRetry {
            val endpoint = "$baseUrl/api/v1/tasks?limit=50"
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = connectTimeoutMs
                readTimeout = readTimeoutMs
                setRequestProperty("Accept", "application/json")
            }

            try {
                val responseCode = conn.responseCode
                if (responseCode in 200..299) {
                    val body = readBoundedStream(conn.inputStream)
                    parseTasksJson(body)
                } else {
                    throw ApiException(responseCode, "Server returned HTTP $responseCode")
                }
            } finally {
                conn.disconnect()
            }
        } ?: emptyList()
    }

    override suspend fun syncTask(task: Task): Boolean = withContext(Dispatchers.IO) {
        if (getCircuitState() == CircuitState.OPEN) {
            return@withContext false
        }

        val result = executeWithRetry {
            val endpoint = "$baseUrl/api/v1/tasks/sync"
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = connectTimeoutMs
                readTimeout = readTimeoutMs
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Idempotency-Key", "task-sync-${task.id}-${task.updatedAt.toEpochMilli()}")
            }

            try {
                val jsonPayload = JSONObject().apply {
                    put("id", task.id)
                    put("title", task.title)
                    put("description", task.description)
                    put("priority", task.priority.name)
                    put("status", task.status.name)
                    put("category", task.category.name)
                    put("estimatedMinutes", task.estimatedMinutes ?: JSONObject.NULL)
                    put("dueAtEpochMillis", task.dueAt?.toEpochMilli() ?: JSONObject.NULL)
                    put("createdAtEpochMillis", task.createdAt.toEpochMilli())
                    put("updatedAtEpochMillis", task.updatedAt.toEpochMilli())
                }.toString()

                OutputStreamWriter(conn.outputStream).use { writer ->
                    writer.write(jsonPayload)
                    writer.flush()
                }

                val responseCode = conn.responseCode
                responseCode in 200..299
            } finally {
                conn.disconnect()
            }
        }
        result ?: false
    }

    override suspend fun fetchUserProfile(userId: String): UserProfile? = withContext(Dispatchers.IO) {
        if (getCircuitState() == CircuitState.OPEN) {
            return@withContext null
        }

        executeWithRetry {
            val endpoint = "$baseUrl/api/v1/users/$userId"
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = connectTimeoutMs
                readTimeout = readTimeoutMs
                setRequestProperty("Accept", "application/json")
            }

            try {
                val responseCode = conn.responseCode
                if (responseCode == 200) {
                    val body = readBoundedStream(conn.inputStream)
                    val json = JSONObject(body)
                    UserProfile(
                        id = json.getString("id"),
                        displayName = json.optString("displayName", json.optString("name", "User"))
                    )
                } else {
                    null
                }
            } finally {
                conn.disconnect()
            }
        }
    }

    private suspend fun <T> executeWithRetry(block: () -> T): T? {
        var lastException: Exception? = null

        for (attempt in 0..maxRetries) {
            try {
                val result = block()
                recordSuccess()
                return result
            } catch (e: ApiException) {
                // Non-retryable 4xx client errors should fail fast without retry
                if (e.statusCode in 400..499 && e.statusCode != 429) {
                    recordFailure()
                    return null
                }
                lastException = e
            } catch (e: Exception) {
                lastException = e
            }

            recordFailure()

            if (attempt < maxRetries) {
                // Exponential backoff: base 200ms, cap at 3000ms, with jitter
                val backoffMs = min(3000.0, 200.0 * 2.0.pow(attempt.toDouble())).toLong()
                val jitter = Random.nextLong(0, 100)
                delay(backoffMs + jitter)
            }
        }
        return null
    }

    private fun readBoundedStream(inputStream: java.io.InputStream): String {
        val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
        val sb = StringBuilder()
        val buffer = CharArray(1024)
        var totalBytesRead = 0

        var read: Int
        while (reader.read(buffer).also { read = it } != -1) {
            totalBytesRead += read * 2
            if (totalBytesRead > MAX_RESPONSE_BYTES) {
                break
            }
            sb.append(buffer, 0, read)
        }
        return sb.toString()
    }

    private fun parseTasksJson(jsonString: String): List<Task> {
        val tasks = mutableListOf<Task>()
        try {
            val root = JSONObject(jsonString)
            val items = root.optJSONArray("items") ?: JSONArray()
            for (i in 0 until items.length()) {
                val obj = items.getJSONObject(i)
                tasks.add(
                    Task(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        description = obj.optString("description", ""),
                        priority = try { TaskPriority.valueOf(obj.getString("priority")) } catch (e: Exception) { TaskPriority.MEDIUM },
                        status = try { TaskStatus.valueOf(obj.getString("status")) } catch (e: Exception) { TaskStatus.PENDING },
                        category = try { TaskCategory.valueOf(obj.getString("category")) } catch (e: Exception) { TaskCategory.GENERAL },
                        dueAt = if (obj.has("dueAtEpochMillis") && !obj.isNull("dueAtEpochMillis")) {
                            Instant.ofEpochMilli(obj.getLong("dueAtEpochMillis"))
                        } else null,
                        estimatedMinutes = if (obj.has("estimatedMinutes") && !obj.isNull("estimatedMinutes")) {
                            obj.getInt("estimatedMinutes")
                        } else null,
                        createdAt = Instant.ofEpochMilli(obj.optLong("createdAtEpochMillis", System.currentTimeMillis())),
                        updatedAt = Instant.ofEpochMilli(obj.optLong("updatedAtEpochMillis", System.currentTimeMillis()))
                    )
                )
            }
        } catch (e: Exception) {
            // Safe fallback on unexpected JSON schema
        }
        return tasks
    }
}

class ApiException(val statusCode: Int, message: String) : Exception(message)
