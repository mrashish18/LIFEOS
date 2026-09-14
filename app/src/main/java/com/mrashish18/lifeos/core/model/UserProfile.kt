package com.mrashish18.lifeos.core.model

import java.time.Instant

/**
 * Domain model representing a user profile within LIFEOS.
 */
data class UserProfile(
    val id: String,
    val displayName: String,
    val createdAt: Instant = Instant.now()
)
