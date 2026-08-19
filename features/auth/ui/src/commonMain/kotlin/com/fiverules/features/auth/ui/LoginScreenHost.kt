package com.fiverules.features.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fiverules.common.uikit.theme.Spacing
import com.fiverules.features.auth.core.model.AuthMode
import com.fiverules.features.auth.core.model.LoginAction
import com.fiverules.features.auth.core.presentation.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreenHost(
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val title = when (state.mode) {
        AuthMode.LOGIN -> "Вход"
        AuthMode.REGISTER -> "Регистрация"
        AuthMode.VERIFY_EMAIL -> "Подтверждение почты"
        AuthMode.FORGOT_PASSWORD -> "Восстановление пароля"
        AuthMode.RESET_PASSWORD -> "Новый пароль"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onAction(LoginAction.EmailChanged(it)) },
            label = { Text("Почта") },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.mode != AuthMode.VERIFY_EMAIL,
        )
        if (state.mode == AuthMode.REGISTER) {
            OutlinedTextField(
                value = state.displayName,
                onValueChange = { viewModel.onAction(LoginAction.DisplayNameChanged(it)) },
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (state.mode == AuthMode.LOGIN || state.mode == AuthMode.REGISTER || state.mode == AuthMode.RESET_PASSWORD) {
            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onAction(LoginAction.PasswordChanged(it)) },
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
            )
        }
        if (state.mode == AuthMode.VERIFY_EMAIL || state.mode == AuthMode.RESET_PASSWORD) {
            OutlinedTextField(
                value = state.verificationCode,
                onValueChange = { viewModel.onAction(LoginAction.VerificationCodeChanged(it)) },
                label = { Text("Код из письма") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        state.infoMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = { viewModel.onAction(LoginAction.Submit) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (state.isLoading) "..." else "Продолжить")
        }
        when (state.mode) {
            AuthMode.LOGIN -> {
                TextButton(onClick = { viewModel.onAction(LoginAction.ShowRegister) }) { Text("Создать аккаунт") }
                TextButton(onClick = { viewModel.onAction(LoginAction.ShowForgotPassword) }) { Text("Забыли пароль") }
            }
            AuthMode.REGISTER, AuthMode.FORGOT_PASSWORD, AuthMode.RESET_PASSWORD -> {
                TextButton(onClick = { viewModel.onAction(LoginAction.ShowLogin) }) { Text("Ко входу") }
            }
            AuthMode.VERIFY_EMAIL -> {
                TextButton(
                    onClick = { viewModel.onAction(LoginAction.ResendCode) },
                    enabled = state.resendSeconds == 0,
                ) {
                    Text(if (state.resendSeconds > 0) "Повторить через ${state.resendSeconds}с" else "Отправить код ещё раз")
                }
            }
        }
    }
}
