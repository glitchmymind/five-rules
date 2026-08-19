package com.fiverules.features.auth.core.model

import com.fiverules.common.core.UiAction
import com.fiverules.common.core.UiState

data class LoginState(
    val mode: AuthMode = AuthMode.LOGIN,
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val verificationCode: String = "",
    val resendSeconds: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
) : UiState

enum class AuthMode {
    LOGIN,
    REGISTER,
    VERIFY_EMAIL,
    FORGOT_PASSWORD,
    RESET_PASSWORD,
}

sealed interface LoginAction : UiAction {
    data class EmailChanged(val value: String) : LoginAction
    data class PasswordChanged(val value: String) : LoginAction
    data class DisplayNameChanged(val value: String) : LoginAction
    data class VerificationCodeChanged(val value: String) : LoginAction
    data object ShowLogin : LoginAction
    data object ShowRegister : LoginAction
    data object ShowForgotPassword : LoginAction
    data object ResendCode : LoginAction
    data object ResendResetCode : LoginAction
    data object Submit : LoginAction
}
