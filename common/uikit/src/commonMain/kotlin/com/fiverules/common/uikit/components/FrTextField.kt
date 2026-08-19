package com.fiverules.common.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.fiverules.common.uikit.theme.FrSize
import com.fiverules.common.uikit.theme.FrTheme

enum class FrTextFieldStatus { Idle, Focused, Error, Disabled }

@Composable
fun FrTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
) {
    var focused by remember { mutableStateOf(false) }
    val status = when {
        !enabled -> FrTextFieldStatus.Disabled
        isError -> FrTextFieldStatus.Error
        focused -> FrTextFieldStatus.Focused
        else -> FrTextFieldStatus.Idle
    }
    val colors = FrTheme.colors
    val typography = FrTheme.typography
    val underline = when (status) {
        FrTextFieldStatus.Focused -> colors.primary
        FrTextFieldStatus.Error -> colors.error
        else -> colors.textMuted
    }
    val labelColor = when (status) {
        FrTextFieldStatus.Focused -> colors.primary
        FrTextFieldStatus.Error -> colors.error
        FrTextFieldStatus.Disabled -> colors.textMuted.copy(alpha = 0.5f)
        FrTextFieldStatus.Idle -> if (value.isNotEmpty() && label != null) colors.textMuted else colors.textMuted
    }
    val textColor = when (status) {
        FrTextFieldStatus.Disabled -> colors.textMuted.copy(alpha = 0.5f)
        FrTextFieldStatus.Focused -> if (value.isEmpty() && placeholder != null) colors.primary else colors.textPrimary
        else -> colors.textPrimary
    }
    val showFloatingLabel = label != null && (focused || value.isNotEmpty())

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = singleLine,
        textStyle = typography.normalNormal.copy(color = textColor),
        cursorBrush = SolidColor(colors.primary),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier
            .height(FrSize.textField)
            .onFocusChanged { focused = it.isFocused },
        decorationBox = { inner ->
            Column(modifier = Modifier.fillMaxWidth()) {
                if (showFloatingLabel) {
                    Text(
                        text = label.orEmpty(),
                        style = typography.smallNormal,
                        color = labelColor,
                    )
                    Spacer(Modifier.height(3.dp))
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = androidx.compose.ui.Alignment.CenterStart,
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder ?: label.takeUnless { showFloatingLabel }.orEmpty(),
                            style = typography.normalNormal,
                            color = when (status) {
                                FrTextFieldStatus.Focused -> colors.primary
                                FrTextFieldStatus.Disabled -> colors.textMuted.copy(alpha = 0.5f)
                                else -> colors.textMuted
                            },
                        )
                    }
                    inner()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(underline),
                )
            }
        },
    )
}
