package com.fiverules.features.auth.core.data

import com.fiverules.common.models.ApiPaths
import com.fiverules.common.models.auth.AuthTokensResponse
import com.fiverules.common.models.auth.ForgotPasswordRequest
import com.fiverules.common.models.auth.LoginRequest
import com.fiverules.common.models.auth.RegisterRequest
import com.fiverules.common.models.auth.RegistrationResponse
import com.fiverules.common.models.auth.ResendVerificationRequest
import com.fiverules.common.models.auth.ResetPasswordRequest
import com.fiverules.common.models.auth.VerificationResponse
import com.fiverules.common.models.auth.VerifyEmailRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthApi(
    private val httpClient: HttpClient,
) {
    suspend fun login(request: LoginRequest): AuthTokensResponse =
        httpClient.post(ApiPaths.AUTH_LOGIN) { setBody(request) }.body()

    suspend fun register(request: RegisterRequest): RegistrationResponse =
        httpClient.post(ApiPaths.AUTH_REGISTER) { setBody(request) }.body()

    suspend fun verifyEmail(request: VerifyEmailRequest): AuthTokensResponse =
        httpClient.post(ApiPaths.AUTH_VERIFY_EMAIL) { setBody(request) }.body()

    suspend fun resendVerification(email: String): VerificationResponse =
        httpClient.post(ApiPaths.AUTH_RESEND_VERIFICATION) {
            setBody(ResendVerificationRequest(email))
        }.body()

    suspend fun forgotPassword(email: String): VerificationResponse =
        httpClient.post(ApiPaths.AUTH_FORGOT_PASSWORD) {
            setBody(ForgotPasswordRequest(email))
        }.body()

    suspend fun resetPassword(request: ResetPasswordRequest): VerificationResponse =
        httpClient.post(ApiPaths.AUTH_RESET_PASSWORD) { setBody(request) }.body()
}
