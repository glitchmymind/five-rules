package com.fiverules.common.uikit.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Figma Variables: Extra large 40, Large 32, Extra Medium 24, Medium 20, Extra Small 16, Small 8.
 */
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val medium = 20.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 40.dp
}

object FrRadius {
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 40.dp
    val pill = 100.dp

    val button = RoundedCornerShape(lg)
    val card = RoundedCornerShape(lg)
    val circle = RoundedCornerShape(pill)
}

object FrSize {
    val icon14 = 14.dp
    val icon16 = 16.dp
    val icon20 = 20.dp
    val icon24 = 24.dp
    val icon28 = 28.dp
    val actionButton = 36.dp
    val circleButton = 56.dp
    val textField = 45.dp
    val logoAuth = 76.dp to 112.dp
    val logoHeader = 38.dp to 56.dp
}
