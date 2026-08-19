package com.fiverules.features.auth.core.presentation

import androidx.lifecycle.viewModelScope
import com.fiverules.common.core.MviViewModel
import com.fiverules.common.models.auth.LoginRequest
import com.fiverules.common.models.auth.RegisterRequest
import com.fiverules.common.models.auth.ResetPasswordRequest
import com.fiverules.common.models.auth.VerifyEmailRequest
import com.fiverules.common.navigation.AppNavigator
import com.fiverules.common.network.TokenRepository
import com.fiverules.features.auth.core.data.AuthApi
import com.fiverules.features.auth.core.model.AuthMode
import com.fiverules.features.auth.core.model.LoginAction
import com.fiverules.features.auth.core.model.LoginState
import com.fiverules.features.home.api.HomeRoute
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(
    private val navigator: AppNavigator,
    private val authApi: AuthApi,
    private val tokenRepository: TokenRepository,
) : MviViewModel<LoginState, LoginAction>() {
    private var resendTimer: Job? = null

    override fun initState(): LoginState = LoginState()

    override fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChanged -> updateState {
                copy(email = action.value, errorMessage = null, infoMessage = null)
            }
            is LoginAction.PasswordChanged -> updateState {
                copy(password = action.value, errorMessage = null, infoMessage = null)
            }
            is LoginAction.DisplayNameChanged -> updateState {
                copy(displayName = action.value, errorMessage = null)
            }
            is LoginAction.VerificationCodeChanged -> {
                val digits = action.value.filter(Char::isDigit).take(6)
                updateState { copy(verificationCode = digits, errorMessage = null) }
                if (digits.length == 6 && !uiStateValue.isLoading) {
                    when (uiStateValue.mode) {
                        AuthMode.VERIFY_EMAIL -> submitVerification(digits)
                        AuthMode.RESET_PASSWORD -> submitResetPassword(digits)
                        else -> Unit
                    }
                }
            }
            LoginAction.ShowLogin -> updateState {
                copy(mode = AuthMode.LOGIN, errorMessage = null, infoMessage = null, verificationCode = "")
            }
            LoginAction.ShowRegister -> updateState {
                copy(mode = AuthMode.REGISTER, errorMessage = null, infoMessage = null, verificationCode = "")
            }
            LoginAction.OpenPrivacyPolicy -> Unit
            LoginAction.ShowForgotPassword -> updateState {
                copy(mode = AuthMode.FORGOT_PASSWORD, errorMessage = null, infoMessage = null, verificationCode = "", password = "")
            }
            LoginAction.ResendCode -> resendVerificationCode()
            LoginAction.ResendResetCode -> resendResetCode()
            LoginAction.Submit -> submit()
        }
    }

    private fun submit() {
        when (uiStateValue.mode) {
            AuthMode.LOGIN -> submitLogin()
            AuthMode.REGISTER -> submitRegistration()
            AuthMode.VERIFY_EMAIL -> submitVerification(uiStateValue.verificationCode)
            AuthMode.FORGOT_PASSWORD -> submitForgotPassword()
            AuthMode.RESET_PASSWORD -> submitResetPassword(uiStateValue.verificationCode)
        }
    }

    private fun submitLogin() {
        val email = uiStateValue.email.trim()
        val password = uiStateValue.password
        if (email.isBlank() || password.isBlank()) {
            updateState { copy(errorMessage = "Введите почту и пароль") }
            return
        }
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null, infoMessage = null) }
            try {
                val response = authApi.login(LoginRequest(email = email, password = password))
                finishAuthentication(response.accessToken, response.refreshToken)
            } catch (error: ClientRequestException) {
                if (error.response.status == HttpStatusCode.Forbidden) {
                    updateState {
                        copy(
                            mode = AuthMode.VERIFY_EMAIL,
                            isLoading = false,
                            infoMessage = "Почта не подтверждена. Мы отправили новый код.",
                            errorMessage = null,
                        )
                    }
                    startResendTimer()
                    return@launch
                }
                updateState { copy(isLoading = false, errorMessage = "Неверная почта или пароль") }
            } catch (_: Exception) {
                updateState { copy(isLoading = false, errorMessage = "Не удалось войти. Попробуйте снова.") }
            }
        }
    }

    private fun submitRegistration() {
        val email = uiStateValue.email.trim()
        val password = uiStateValue.password
        if (!EMAIL_REGEX.matches(email)) {
            updateState { copy(errorMessage = "Введите корректный адрес почты") }
            return
        }
        if (!isValidPassword(password)) {
            updateState { copy(errorMessage = "Пароль: минимум 8 символов, заглавная и строчная буквы, цифра") }
            return
        }
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null, infoMessage = null) }
            try {
                authApi.register(
                    RegisterRequest(
                        email = email,
                        password = password,
                        displayName = uiStateValue.displayName.trim().takeIf(String::isNotEmpty),
                    ),
                )
                updateState {
                    copy(
                        mode = AuthMode.VERIFY_EMAIL,
                        isLoading = false,
                        verificationCode = "",
                        infoMessage = "Код отправлен на почту",
                    )
                }
                startResendTimer()
            } catch (_: Exception) {
                updateState { copy(isLoading = false, errorMessage = "Не удалось зарегистрироваться") }
            }
        }
    }

    private fun submitVerification(code: String) {
        if (code.length != 6) {
            updateState { copy(errorMessage = "Введите 6-значный код") }
            return
        }
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            try {
                val response = authApi.verifyEmail(
                    VerifyEmailRequest(email = uiStateValue.email.trim(), code = code),
                )
                finishAuthentication(response.accessToken, response.refreshToken)
            } catch (_: Exception) {
                updateState { copy(isLoading = false, errorMessage = "Неверный или просроченный код") }
            }
        }
    }

    private fun submitForgotPassword() {
        val email = uiStateValue.email.trim()
        if (!EMAIL_REGEX.matches(email)) {
            updateState { copy(errorMessage = "Введите корректный адрес почты") }
            return
        }
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            try {
                authApi.forgotPassword(email)
                updateState {
                    copy(
                        mode = AuthMode.RESET_PASSWORD,
                        isLoading = false,
                        infoMessage = "Если аккаунт существует, мы отправили код",
                    )
                }
                startResendTimer()
            } catch (_: Exception) {
                updateState { copy(isLoading = false, errorMessage = "Не удалось отправить код") }
            }
        }
    }

    private fun submitResetPassword(code: String) {
        val password = uiStateValue.password
        if (code.length != 6 || !isValidPassword(password)) {
            updateState { copy(errorMessage = "Проверьте код и новый пароль") }
            return
        }
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            try {
                authApi.resetPassword(
                    ResetPasswordRequest(
                        email = uiStateValue.email.trim(),
                        code = code,
                        newPassword = password,
                    ),
                )
                updateState {
                    copy(
                        mode = AuthMode.LOGIN,
                        isLoading = false,
                        infoMessage = "Пароль обновлён. Войдите.",
                        verificationCode = "",
                    )
                }
            } catch (_: Exception) {
                updateState { copy(isLoading = false, errorMessage = "Не удалось сбросить пароль") }
            }
        }
    }

    private fun resendVerificationCode() {
        if (uiStateValue.resendSeconds > 0) return
        viewModelScope.launch {
            runCatching { authApi.resendVerification(uiStateValue.email.trim()) }
            startResendTimer()
        }
    }

    private fun resendResetCode() {
        if (uiStateValue.resendSeconds > 0) return
        viewModelScope.launch {
            runCatching { authApi.forgotPassword(uiStateValue.email.trim()) }
            startResendTimer()
        }
    }

    private fun finishAuthentication(access: String, refresh: String) {
        tokenRepository.saveTokens(access, refresh)
        updateState { copy(isLoading = false) }
        navigator.navigateAndClearStack(HomeRoute)
    }

    private fun startResendTimer() {
        resendTimer?.cancel()
        resendTimer = viewModelScope.launch {
            updateState { copy(resendSeconds = 30) }
            while (uiStateValue.resendSeconds > 0) {
                delay(1000)
                updateState { copy(resendSeconds = (resendSeconds - 1).coerceAtLeast(0)) }
            }
        }
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
