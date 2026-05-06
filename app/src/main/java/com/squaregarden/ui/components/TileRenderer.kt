package com.squaregarden.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.squaregarden.model.TileColor
import com.squaregarden.ui.theme.*

fun TileColor.toComposeColor(): Color = when (this) {
    TileColor.RED -> TileRed
    TileColor.BLUE -> TileBlue
    TileColor.YELLOW -> TileYellow
    TileColor.GREEN -> TileGreen
    TileColor.ORANGE -> TileOrange
}

fun TileColor.toLightColor(): Color = when (this) {
    TileColor.RED -> TileRedLight
    TileColor.BLUE -> TileBlueLight
    TileColor.YELLOW -> TileYellowLight
    TileColor.GREEN -> TileGreenLight
    TileColor.ORANGE -> TileOrangeLight
}

fun TileColor.toDarkColor(): Color = when (this) {
    TileColor.RED -> TileRedDark
    TileColor.BLUE -> TileBlueDark
    TileColor.YELLOW -> TileYellowDark
    TileColor.GREEN -> TileGreenDark
    TileColor.ORANGE -> TileOrangeDark
}

fun DrawScope.drawEmbossedTile(
    color: TileColor,
    x: Float,
    y: Float,
    cs: Float,
    cornerR: Float
) {
    val tileInset = cs * 0.08f
    val embossWidth = cs * 0.045f
    val tileCorner = cornerR * 0.9f
    val tileX = x + tileInset
    val tileY = y + tileInset
    val tileW = cs - tileInset * 2
    val tileH = cs - tileInset * 2

    // Shadow edge (bottom-right)
    drawRoundRect(
        color = color.toDarkColor().copy(alpha = 0.55f),
        topLeft = Offset(tileX + embossWidth * 0.3f, tileY + embossWidth * 0.6f),
        size = Size(tileW, tileH),
        cornerRadius = CornerRadius(tileCorner)
    )

    // Main tile body
    drawRoundRect(
        color = color.toComposeColor(),
        topLeft = Offset(tileX, tileY),
        size = Size(tileW, tileH),
        cornerRadius = CornerRadius(tileCorner)
    )

    // Highlight edge (top-left inner bevel)
    drawRoundRect(
        color = color.toLightColor().copy(alpha = 0.6f),
        topLeft = Offset(tileX, tileY),
        size = Size(tileW - embossWidth, tileH - embossWidth),
        cornerRadius = CornerRadius(tileCorner)
    )

    // Re-draw center to clean the bevel
    drawRoundRect(
        color = color.toComposeColor(),
        topLeft = Offset(tileX + embossWidth, tileY + embossWidth),
        size = Size(tileW - embossWidth * 2, tileH - embossWidth * 2),
        cornerRadius = CornerRadius(tileCorner * 0.8f)
    )

    // Subtle inner glow / sheen at top
    drawRoundRect(
        color = Color.White.copy(alpha = 0.15f),
        topLeft = Offset(tileX + embossWidth * 1.5f, tileY + embossWidth),
        size = Size(tileW - embossWidth * 3, tileH * 0.35f),
        cornerRadius = CornerRadius(tileCorner * 0.7f)
    )
}

fun DrawScope.drawTileMotif(color: TileColor, x: Float, y: Float, cs: Float) {
    val cx = x + cs / 2
    val cy = y + cs / 2
    val motifColor = color.toDarkColor().copy(alpha = 0.25f)
    val motifSize = cs * 0.16f

    when (color) {
        TileColor.RED -> {
            drawCircle(color = motifColor, radius = motifSize * 0.4f, center = Offset(cx, cy))
            for (i in 0 until 6) {
                val angle = Math.toRadians(i * 60.0)
                val px = cx + (motifSize * Math.cos(angle)).toFloat()
                val py = cy + (motifSize * Math.sin(angle)).toFloat()
                drawCircle(color = motifColor, radius = motifSize * 0.35f, center = Offset(px, py))
            }
        }
        TileColor.BLUE -> {
            val path = Path().apply {
                moveTo(cx, cy - motifSize * 1.2f)
                cubicTo(
                    cx + motifSize, cy - motifSize * 0.2f,
                    cx + motifSize * 0.8f, cy + motifSize,
                    cx, cy + motifSize * 1.1f
                )
                cubicTo(
                    cx - motifSize * 0.8f, cy + motifSize,
                    cx - motifSize, cy - motifSize * 0.2f,
                    cx, cy - motifSize * 1.2f
                )
                close()
            }
            drawPath(path, color = motifColor)
        }
        TileColor.YELLOW -> {
            drawCircle(color = motifColor, radius = motifSize * 0.5f, center = Offset(cx, cy))
            for (i in 0 until 8) {
                val angle = Math.toRadians(i * 45.0)
                val sx = cx + (motifSize * 0.7f * Math.cos(angle)).toFloat()
                val sy = cy + (motifSize * 0.7f * Math.sin(angle)).toFloat()
                val ex = cx + (motifSize * 1.1f * Math.cos(angle)).toFloat()
                val ey = cy + (motifSize * 1.1f * Math.sin(angle)).toFloat()
                drawLine(color = motifColor, start = Offset(sx, sy), end = Offset(ex, ey), strokeWidth = 2f)
            }
        }
        TileColor.GREEN -> {
            val path = Path().apply {
                moveTo(cx - motifSize, cy)
                quadraticTo(cx, cy - motifSize * 1.3f, cx + motifSize, cy)
                quadraticTo(cx, cy + motifSize * 1.3f, cx - motifSize, cy)
                close()
            }
            drawPath(path, color = motifColor)
            drawLine(
                color = motifColor.copy(alpha = 0.5f),
                start = Offset(cx - motifSize * 0.6f, cy),
                end = Offset(cx + motifSize * 0.6f, cy),
                strokeWidth = 1.5f
            )
        }
        TileColor.ORANGE -> {
            drawCircle(color = motifColor, radius = motifSize * 0.9f, center = Offset(cx, cy))
            for (i in 0 until 6) {
                val angle = Math.toRadians(i * 60.0 + 30.0)
                val ex = cx + (motifSize * 0.85f * Math.cos(angle)).toFloat()
                val ey = cy + (motifSize * 0.85f * Math.sin(angle)).toFloat()
                drawLine(motifColor, Offset(cx, cy), Offset(ex, ey), strokeWidth = 1.5f)
            }
            drawCircle(color = motifColor.copy(alpha = 0.4f), radius = motifSize * 0.3f, center = Offset(cx, cy))
        }
    }
}
