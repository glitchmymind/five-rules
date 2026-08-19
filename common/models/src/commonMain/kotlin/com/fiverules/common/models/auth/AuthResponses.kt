package com.fiverules.common.models.auth

import com.fiverules.common.models.user.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokensResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: UserResponse,
)

@Serializable
data class RegistrationResponse(
    val email: String,
    val verificationRequired: Boolean = true,
)

@Serializable
data class VerificationResponse(
    val verified: Boolean,
)

@Serializable
data class ApiErrorResponse(
    val error: String,
    val message: String,
)
