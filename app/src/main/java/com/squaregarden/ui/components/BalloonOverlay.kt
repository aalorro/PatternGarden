package com.squaregarden.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.*
import kotlin.random.Random

private data class Balloon(
    val x: Float,           // 0..1 normalized start X
    val speed: Float,        // rise speed multiplier
    val swayAmplitude: Float,
    val swayPhase: Float,
    val bodyWidth: Float,    // balloon width
    val bodyHeight: Float,   // balloon height
    val color: Color,
    val delay: Float         // staggered start 0..0.3
)

private val balloonColors = listOf(
    Color(0xFFFF4444), // red
    Color(0xFF4488FF), // blue
    Color(0xFF44CC66), // green
    Color(0xFFFFCC22), // yellow
    Color(0xFFFF66AA), // pink
    Color(0xFF9955DD), // purple
)

@Composable
fun BalloonOverlay(stars: Int) {
    val balloonCount = when (stars) {
        3 -> 40
        2 -> 25
        else -> 15
    }
    val durationMs = when (stars) {
        3 -> 7000
        2 -> 6000
        else -> 5000
    }

    val balloons = remember(stars) {
        List(balloonCount) {
            Balloon(
                x = Random.nextFloat(),
                speed = 0.6f + Random.nextFloat() * 0.6f,
                swayAmplitude = 0.015f + Random.nextFloat() * 0.035f,
                swayPhase = Random.nextFloat() * 2f * PI.toFloat(),
                bodyWidth = 20f + Random.nextFloat() * 15f,
                bodyHeight = 26f + Random.nextFloat() * 18f,
                color = balloonColors[Random.nextInt(balloonColors.size)],
                delay = Random.nextFloat() * 0.3f
            )
        }
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(stars) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMs, easing = LinearEasing)
        )
    }

    val t = progress.value

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        balloons.forEach { b ->
            val localT = ((t - b.delay) / (1f - b.delay)).coerceIn(0f, 1f)
            if (localT <= 0f) return@forEach

            // Rise from bottom to above top
            val yPos = h + 40f - (h + 100f) * localT * b.speed
            if (yPos < -80f) return@forEach

            val xSway = sin(localT * 6f + b.swayPhase) * b.swayAmplitude * w
            val xPos = b.x * w + xSway

            // Fade out in the last 15%
            val alpha = if (localT > 0.85f) ((1f - localT) / 0.15f).coerceIn(0f, 1f) else 1f

            val bw = b.bodyWidth
            val bh = b.bodyHeight

            // Balloon body — oval
            drawOval(
                color = b.color.copy(alpha = alpha),
                topLeft = Offset(xPos - bw / 2f, yPos - bh / 2f),
                size = Size(bw, bh)
            )

            // Highlight — small bright oval at top-left
            drawOval(
                color = Color.White.copy(alpha = alpha * 0.4f),
                topLeft = Offset(xPos - bw * 0.25f, yPos - bh * 0.35f),
                size = Size(bw * 0.3f, bh * 0.25f)
            )

            // Knot — small triangle at bottom
            val knotPath = Path().apply {
                moveTo(xPos - 3f, yPos + bh / 2f)
                lineTo(xPos + 3f, yPos + bh / 2f)
                lineTo(xPos, yPos + bh / 2f + 6f)
                close()
            }
            drawPath(knotPath, b.color.copy(alpha = alpha * 0.8f))

            // String — wavy line below knot
            val stringPath = Path().apply {
                val startY = yPos + bh / 2f + 6f
                moveTo(xPos, startY)
                val stringLen = 30f
                for (s in 1..8) {
                    val sy = startY + stringLen * s / 8f
                    val sx = xPos + sin(s * 1.2f + b.swayPhase) * 4f
                    lineTo(sx, sy)
                }
            }
            drawPath(
                stringPath,
                Color.Gray.copy(alpha = alpha * 0.5f),
                style = Stroke(width = 1f)
            )
        }
    }
}
