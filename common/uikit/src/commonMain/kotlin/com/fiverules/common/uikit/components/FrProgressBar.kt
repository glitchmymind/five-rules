package com.fiverules.common.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.fiverules.common.uikit.theme.FrRadius
import com.fiverules.common.uikit.theme.FrSize
import com.fiverules.common.uikit.theme.FrTheme

@Composable
fun FrProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val colors = FrTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(FrSize.progressBar)
            .clip(FrRadius.circle)
            .background(colors.primarySoft),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(FrRadius.circle)
                .background(colors.primary),
        )
    }
}
