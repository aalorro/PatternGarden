package com.squaregarden.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.*
import kotlin.random.Random

private data class ConfettiParticle(
    val x: Float,         // 0..1 normalized start X
    val speed: Float,      // fall speed multiplier
    val amplitude: Float,  // horizontal sway
    val phase: Float,      // sway phase offset
    val rotation: Float,   // initial rotation
    val rotSpeed: Float,   // rotation speed
    val width: Float,      // particle width dp
    val height: Float,     // particle height dp
    val color: Color
)

private val confettiColors = listOf(
    Color(0xFFFF6B6B), // red
    Color(0xFFFFD93D), // yellow
    Color(0xFF6BCB77), // green
    Color(0xFF4D96FF), // blue
    Color(0xFFFF9ECD), // pink
    Color(0xFFAB46D2), // purple
    Color(0xFFFF8E3C), // orange
    Color(0xFF00D2FF), // cyan
)

@Composable
fun ConfettiOverlay(stars: Int) {
    val particleCount = when (stars) {
        3 -> 120
        2 -> 70
        else -> 35
    }
    val durationMs = when (stars) {
        3 -> 4000
        2 -> 3000
        else -> 2000
    }

    val particles = remember(stars) {
        List(particleCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                speed = 0.5f + Random.nextFloat() * 1f,
                amplitude = 0.02f + Random.nextFloat() * 0.06f,
                phase = Random.nextFloat() * 2f * PI.toFloat(),
                rotation = Random.nextFloat() * 360f,
                rotSpeed = 100f + Random.nextFloat() * 400f,
                width = 4f + Random.nextFloat() * 6f,
                height = 6f + Random.nextFloat() * 10f,
                color = confettiColors[Random.nextInt(confettiColors.size)]
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

        particles.forEach { p ->
            // Stagger start: faster particles start earlier
            val staggeredT = ((t * (1f + p.speed * 0.3f)) - (1f - p.speed) * 0.1f).coerceIn(0f, 1f)
            if (staggeredT <= 0f) return@forEach

            val yPos = -20f + (h + 40f) * staggeredT * p.speed
            if (yPos > h + 20f) return@forEach

            val xSway = sin(staggeredT * 8f + p.phase) * p.amplitude * w
            val xPos = p.x * w + xSway

            // Fade out in the last 20%
            val alpha = if (t > 0.8f) (1f - t) / 0.2f else 1f

            val rot = p.rotation + p.rotSpeed * t

            rotate(degrees = rot, pivot = Offset(xPos, yPos)) {
                drawRect(
                    color = p.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                    topLeft = Offset(xPos - p.width / 2f, yPos - p.height / 2f),
                    size = Size(p.width, p.height)
                )
            }
        }
    }
}
