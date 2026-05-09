package com.squaregarden.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.*
import kotlin.random.Random

private data class BurstStar(
    val x: Float,         // 0..1 normalized start X
    val speed: Float,      // fall speed multiplier
    val amplitude: Float,  // horizontal sway
    val phase: Float,      // sway phase offset
    val size: Float,       // star size
    val rotSpeed: Float,   // rotation speed deg/s
    val color: Color,
    val delay: Float       // stagger start 0..0.35
)

private val burstGold = Color(0xFFFFD700)
private val burstBright = Color(0xFFFFF9C4)
private val burstOrange = Color(0xFFFFAB00)
private val burstWhite = Color(0xFFFFFFFF)

@Composable
fun StarBurstOverlay(stars: Int) {
    val starCount = when (stars) {
        3 -> 90
        2 -> 60
        else -> 40
    }
    val durationMs = when (stars) {
        3 -> 8000
        2 -> 6500
        else -> 5000
    }

    val burstStars = remember(stars) {
        val colors = listOf(burstGold, burstBright, burstOrange, burstWhite)
        List(starCount) {
            val sizeMultiplier = if (Random.nextFloat() < 0.2f) 1.5f + Random.nextFloat() * 0.7f else 1f
            BurstStar(
                x = Random.nextFloat(),
                speed = 0.5f + Random.nextFloat() * 0.7f,
                amplitude = 0.015f + Random.nextFloat() * 0.04f,
                phase = Random.nextFloat() * 2f * PI.toFloat(),
                size = (12f + Random.nextFloat() * 24f) * sizeMultiplier,
                rotSpeed = 200f + Random.nextFloat() * 400f,
                color = colors[Random.nextInt(colors.size)],
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
    if (t <= 0f) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        burstStars.forEach { s ->
            val localT = ((t - s.delay) / (1f - s.delay)).coerceIn(0f, 1f)
            if (localT <= 0f) return@forEach

            // Fall from above top to below bottom
            val yPos = -50f + (h + 100f) * localT * s.speed
            if (yPos > h + 50f) return@forEach

            val xSway = sin(localT * 6f + s.phase) * s.amplitude * w
            val xPos = s.x * w + xSway

            // Fade out in the last 15%
            val alpha = if (localT > 0.85f) ((1f - localT) / 0.15f).coerceIn(0f, 1f) else 1f
            // Quick fade in
            val fadeIn = if (localT < 0.05f) localT / 0.05f else 1f
            val finalAlpha = (alpha * fadeIn).coerceIn(0f, 1f)

            if (finalAlpha <= 0f) return@forEach

            // Outer glow
            drawCircle(
                color = burstBright.copy(alpha = finalAlpha * 0.25f),
                radius = s.size * 2.2f,
                center = Offset(xPos, yPos)
            )

            // Star shape
            val starRadius = s.size
            val rotation = s.rotSpeed * t
            rotate(degrees = rotation, pivot = Offset(xPos, yPos)) {
                drawBurstStar(
                    center = Offset(xPos, yPos),
                    outerRadius = starRadius,
                    innerRadius = starRadius * 0.42f,
                    color = s.color.copy(alpha = finalAlpha),
                    points = 5
                )
                // Bright inner core
                drawBurstStar(
                    center = Offset(xPos, yPos),
                    outerRadius = starRadius * 0.5f,
                    innerRadius = starRadius * 0.22f,
                    color = burstBright.copy(alpha = finalAlpha * 0.8f),
                    points = 5
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBurstStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    color: Color,
    points: Int
) {
    val path = Path()
    val angleStep = PI.toFloat() / points
    for (i in 0 until points * 2) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = i * angleStep - PI.toFloat() / 2f
        val x = center.x + cos(angle) * radius
        val y = center.y + sin(angle) * radius
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color, style = Fill)
}
