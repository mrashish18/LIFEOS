package com.mrashish18.lifeos.domain.repository

import com.mrashish18.lifeos.core.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing the active [UserProfile].
 */
interface UserProfileRepository {
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun saveUserProfile(profile: UserProfile)
}
