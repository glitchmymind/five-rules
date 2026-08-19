package com.fiverules.server.user

data class StoredUser(
    val id: String,
    val email: String,
    val passwordHash: String?,
    val displayName: String?,
    val avatarUrl: String?,
    val rating: Int,
    val isBlocked: Boolean,
    val emailVerified: Boolean,
    val createdAtIso: String,
)
