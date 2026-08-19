package com.fiverules.common.uikit.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.fiverules.common.uikit.icons.FrIcon
import com.fiverules.common.uikit.icons.FrIcons
import com.fiverules.common.uikit.theme.FrSize
import com.fiverules.common.uikit.theme.FrTheme
import com.fiverules.common.uikit.theme.Spacing

@Composable
fun FrPreviewMessage(
    author: String,
    ruleName: String,
    text: String,
    time: String,
    modifier: Modifier = Modifier,
    liked: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLike: (() -> Unit)? = null,
    onComment: (() -> Unit)? = null,
) {
    val colors = FrTheme.colors
    val typography = FrTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        role = Role.Button,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            ),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        FrAvatar(name = author)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = author,
                    style = typography.normalMedium,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (time.isNotEmpty()) {
                    Text(
                        text = time,
                        style = typography.smallNormal,
                        color = colors.textMuted,
                        modifier = Modifier.padding(start = Spacing.sm),
                    )
                }
            }
            if (ruleName.isNotEmpty()) {
                Text(
                    text = ruleName,
                    style = typography.smallNormal,
                    color = colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = text,
                style = typography.normalNormal,
                color = colors.textPrimary,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FrIcon(
                    icon = if (liked) FrIcons.HeartFill else FrIcons.HeartOutline,
                    tint = if (liked) colors.like else colors.textMuted,
                    size = FrSize.icon20,
                    contentDescription = "Like",
                    modifier = if (onLike != null) {
                        Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onLike,
                        )
                    } else {
                        Modifier
                    },
                )
                FrIcon(
                    icon = FrIcons.Comment,
                    tint = colors.textMuted,
                    size = FrSize.icon20,
                    contentDescription = "Comment",
                    modifier = if (onComment != null) {
                        Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onComment,
                        )
                    } else {
                        Modifier
                    },
                )
            }
        }
    }
}
