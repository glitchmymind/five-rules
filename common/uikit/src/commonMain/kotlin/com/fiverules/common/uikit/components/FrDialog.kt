package com.fiverules.common.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.fiverules.common.uikit.theme.FrRadius
import com.fiverules.common.uikit.theme.FrTheme
import com.fiverules.common.uikit.theme.Spacing

@Composable
fun FrDialog(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .widthIn(max = 280.dp)
            .shadow(
                elevation = 14.dp,
                shape = FrRadius.card,
                ambientColor = FrTheme.colors.shadow,
                spotColor = FrTheme.colors.shadow,
            )
            .clip(FrRadius.card)
            .background(FrTheme.colors.surface)
            .padding(Spacing.lg),
        contentAlignment = Alignment.Center,
    ) {
        Column(content = content)
    }
}

@Composable
fun FrOverlay(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(FrTheme.colors.overlay),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
