package com.fiverules.common.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.fiverules.common.uikit.icons.FrIcon
import com.fiverules.common.uikit.icons.FrIcons
import com.fiverules.common.uikit.theme.FrRadius
import com.fiverules.common.uikit.theme.FrSize
import com.fiverules.common.uikit.theme.FrTheme
import com.fiverules.common.uikit.theme.Spacing

enum class FrTaskCardStyle { Highlighted, Default, Completed, Locked }

@Composable
fun FrTaskCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: FrTaskCardStyle = FrTaskCardStyle.Default,
) {
    val colors = FrTheme.colors
    val container = when (style) {
        FrTaskCardStyle.Highlighted -> colors.primary
        FrTaskCardStyle.Completed -> colors.primarySoft
        FrTaskCardStyle.Locked -> colors.surface
        FrTaskCardStyle.Default -> colors.surface
    }
    val titleColor = when (style) {
        FrTaskCardStyle.Highlighted -> colors.onPrimary
        FrTaskCardStyle.Locked -> colors.textMuted
        else -> colors.textPrimary
    }
    val descriptionColor = when (style) {
        FrTaskCardStyle.Highlighted -> colors.onPrimary.copy(alpha = 0.82f)
        else -> colors.textMuted
    }
    val arrowColor = when (style) {
        FrTaskCardStyle.Highlighted -> colors.onPrimary
        FrTaskCardStyle.Locked -> colors.textMuted
        else -> colors.primary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FrSize.taskCard)
            .clip(FrRadius.card)
            .background(container)
            .clickable(
                enabled = style != FrTaskCardStyle.Locked,
                role = Role.Button,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(horizontal = Spacing.medium, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = title,
                style = FrTheme.typography.largeBold,
                color = titleColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    style = FrTheme.typography.normalNormal,
                    color = descriptionColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        FrIcon(
            icon = FrIcons.ArrowRight,
            tint = arrowColor,
            size = FrSize.icon24,
            contentDescription = null,
        )
    }
}
