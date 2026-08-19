package com.fiverules.common.uikit.icons

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.Image
import com.fiverules.common.uikit.Res
import com.fiverules.common.uikit.ic_arrow_left
import com.fiverules.common.uikit.ic_arrow_right
import com.fiverules.common.uikit.ic_cancel
import com.fiverules.common.uikit.ic_comment
import com.fiverules.common.uikit.ic_heart_fill
import com.fiverules.common.uikit.ic_heart_outline
import com.fiverules.common.uikit.ic_nav_home
import com.fiverules.common.uikit.ic_nav_lessons
import com.fiverules.common.uikit.ic_nav_profile
import com.fiverules.common.uikit.ic_placeholder
import com.fiverules.common.uikit.theme.FrSize
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

enum class FrIcons(
    internal val resource: DrawableResource,
    val defaultSize: Dp,
    val tintable: Boolean = true,
) {
    ArrowLeft(Res.drawable.ic_arrow_left, FrSize.icon20),
    ArrowRight(Res.drawable.ic_arrow_right, FrSize.icon24),
    Cancel(Res.drawable.ic_cancel, FrSize.icon16),
    Comment(Res.drawable.ic_comment, FrSize.icon20),
    HeartOutline(Res.drawable.ic_heart_outline, FrSize.icon20),
    HeartFill(Res.drawable.ic_heart_fill, FrSize.icon20, tintable = false),
    Placeholder(Res.drawable.ic_placeholder, FrSize.icon20),
    NavHome(Res.drawable.ic_nav_home, FrSize.icon28, tintable = false),
    NavLessons(Res.drawable.ic_nav_lessons, FrSize.icon28),
    NavProfile(Res.drawable.ic_nav_profile, FrSize.icon28),
}

@Composable
fun FrIcon(
    icon: FrIcons,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    contentDescription: String? = null,
    size: Dp = icon.defaultSize,
) {
    Image(
        painter = painterResource(icon.resource),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit,
        colorFilter = when {
            tint != Color.Unspecified -> ColorFilter.tint(tint)
            else -> null
        },
    )
}
