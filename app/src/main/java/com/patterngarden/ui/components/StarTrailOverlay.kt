package com.patterngarden.ui.components

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
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

private data class StarParticle(
    val startX: Float,
    val startY: Float,
    val controlOffsetX: Float,
    val controlOffsetY: Float,
    val delay: Float,
    val size: Float,
    val rotSpeed: Float,
    val trailLength: Int
)

private val starGold = Color(0xFFFFD700)
private val starBright = Color(0xFFFFF9C4)
private val starOrange = Color(0xFFFFAB00)

@Composable
fun StarTrailOverlay(
    starCount: Int,
    targetOffset: Offset,
    onComplete: () -> Unit,
    onStarLanded: () -> Unit = {}
) {
    val totalParticles = starCount.coerceIn(1, 12)
    val durationMs = 1800

    val particles = remember(starCount) {
        List(totalParticles) { i ->
            StarParticle(
                startX = 0.35f + Random.nextFloat() * 0.30f,
                startY = 0.35f + Random.nextFloat() * 0.20f,
                controlOffsetX = -0.25f + Random.nextFloat() * 0.50f,
                controlOffsetY = -0.30f + Random.nextFloat() * 0.15f,
                delay = i.toFloat() / totalParticles * 0.55f,
                size = 14f + Random.nextFloat() * 12f,
                rotSpeed = 250f + Random.nextFloat() * 350f,
                trailLength = 4 + Random.nextInt(4)
            )
        }
    }

    val progress = remember { Animatable(0f) }

    // Track which particles have landed to fire callbacks
    val landedCount = remember { mutableIntStateOf(0) }

    LaunchedEffect(starCount) {
        landedCount.intValue = 0
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMs, easing = LinearEasing)
        )
        onComplete()
    }

    // Fire onStarLanded each time a new particle reaches the target
    LaunchedEffect(starCount) {
        while (true) {
            delay(100)
            val t = progress.value
            var newLanded = 0
            for (p in particles) {
                val localT = ((t - p.delay) / (1f - p.delay)).coerceIn(0f, 1f)
                if (localT >= 0.95f) newLanded++
            }
            val prev = landedCount.intValue
            if (newLanded > prev) {
                repeat(newLanded - prev) { onStarLanded() }
                landedCount.intValue = newLanded
            }
            if (t >= 1f) break
        }
    }

    val t = progress.value

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val tx = if (targetOffset != Offset.Zero) targetOffset.x else w * 0.85f
        val ty = if (targetOffset != Offset.Zero) targetOffset.y else h * 0.06f

        particles.forEach { p ->
            val localT = ((t - p.delay) / (1f - p.delay)).coerceIn(0f, 1f)
            if (localT <= 0f) return@forEach

            val eased = 1f - (1f - localT).pow(2.5f)

            val sx = p.startX * w
            val sy = p.startY * h
            val cx = (sx + tx) / 2f + p.controlOffsetX * w
            val cy = (sy + ty) / 2f + p.controlOffsetY * h

            // Draw sparkle trail behind the star
            for (trail in p.trailLength downTo 1) {
                val trailT = (eased - trail * 0.04f).coerceIn(0f, 1f)
                val omt = 1f - trailT
                val trailX = omt * omt * sx + 2f * omt * trailT * cx + trailT * trailT * tx
                val trailY = omt * omt * sy + 2f * omt * trailT * cy + trailT * trailT * ty
                val trailAlpha = (0.4f - trail * 0.07f).coerceIn(0.05f, 0.4f)
                val trailSize = p.size * (0.3f + (1f - trail.toFloat() / p.trailLength) * 0.4f)
                drawCircle(
                    color = starOrange.copy(alpha = trailAlpha),
                    radius = trailSize,
                    center = Offset(trailX, trailY)
                )
            }

            // Main star position
            val oneMinusT = 1f - eased
            val posX = oneMinusT * oneMinusT * sx + 2f * oneMinusT * eased * cx + eased * eased * tx
            val posY = oneMinusT * oneMinusT * sy + 2f * oneMinusT * eased * cy + eased * eased * ty

            // Scale: burst bigger at start, shrink at end
            val scale = when {
                eased < 0.1f -> 0.5f + eased * 5f  // grow in
                eased > 0.85f -> 1f - (eased - 0.85f) * 4f  // shrink at target
                else -> 1f
            }.coerceIn(0.2f, 1.5f)

            val alpha = if (eased > 0.9f) ((1f - eased) / 0.1f).coerceIn(0f, 1f) else 1f

            // Outer glow
            drawCircle(
                color = starBright.copy(alpha = alpha * 0.35f),
                radius = p.size * scale * 2.5f,
                center = Offset(posX, posY)
            )
            // Mid glow
            drawCircle(
                color = starGold.copy(alpha = alpha * 0.5f),
                radius = p.size * scale * 1.6f,
                center = Offset(posX, posY)
            )

            // Star shape
            val starRadius = p.size * scale
            val rotation = p.rotSpeed * t
            rotate(degrees = rotation, pivot = Offset(posX, posY)) {
                drawStarShape(
                    center = Offset(posX, posY),
                    outerRadius = starRadius,
                    innerRadius = starRadius * 0.42f,
                    color = starGold.copy(alpha = alpha),
                    points = 5
                )
                // Bright inner core
                drawStarShape(
                    center = Offset(posX, posY),
                    outerRadius = starRadius * 0.55f,
                    innerRadius = starRadius * 0.25f,
                    color = starBright.copy(alpha = alpha * 0.8f),
                    points = 5
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStarShape(
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
