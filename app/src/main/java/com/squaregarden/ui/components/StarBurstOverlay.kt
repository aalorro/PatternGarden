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
    val angle: Float,       // radial direction in degrees
    val speed: Float,        // distance multiplier
    val size: Float,         // star size
    val rotSpeed: Float,     // rotation speed deg/s
    val color: Color,
    val trailCount: Int      // 2-3 trail dots
)

private val burstGold = Color(0xFFFFD700)
private val burstBright = Color(0xFFFFF9C4)
private val burstOrange = Color(0xFFFFAB00)
private val burstWhite = Color(0xFFFFFFFF)

@Composable
fun StarBurstOverlay(stars: Int) {
    val starCount = when (stars) {
        3 -> 50
        2 -> 35
        else -> 20
    }
    val durationMs = 2500
    val delayMs = 500 // start after 500ms

    val burstStars = remember(stars) {
        val colors = listOf(burstGold, burstBright, burstOrange, burstWhite)
        List(starCount) {
            BurstStar(
                angle = Random.nextFloat() * 360f,
                speed = 0.5f + Random.nextFloat() * 0.7f,
                size = 6f + Random.nextFloat() * 10f,
                rotSpeed = 200f + Random.nextFloat() * 400f,
                color = colors[Random.nextInt(colors.size)],
                trailCount = 2 + Random.nextInt(2)
            )
        }
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(stars) {
        progress.snapTo(0f)
        kotlinx.coroutines.delay(delayMs.toLong())
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMs, easing = FastOutSlowInEasing)
        )
    }

    val t = progress.value
    if (t <= 0f) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h * 0.4f // slightly above center
        val maxDist = minOf(w, h) * 0.55f

        burstStars.forEach { s ->
            val angleRad = s.angle * PI.toFloat() / 180f
            val dist = maxDist * t * s.speed

            // Scale: grow then shrink
            val scale = when {
                t < 0.15f -> t / 0.15f
                t > 0.7f -> (1f - t) / 0.3f
                else -> 1f
            }.coerceIn(0f, 1f)

            val alpha = when {
                t > 0.75f -> (1f - t) / 0.25f
                else -> 1f
            }.coerceIn(0f, 1f)

            if (alpha <= 0f || scale <= 0f) return@forEach

            val posX = cx + cos(angleRad) * dist
            val posY = cy + sin(angleRad) * dist

            // Trail dots behind the star
            for (trail in s.trailCount downTo 1) {
                val trailDist = dist - trail * 18f * s.speed
                if (trailDist < 0f) continue
                val trailX = cx + cos(angleRad) * trailDist
                val trailY = cy + sin(angleRad) * trailDist
                val trailAlpha = (alpha * (0.35f - trail * 0.1f)).coerceIn(0f, 0.35f)
                val trailSize = s.size * scale * (0.3f + (1f - trail.toFloat() / s.trailCount) * 0.3f)
                drawCircle(
                    color = burstOrange.copy(alpha = trailAlpha),
                    radius = trailSize,
                    center = Offset(trailX, trailY)
                )
            }

            // Outer glow
            drawCircle(
                color = burstBright.copy(alpha = alpha * 0.25f),
                radius = s.size * scale * 2f,
                center = Offset(posX, posY)
            )

            // Star shape
            val starRadius = s.size * scale
            val rotation = s.rotSpeed * t
            rotate(degrees = rotation, pivot = Offset(posX, posY)) {
                drawBurstStar(
                    center = Offset(posX, posY),
                    outerRadius = starRadius,
                    innerRadius = starRadius * 0.42f,
                    color = s.color.copy(alpha = alpha),
                    points = 5
                )
                // Bright inner core
                drawBurstStar(
                    center = Offset(posX, posY),
                    outerRadius = starRadius * 0.5f,
                    innerRadius = starRadius * 0.22f,
                    color = burstBright.copy(alpha = alpha * 0.8f),
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
