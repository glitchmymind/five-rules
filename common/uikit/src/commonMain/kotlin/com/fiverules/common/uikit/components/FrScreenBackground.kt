package com.fiverules.common.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.fiverules.common.uikit.theme.FrTheme

@Composable
fun FrScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    val colors = FrTheme.colors
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface)
            .background(
                Brush.radialGradient(
                    colors = listOf(colors.primary.copy(alpha = 0.55f), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = 920f,
                ),
            ),
    ) {
        content()
    }
}
