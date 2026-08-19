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
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.fiverules.common.uikit.icons.FrIcon
import com.fiverules.common.uikit.icons.FrIcons
import com.fiverules.common.uikit.theme.FrRadius
import com.fiverules.common.uikit.theme.FrSize
import com.fiverules.common.uikit.theme.FrTheme

data class FrToolbarItem(
    val icon: FrIcons,
    val contentDescription: String,
    val selected: Boolean,
    val onClick: () -> Unit,
)

@Composable
fun FrToolbar(
    items: List<FrToolbarItem>,
    modifier: Modifier = Modifier,
) {
    val colors = FrTheme.colors
    Row(
        modifier = modifier
            .width(FrSize.toolbarWidth)
            .height(FrSize.toolbar)
            .shadow(
                elevation = 14.dp,
                shape = FrRadius.circle,
                ambientColor = colors.shadow,
                spotColor = colors.shadow,
            )
            .clip(FrRadius.circle)
            .background(colors.bar),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            Box(
                modifier = Modifier
                    .size(FrSize.toolbar)
                    .clickable(
                        role = Role.Tab,
                        indication = null,
                        interactionSource = remember(item.contentDescription) { MutableInteractionSource() },
                        onClick = item.onClick,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                FrIcon(
                    icon = item.icon,
                    tint = if (item.selected) colors.primary else colors.textMuted,
                    size = FrSize.icon28,
                    contentDescription = item.contentDescription,
                )
            }
        }
    }
}

@Composable
fun FrScreenHeader(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(FrSize.header),
        contentAlignment = Alignment.Center,
    ) {
        FrLogo(
            width = FrSize.logoHeader.first,
            height = FrSize.logoHeader.second,
        )
    }
}
