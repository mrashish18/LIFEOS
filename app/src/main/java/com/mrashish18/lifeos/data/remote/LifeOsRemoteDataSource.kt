package com.mrashish18.lifeos.data.remote

import com.mrashish18.lifeos.core.model.Task
import com.mrashish18.lifeos.core.model.UserProfile

/**
 * Interface representing a Retrofit-ready remote API service abstraction for cloud synchronization.
 */
interface LifeOsRemoteDataSource {
    suspend fun fetchTasks(): List<Task>
    suspend fun syncTask(task: Task): Boolean
    suspend fun fetchUserProfile(userId: String): UserProfile?
}
