package com.fiverules.common.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.fiverules.common.uikit.theme.FrSize
import com.fiverules.common.uikit.theme.FrTheme

@Composable
fun FrAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = FrSize.avatar,
) {
    val colors = FrTheme.colors
    val initials = name
        .trim()
        .split(" ")
        .filter { it.isNotEmpty() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")
        .ifEmpty { "?" }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.primarySoft),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = FrTheme.typography.smallBold,
            color = colors.primary,
        )
    }
}
