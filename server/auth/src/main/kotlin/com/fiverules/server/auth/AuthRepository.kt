package com.fiverules.server.auth

import com.fiverules.common.models.auth.AuthTokensResponse
import com.fiverules.common.models.auth.RegisterRequest
import com.fiverules.common.models.auth.RegistrationResponse
import com.fiverules.common.models.auth.VerifyEmailRequest
import com.fiverules.server.core.JwtConfig
import com.fiverules.server.user.StoredUser
import com.fiverules.server.user.UserRepository
import com.fiverules.server.user.toResponse
import java.util.UUID

class AuthRepository(
    private val userRepository: UserRepository,
    private val emailVerificationService: EmailVerificationService,
    private val passwordResetService: PasswordResetService,
) {
    suspend fun login(email: String, password: String, deviceId: String?): LoginOutcome {
        val user = userRepository.verifyPassword(email, password)
            ?: return LoginOutcome.InvalidCredentials
        if (user.isBlocked) return LoginOutcome.InvalidCredentials
        if (!user.emailVerified) {
            runCatching { emailVerificationService.sendCode(user) }
            return LoginOutcome.EmailNotVerified
        }
        val did = deviceId?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        return LoginOutcome.Success(issueTokens(user, did))
    }

    suspend fun refresh(refreshToken: String): AuthTokensResponse? {
        val jwt = JwtConfig.verifyRefreshToken(refreshToken) ?: return null
        val userId = jwt.getClaim(JwtConfig.userIdClaim).asString() ?: return null
        val deviceId = jwt.getClaim(JwtConfig.deviceIdClaim).asString() ?: return null
        val user = userRepository.findById(UUID.fromString(userId)) ?: return null
        if (!user.emailVerified || user.isBlocked) return null
        return issueTokens(user, deviceId)
    }

    suspend fun register(request: RegisterRequest): RegisterOutcome {
        val email = request.email.trim().lowercase()
        if (!EMAIL_REGEX.matches(email) || !isValidPassword(request.password)) {
            return RegisterOutcome.InvalidInput
        }
        val existing = userRepository.findByEmail(email)
        if (existing != null) {
            if (existing.emailVerified) return RegisterOutcome.EmailTaken
            val updated = userRepository.updateProfileForRegistration(
                id = UUID.fromString(existing.id),
                password = request.password,
                displayName = request.displayName,
            ) ?: return RegisterOutcome.Error
            emailVerificationService.sendCode(updated)
            return RegisterOutcome.PendingVerification(
                RegistrationResponse(email = updated.email, verificationRequired = true),
            )
        }
        val created = userRepository.create(
            email = email,
            password = request.password,
            displayName = request.displayName,
        )
        val stored = userRepository.findByEmail(created.email) ?: return RegisterOutcome.Error
        emailVerificationService.sendCode(stored)
        return RegisterOutcome.Success(RegistrationResponse(email = stored.email))
    }

    suspend fun verifyEmail(request: VerifyEmailRequest): AuthTokensResponse? {
        val user = emailVerificationService.verify(request.email, request.code) ?: return null
        val deviceId = request.deviceId?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        return issueTokens(user, deviceId)
    }

    suspend fun resendVerification(email: String): Boolean =
        emailVerificationService.resend(email)

    suspend fun requestPasswordReset(email: String): Boolean =
        passwordResetService.requestReset(email)

    suspend fun resetPassword(email: String, code: String, newPassword: String): ResetPasswordOutcome {
        val normalized = email.trim().lowercase()
        if (!EMAIL_REGEX.matches(normalized) || !isValidPassword(newPassword)) {
            return ResetPasswordOutcome.InvalidInput
        }
        val success = passwordResetService.resetPassword(normalized, code, newPassword)
        return if (success) ResetPasswordOutcome.Success else ResetPasswordOutcome.InvalidCode
    }

    private fun issueTokens(user: StoredUser, deviceId: String): AuthTokensResponse {
        val access = JwtConfig.generateAccessToken(user.id)
        val refresh = JwtConfig.generateRefreshToken(user.id, deviceId)
        return AuthTokensResponse(
            accessToken = access,
            refreshToken = refresh,
            expiresIn = JwtConfig.accessTokenTtlSeconds,
            user = user.toResponse(),
        )
    }

    private fun isValidPassword(password: String): Boolean =
        password.length >= 8 &&
            password.any(Char::isUpperCase) &&
            password.any(Char::isLowerCase) &&
            password.any(Char::isDigit)

    private companion object {
        val EMAIL_REGEX = Regex("""^[^@\s]+@[^@\s]+\.[^@\s]+$""")
    }
}

sealed class RegisterOutcome {
    data class Success(val response: RegistrationResponse) : RegisterOutcome()
    data class PendingVerification(val response: RegistrationResponse) : RegisterOutcome()
    data object EmailTaken : RegisterOutcome()
    data object InvalidInput : RegisterOutcome()
    data object Error : RegisterOutcome()
}

sealed class LoginOutcome {
    data class Success(val tokens: AuthTokensResponse) : LoginOutcome()
    data object InvalidCredentials : LoginOutcome()
    data object EmailNotVerified : LoginOutcome()
}

sealed class ResetPasswordOutcome {
    data object Success : ResetPasswordOutcome()
    data object InvalidCode : ResetPasswordOutcome()
    data object InvalidInput : ResetPasswordOutcome()
}
