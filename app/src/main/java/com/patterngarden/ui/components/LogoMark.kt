package com.patterngarden.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patterngarden.ui.theme.*

@Composable
fun LogoMark(size: Dp = 110.dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.width
        val tileSize = s * 0.42f
        val gap = s * 0.04f
        val r = tileSize * 0.22f

        data class TileData(val main: Color, val light: Color, val dark: Color)
        val tiles = listOf(
            TileData(TileRed, TileRedLight, TileRedDark),
            TileData(TileBlue, TileBlueLight, TileBlueDark),
            TileData(TileGreen, TileGreenLight, TileGreenDark),
            TileData(TileYellow, TileYellowLight, TileYellowDark)
        )

        val positions = listOf(
            Offset(s * 0.05f, s * 0.05f),
            Offset(s * 0.53f, s * 0.05f),
            Offset(s * 0.05f, s * 0.53f),
            Offset(s * 0.53f, s * 0.53f)
        )

        // Draw 4 tiles
        for (i in 0..3) {
            val pos = positions[i]
            val tile = tiles[i]

            // Gradient fill
            val brush = Brush.verticalGradient(
                colors = listOf(tile.light, tile.main, tile.dark),
                startY = pos.y,
                endY = pos.y + tileSize
            )
            drawRoundRect(
                brush = brush,
                topLeft = pos,
                size = Size(tileSize, tileSize),
                cornerRadius = CornerRadius(r)
            )

            // Border stroke
            drawRoundRect(
                color = tile.dark.copy(alpha = 0.4f),
                topLeft = pos,
                size = Size(tileSize, tileSize),
                cornerRadius = CornerRadius(r),
                style = Stroke(width = 1.5f)
            )
        }

        // Sprout in the center
        val cx = s / 2f
        val cy = s / 2f

        // Stem
        drawLine(
            color = Color(0xFF2E7D32),
            start = Offset(cx, cy + s * 0.05f),
            end = Offset(cx, cy - s * 0.12f),
            strokeWidth = s * 0.02f,
            cap = StrokeCap.Round
        )

        // Left leaf
        val leftLeaf = Path().apply {
            moveTo(cx, cy + s * 0.02f)
            quadraticTo(cx - s * 0.1f, cy - s * 0.02f, cx - s * 0.12f, cy - s * 0.1f)
            quadraticTo(cx - s * 0.05f, cy - s * 0.05f, cx, cy - s * 0.02f)
            close()
        }
        drawPath(leftLeaf, color = Color(0xFF4CAF50))

        // Right leaf
        val rightLeaf = Path().apply {
            moveTo(cx, cy + s * 0.02f)
            quadraticTo(cx + s * 0.1f, cy - s * 0.02f, cx + s * 0.12f, cy - s * 0.1f)
            quadraticTo(cx + s * 0.05f, cy - s * 0.05f, cx, cy - s * 0.02f)
            close()
        }
        drawPath(rightLeaf, color = Color(0xFF66BB6A))

        // Seed/bud at top
        drawCircle(
            color = Color(0xFFFFCA28),
            radius = s * 0.025f,
            center = Offset(cx, cy - s * 0.14f)
        )
    }
}
