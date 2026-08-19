package com.fiverules.common.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.fiverules.common.uikit.theme.FrSize
import com.fiverules.common.uikit.theme.FrTheme
import com.fiverules.common.uikit.theme.Spacing

enum class FrListItemStyle { Rule, Task }

@Composable
fun FrListItem(
    title: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    style: FrListItemStyle = FrListItemStyle.Rule,
    icon: FrIcons = if (style == FrListItemStyle.Rule) FrIcons.NavLessons else FrIcons.Placeholder,
) {
    val colors = FrTheme.colors
    val isRule = style == FrListItemStyle.Rule
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isRule) FrSize.listRule else FrSize.listTask)
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(if (isRule) FrSize.avatar else FrSize.icon20)
                .clip(CircleShape)
                .background(if (isRule) colors.primarySoft else colors.background),
            contentAlignment = Alignment.Center,
        ) {
            FrIcon(
                icon = icon,
                tint = if (isRule) colors.primary else colors.textMuted,
                size = if (isRule) FrSize.icon20 else FrSize.icon14,
                contentDescription = null,
            )
        }
        Text(
            text = title,
            style = if (isRule) FrTheme.typography.normalMedium else FrTheme.typography.smallNormal,
            color = if (isRule) colors.textPrimary else colors.textMuted,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}
