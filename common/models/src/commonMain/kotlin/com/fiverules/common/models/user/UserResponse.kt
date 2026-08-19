package com.fiverules.common.models.user

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val displayName: String?,
    val avatarUrl: String? = null,
    val rating: Int = 1,
    val isBlocked: Boolean = false,
    val emailVerified: Boolean = false,
    val createdAt: String,
)
