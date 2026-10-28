package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LosslessGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SpotifyGreen

@Composable
fun SpectrumVisualizer(
    amplitudes: List<Float>,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val phaseAnim = remember { Animatable(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            phaseAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            phaseAnim.snapTo(0f)
        }
    }

    val gradientBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                NeonCyan,
                GoldPrimary,
                SpotifyGreen
            )
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        val barCount = amplitudes.size.coerceAtLeast(1)
        val spacing = 3.dp.toPx()
        val totalSpacing = spacing * (barCount - 1)
        val barWidth = ((size.width - totalSpacing) / barCount).coerceAtLeast(2f)
        val maxHeight = size.height

        amplitudes.forEachIndexed { index, amp ->
            val effectiveAmp = if (isPlaying) amp.coerceIn(0.08f, 1f) else 0.06f
            val barHeight = maxHeight * effectiveAmp
            val x = index * (barWidth + spacing)
            val y = maxHeight - barHeight

            drawRoundRect(
                brush = gradientBrush,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }
    }
}
