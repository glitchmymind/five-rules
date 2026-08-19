package com.fiverules.features.auth.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fiverules.common.uikit.components.FrActionButton
import com.fiverules.common.uikit.components.FrCircleButton
import com.fiverules.common.uikit.components.FrLogo
import com.fiverules.common.uikit.components.FrScreenBackground
import com.fiverules.common.uikit.components.FrTextField
import com.fiverules.common.uikit.theme.FrTheme
import com.fiverules.common.uikit.theme.Spacing
import com.fiverules.features.auth.core.model.AuthMode
import com.fiverules.features.auth.core.model.LoginAction
import com.fiverules.features.auth.core.model.LoginState
import com.fiverules.features.auth.core.presentation.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreenHost(
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    FrScreenBackground {
        when (state.mode) {
            AuthMode.LOGIN -> EnterScreen(
                email = state.email,
                password = state.password,
                canSubmit = state.canSubmitLogin,
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
                infoMessage = state.infoMessage,
                onEmailChange = { viewModel.onAction(LoginAction.EmailChanged(it)) },
                onPasswordChange = { viewModel.onAction(LoginAction.PasswordChanged(it)) },
                onSubmit = { viewModel.onAction(LoginAction.Submit) },
                onPrivacy = { viewModel.onAction(LoginAction.OpenPrivacyPolicy) },
                onRegister = { viewModel.onAction(LoginAction.ShowRegister) },
                onForgotPassword = { viewModel.onAction(LoginAction.ShowForgotPassword) },
            )
            else -> AuthFormScreen(
                state = state,
                onAction = viewModel::onAction,
            )
        }
    }
}

@Composable
private fun EnterScreen(
    email: String,
    password: String,
    canSubmit: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    infoMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onPrivacy: () -> Unit,
    onRegister: () -> Unit,
    onForgotPassword: () -> Unit,
) {
    val colors = FrTheme.colors
    val typography = FrTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xxl),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.xxl),
                contentAlignment = Alignment.TopCenter,
            ) {
                FrLogo(width = 76.dp, height = 112.dp)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text(
                    text = "Your email",
                    style = typography.normalNormal,
                    color = colors.textPrimary,
                    modifier = Modifier.fillMaxWidth(),
                )
                FrTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                FrTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = "Password",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                    modifier = Modifier.fillMaxWidth(),
                )
                infoMessage?.let {
                    Text(text = it, style = typography.smallNormal, color = colors.primary)
                }
                errorMessage?.let {
                    Text(text = it, style = typography.smallNormal, color = colors.error)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            FrCircleButton(
                onClick = onSubmit,
                enabled = canSubmit && !isLoading,
                contentDescription = "Continue",
            )
            PrivacyPolicyText(onPrivacy = onPrivacy)
            Text(
                text = "Create account",
                style = typography.smallNormal,
                color = colors.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onRegister),
            )
            Text(
                text = "Forgot password",
                style = typography.smallNormal,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onForgotPassword),
            )
        }
    }
}

@Composable
private fun PrivacyPolicyText(onPrivacy: () -> Unit) {
    val colors = FrTheme.colors
    val typography = FrTheme.typography
    val annotated = buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = colors.textMuted,
                fontSize = typography.smallNormal.fontSize,
                fontWeight = typography.smallNormal.fontWeight,
                fontFamily = typography.smallNormal.fontFamily,
            ),
        ) {
            append("By registering you agree to ")
        }
        withStyle(
            SpanStyle(
                color = colors.primary,
                fontSize = typography.smallNormal.fontSize,
                fontWeight = typography.smallNormal.fontWeight,
                fontFamily = typography.smallNormal.fontFamily,
            ),
        ) {
            append("the privacy policy")
        }
    }
    Text(
        text = annotated,
        style = typography.smallNormal.copy(textAlign = TextAlign.Center),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPrivacy),
    )
}

@Composable
private fun AuthFormScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    val colors = FrTheme.colors
    val typography = FrTheme.typography
    val title = when (state.mode) {
        AuthMode.REGISTER -> "Create account"
        AuthMode.VERIFY_EMAIL -> "Confirm email"
        AuthMode.FORGOT_PASSWORD -> "Reset password"
        AuthMode.RESET_PASSWORD -> "New password"
        AuthMode.LOGIN -> "Your email"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.md, bottom = Spacing.xxl),
        ) {
            FrActionButton(
                onClick = { onAction(LoginAction.ShowLogin) },
                contentDescription = "Back",
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            Text(text = title, style = typography.normalNormal, color = colors.textPrimary)
            if (state.mode != AuthMode.VERIFY_EMAIL) {
                FrTextField(
                    value = state.email,
                    onValueChange = { onAction(LoginAction.EmailChanged(it)) },
                    label = "Email",
                    enabled = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (state.mode == AuthMode.REGISTER) {
                FrTextField(
                    value = state.displayName,
                    onValueChange = { onAction(LoginAction.DisplayNameChanged(it)) },
                    label = "Name",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (state.mode == AuthMode.REGISTER || state.mode == AuthMode.RESET_PASSWORD) {
                FrTextField(
                    value = state.password,
                    onValueChange = { onAction(LoginAction.PasswordChanged(it)) },
                    label = "Password",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { onAction(LoginAction.Submit) }),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (state.mode == AuthMode.VERIFY_EMAIL || state.mode == AuthMode.RESET_PASSWORD) {
                FrTextField(
                    value = state.verificationCode,
                    onValueChange = { onAction(LoginAction.VerificationCodeChanged(it)) },
                    label = "Code from email",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            state.infoMessage?.let { Text(it, style = typography.smallNormal, color = colors.primary) }
            state.errorMessage?.let { Text(it, style = typography.smallNormal, color = colors.error) }
        }
        Spacer(Modifier.weight(1f))
        Column(
            modifier = Modifier.padding(bottom = Spacing.md),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            FrCircleButton(
                onClick = { onAction(LoginAction.Submit) },
                enabled = !state.isLoading,
                contentDescription = "Continue",
            )
            if (state.mode == AuthMode.VERIFY_EMAIL) {
                Text(
                    text = if (state.resendSeconds > 0) {
                        "Call again after 0:${state.resendSeconds.toString().padStart(2, '0')}"
                    } else {
                        "Send code again"
                    },
                    style = typography.normalNormal,
                    color = colors.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = state.resendSeconds == 0) {
                            onAction(LoginAction.ResendCode)
                        },
                )
            }
        }
    }
}
