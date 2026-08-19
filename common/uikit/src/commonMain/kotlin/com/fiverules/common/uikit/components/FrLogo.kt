package com.fiverules.common.uikit.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fiverules.common.uikit.Res
import com.fiverules.common.uikit.logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun FrLogo(
    modifier: Modifier = Modifier,
    width: Dp = 38.dp,
    height: Dp = 56.dp,
) {
    Image(
        painter = painterResource(Res.drawable.logo),
        contentDescription = "5rules",
        modifier = modifier
            .width(width)
            .height(height),
        contentScale = ContentScale.FillBounds,
    )
}
