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
    val speed: Float,        // fall speed multiplier
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
fun BalloonOverlay(stars: Int, perfectGame: Boolean = false) {
    val balloonCount = when {
        perfectGame -> 120
        stars == 3 -> 80
        stars == 2 -> 50
        else -> 30
    }
    val durationMs = when {
        perfectGame -> 34000
        stars == 3 -> 8000
        stars == 2 -> 7000
        else -> 6000
    }

    val balloons = remember(stars) {
        List(balloonCount) {
            val sizeMultiplier = if (Random.nextFloat() < 0.25f) 1.4f + Random.nextFloat() * 0.6f else 1f
            Balloon(
                x = Random.nextFloat(),
                speed = 0.5f + Random.nextFloat() * 0.7f,
                swayAmplitude = 0.015f + Random.nextFloat() * 0.04f,
                swayPhase = Random.nextFloat() * 2f * PI.toFloat(),
                bodyWidth = (30f + Random.nextFloat() * 40f) * sizeMultiplier,
                bodyHeight = (38f + Random.nextFloat() * 50f) * sizeMultiplier,
                color = balloonColors[Random.nextInt(balloonColors.size)],
                delay = Random.nextFloat() * 0.35f
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

            // Fall from above top to below bottom
            val yPos = -120f + (h + 240f) * localT * b.speed
            if (yPos > h + 120f) return@forEach

            val xSway = sin(localT * 6f + b.swayPhase) * b.swayAmplitude * w
            val xPos = b.x * w + xSway

            // Fade out in the last 15%
            val alpha = if (localT > 0.85f) ((1f - localT) / 0.15f).coerceIn(0f, 1f) else 1f

            val bw = b.bodyWidth
            val bh = b.bodyHeight

            // String — wavy line trailing above balloon
            val stringPath = Path().apply {
                val startY = yPos - bh / 2f - 6f
                moveTo(xPos, startY)
                val stringLen = 35f
                for (s in 1..8) {
                    val sy = startY - stringLen * s / 8f
                    val sx = xPos + sin(s * 1.2f + b.swayPhase) * 5f
                    lineTo(sx, sy)
                }
            }
            drawPath(
                stringPath,
                Color.Gray.copy(alpha = alpha * 0.5f),
                style = Stroke(width = 1.5f)
            )

            // Knot — small triangle at top of balloon
            val knotPath = Path().apply {
                moveTo(xPos - 3f, yPos - bh / 2f)
                lineTo(xPos + 3f, yPos - bh / 2f)
                lineTo(xPos, yPos - bh / 2f - 6f)
                close()
            }
            drawPath(knotPath, b.color.copy(alpha = alpha * 0.8f))

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
        }
    }
}
