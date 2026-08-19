package com.fiverules.common.uikit.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiverules.common.uikit.theme.FrSize
import com.fiverules.common.uikit.theme.FrTheme
import com.fiverules.common.uikit.theme.Spacing
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Daily rule disc with pressable elevation and a living aura.
 *
 * Aura motion follows the Yandex Music "My Wave" idea
 * (https://habr.com/ru/companies/yandex/articles/678102/):
 * low band drives glow depth, mid band bends the blob, frames lerp toward
 * a 100ms sample instead of restarting a new animator each tick.
 */
@Composable
fun FrDailyRuleButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FrTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val wave = remember { WaveAuraSampler() }
    var bass by remember { mutableFloatStateOf(0.5f) }
    var mids by remember { mutableFloatStateOf(0.4f) }
    var phase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { frameMs ->
                wave.onFrame(frameMs)
                bass = wave.bass
                mids = wave.mids
                phase = wave.phase
            }
        }
    }

    val elevation by animateDpAsState(
        targetValue = if (pressed) 3.dp else 16.dp,
        animationSpec = tween(durationMillis = 90),
        label = "daily-rule-elevation",
    )
    val pressOffset by animateDpAsState(
        targetValue = if (pressed) 7.dp else 0.dp,
        animationSpec = tween(durationMillis = 90),
        label = "daily-rule-press",
    )
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 90),
        label = "daily-rule-scale",
    )
    val auraGain by animateFloatAsState(
        targetValue = if (pressed) 0.72f else 1f,
        animationSpec = tween(durationMillis = 90),
        label = "daily-rule-aura",
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(FrSize.dailyRule + FrSize.dailyRuleAura * 2)
                .graphicsLayer {
                    translationY = pressOffset.toPx()
                    scaleX = pressScale
                    scaleY = pressScale
                },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawWaveAura(
                    bass = bass,
                    mids = mids,
                    phase = phase,
                    intensity = auraGain,
                    glow = colors.primary,
                )
            }
            Box(
                modifier = Modifier
                    .size(FrSize.dailyRule)
                    .shadow(
                        elevation = elevation,
                        shape = CircleShape,
                        ambientColor = colors.primary.copy(alpha = 0.28f),
                        spotColor = colors.shadow,
                    )
                    .clip(CircleShape)
                    .background(colors.surface)
                    .clickable(
                        role = Role.Button,
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = onClick,
                    )
                    .padding(Spacing.xl),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title,
                    style = FrTheme.typography.titleNormal.copy(lineHeight = 28.sp),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private class WaveAuraSampler {
    var bass: Float = 0.5f
        private set
    var mids: Float = 0.4f
        private set
    var phase: Float = 0f
        private set

    private var targetBass = 0.5f
    private var targetMids = 0.4f
    private var lastSampleMs = 0L

    fun onFrame(timeMs: Long) {
        if (lastSampleMs == 0L || timeMs - lastSampleMs >= SAMPLE_WINDOW_MS) {
            lastSampleMs = timeMs
            val seconds = timeMs / 1000f
            targetBass = 0.48f + 0.42f * sin(seconds * 0.85f)
            targetMids = 0.38f + 0.48f * sin(seconds * 1.65f + 1.1f)
        }
        bass += (targetBass - bass) * LERP
        mids += (targetMids - mids) * LERP
        phase = timeMs / 1000f * 0.65f
    }

    private companion object {
        const val SAMPLE_WINDOW_MS = 100L
        const val LERP = 0.18f
    }
}

private fun DrawScope.drawWaveAura(
    bass: Float,
    mids: Float,
    phase: Float,
    intensity: Float,
    glow: Color,
) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val discRadius = FrSize.dailyRule.toPx() / 2f
    val fill = (0.55f + bass * 0.45f) * intensity
    val bend = 0.03f + mids * 0.045f
    val haloRadius = discRadius * (2.28f + bass * 0.18f)

    drawCircle(
        brush = Brush.radialGradient(
            colorStops = fadeStops(peakAlpha = 0.42f * fill, color = glow),
            center = center,
            radius = haloRadius,
        ),
        radius = haloRadius,
        center = center,
    )

    val blob = waveBlob(
        center = center,
        radius = discRadius * (1.48f + bass * 0.1f),
        bend = bend,
        phase = phase,
    )
    drawPath(
        path = blob,
        brush = Brush.radialGradient(
            colorStops = fadeStops(peakAlpha = 0.22f * fill, color = glow),
            center = center,
            radius = discRadius * 1.85f,
        ),
    )
}

private fun fadeStops(peakAlpha: Float, color: Color): Array<Pair<Float, Color>> {
    val positions = floatArrayOf(0f, 0.16f, 0.32f, 0.5f, 0.68f, 0.82f, 0.93f, 1f)
    return Array(positions.size) { index ->
        val t = positions[index]
        val falloff = (1f - t) * (1f - t)
        t to if (t >= 0.999f) Color.Transparent else color.copy(alpha = peakAlpha * falloff)
    }
}

private fun waveBlob(
    center: Offset,
    radius: Float,
    bend: Float,
    phase: Float,
    points: Int = 72,
): Path {
    val path = Path()
    for (index in 0..points) {
        val theta = index / points.toFloat() * 2f * PI.toFloat()
        val deform =
            sin(2f * theta + phase) * bend +
                sin(3f * theta + phase * 1.35f) * bend * 0.65f +
                sin(5f * theta - phase * 0.85f) * bend * 0.4f
        val r = radius * (1f + deform)
        val x = center.x + r * cos(theta)
        val y = center.y + r * sin(theta)
        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
