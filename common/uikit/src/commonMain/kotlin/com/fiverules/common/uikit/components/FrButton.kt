package com.fiverules.common.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fiverules.common.uikit.icons.FrIcon
import com.fiverules.common.uikit.icons.FrIcons
import com.fiverules.common.uikit.theme.FrRadius
import com.fiverules.common.uikit.theme.FrSize
import com.fiverules.common.uikit.theme.FrTheme
import com.fiverules.common.uikit.theme.Spacing

enum class FrButtonStyle { Action, Secondary, Circle }

@Composable
fun FrButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: FrButtonStyle = FrButtonStyle.Action,
) {
    val colors = FrTheme.colors
    val container = when {
        !enabled && style == FrButtonStyle.Secondary -> Color.Transparent
        !enabled -> colors.textMuted
        style == FrButtonStyle.Secondary -> Color.Transparent
        else -> colors.primary
    }
    val content = when {
        !enabled && style == FrButtonStyle.Secondary -> colors.textMuted
        style == FrButtonStyle.Secondary -> colors.primary
        else -> colors.onPrimary
    }
    val borderColor = when {
        style != FrButtonStyle.Secondary -> null
        enabled -> colors.primary
        else -> colors.textMuted
    }
    Box(
        modifier = modifier
            .clip(FrRadius.button)
            .then(
                if (borderColor != null) {
                    Modifier.border(1.dp, borderColor, FrRadius.button)
                } else {
                    Modifier
                },
            )
            .background(container)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .defaultMinSize(minHeight = 57.dp)
            .padding(PaddingValues(horizontal = Spacing.medium, vertical = Spacing.medium)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = FrTheme.typography.normalMedium,
            color = content,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun FrCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: FrIcons = FrIcons.ArrowRight,
    contentDescription: String? = null,
) {
    val colors = FrTheme.colors
    Box(
        modifier = modifier
            .size(FrSize.circleButton)
            .clip(CircleShape)
            .background(if (enabled) colors.primary else colors.textMuted)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(Spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        FrIcon(
            icon = icon,
            tint = colors.onPrimary,
            size = FrSize.icon24,
            contentDescription = contentDescription,
        )
    }
}

@Composable
fun FrActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: FrIcons = FrIcons.ArrowLeft,
    contentDescription: String? = null,
) {
    val colors = FrTheme.colors
    Box(
        modifier = modifier
            .size(FrSize.actionButton)
            .clip(CircleShape)
            .background(colors.actionButton)
            .clickable(
                role = Role.Button,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(Spacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        FrIcon(
            icon = icon,
            tint = colors.textPrimary,
            size = FrSize.icon20,
            contentDescription = contentDescription,
        )
    }
}
