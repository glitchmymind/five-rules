package com.fiverules.common.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val deviceId: String? = null,
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String? = null,
    val deviceId: String? = null,
)

@Serializable
data class VerifyEmailRequest(
    val email: String,
    val code: String,
    val deviceId: String? = null,
)

@Serializable
data class ResendVerificationRequest(
    val email: String,
)

@Serializable
data class RefreshRequest(
    val refreshToken: String,
)

@Serializable
data class ForgotPasswordRequest(
    val email: String,
)

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String,
)
