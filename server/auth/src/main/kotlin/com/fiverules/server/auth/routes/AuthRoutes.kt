package com.fiverules.server.auth.routes

import com.fiverules.common.models.auth.ApiErrorResponse
import com.fiverules.common.models.auth.ForgotPasswordRequest
import com.fiverules.common.models.auth.LoginRequest
import com.fiverules.common.models.auth.RefreshRequest
import com.fiverules.common.models.auth.RegisterRequest
import com.fiverules.common.models.auth.ResendVerificationRequest
import com.fiverules.common.models.auth.ResetPasswordRequest
import com.fiverules.common.models.auth.VerificationResponse
import com.fiverules.common.models.auth.VerifyEmailRequest
import com.fiverules.server.auth.AuthRepository
import com.fiverules.server.auth.LoginOutcome
import com.fiverules.server.auth.RegisterOutcome
import com.fiverules.server.auth.ResetPasswordOutcome
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    route("/auth") {
        val authRepository by inject<AuthRepository>()

        post("/login") {
            val body = call.receive<LoginRequest>()
            when (
                val outcome = authRepository.login(
                    email = body.email,
                    password = body.password,
                    deviceId = body.deviceId,
                )
            ) {
                is LoginOutcome.Success -> call.respond(HttpStatusCode.OK, outcome.tokens)
                LoginOutcome.EmailNotVerified -> call.respond(
                    HttpStatusCode.Forbidden,
                    ApiErrorResponse("email_not_verified", "Confirm your email address"),
                )
                LoginOutcome.InvalidCredentials -> call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiErrorResponse("invalid_credentials", "Invalid email or password"),
                )
            }
        }

        post("/refresh") {
            val body = call.receive<RefreshRequest>()
            val tokens = authRepository.refresh(body.refreshToken)
            if (tokens == null) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiErrorResponse("invalid_refresh_token", "Invalid or expired refresh token"),
                )
            } else {
                call.respond(HttpStatusCode.OK, tokens)
            }
        }

        post("/register") {
            val body = call.receive<RegisterRequest>()
            val outcome = runCatching { authRepository.register(body) }.getOrElse { error ->
                call.respond(
                    HttpStatusCode.BadGateway,
                    ApiErrorResponse("email_send_failed", error.message ?: "Could not send verification email"),
                )
                return@post
            }
            when (outcome) {
                is RegisterOutcome.Success -> call.respond(HttpStatusCode.Created, outcome.response)
                is RegisterOutcome.PendingVerification -> call.respond(HttpStatusCode.Created, outcome.response)
                RegisterOutcome.EmailTaken -> call.respond(
                    HttpStatusCode.Conflict,
                    ApiErrorResponse("email_taken", "User with this email already exists"),
                )
                RegisterOutcome.InvalidInput -> call.respond(
                    HttpStatusCode.BadRequest,
                    ApiErrorResponse("invalid_input", "Invalid email or password too short"),
                )
                RegisterOutcome.Error -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiErrorResponse("server_error", "Could not complete registration"),
                )
            }
        }

        post("/verify-email") {
            val body = call.receive<VerifyEmailRequest>()
            val tokens = authRepository.verifyEmail(body)
            if (tokens == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiErrorResponse("invalid_verification_code", "Invalid or expired code"),
                )
            } else {
                call.respond(HttpStatusCode.OK, tokens)
            }
        }

        post("/resend-verification") {
            val body = call.receive<ResendVerificationRequest>()
            runCatching { authRepository.resendVerification(body.email) }
                .onFailure { error ->
                    call.respond(
                        HttpStatusCode.BadGateway,
                        ApiErrorResponse("email_send_failed", error.message ?: "Could not send verification email"),
                    )
                    return@post
                }
            call.respond(HttpStatusCode.Accepted, VerificationResponse(verified = false))
        }

        post("/forgot-password") {
            val body = call.receive<ForgotPasswordRequest>()
            runCatching { authRepository.requestPasswordReset(body.email) }
                .onFailure { error ->
                    call.respond(
                        HttpStatusCode.BadGateway,
                        ApiErrorResponse("email_send_failed", error.message ?: "Could not send reset email"),
                    )
                    return@post
                }
            call.respond(HttpStatusCode.Accepted, VerificationResponse(verified = false))
        }

        post("/reset-password") {
            val body = call.receive<ResetPasswordRequest>()
            when (
                val outcome = authRepository.resetPassword(
                    email = body.email,
                    code = body.code,
                    newPassword = body.newPassword,
                )
            ) {
                ResetPasswordOutcome.Success -> call.respond(HttpStatusCode.OK, VerificationResponse(verified = true))
                ResetPasswordOutcome.InvalidCode -> call.respond(
                    HttpStatusCode.BadRequest,
                    ApiErrorResponse("invalid_reset_code", "Invalid or expired code"),
                )
                ResetPasswordOutcome.InvalidInput -> call.respond(
                    HttpStatusCode.BadRequest,
                    ApiErrorResponse("invalid_input", "Invalid email or password"),
                )
            }
        }
    }
}
