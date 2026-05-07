package com.squaregarden.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.squaregarden.model.GameDifficulty
import com.squaregarden.model.Goal
import com.squaregarden.model.TileColor
import com.squaregarden.ui.theme.*

@Composable
fun GoalPanel(
    goals: List<Goal>,
    completedIds: Set<String>,
    movesRemaining: Int = -1,
    movesMax: Int = -1,
    gameDifficulty: GameDifficulty? = null,
    modifier: Modifier = Modifier
) {
    val isSmallScreen = LocalConfiguration.current.screenWidthDp < 800

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(if (isSmallScreen) 12.dp else 16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (isSmallScreen) 10.dp else 14.dp,
                    vertical = if (isSmallScreen) 6.dp else 10.dp
                ),
            verticalArrangement = Arrangement.spacedBy(if (isSmallScreen) 1.dp else 3.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "GOALS",
                fontSize = if (isSmallScreen) 12.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.8.sp
            )
            goals.forEach { goal ->
                val completed = goal.id in completedIds
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (isSmallScreen) 4.dp else 6.dp)
                ) {
                    // Color swatch
                    Box(
                        modifier = Modifier
                            .size(if (isSmallScreen) 14.dp else 18.dp)
                            .clip(RoundedCornerShape(if (isSmallScreen) 4.dp else 5.dp))
                            .background(goal.color.toComposeColor())
                    )
                    Text(
                        text = goal.description,
                        fontSize = if (isSmallScreen) 12.sp else 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (completed)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.onBackground,
                        textDecoration = if (completed) TextDecoration.LineThrough else TextDecoration.None
                    )
                    if (completed) {
                        Text(
                            text = "\u2714",
                            fontSize = if (isSmallScreen) 12.sp else 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF43A047)
                        )
                    }
                }
            }

            // Moves + Difficulty row below goals
            if (movesRemaining >= 0) {
                Spacer(modifier = Modifier.height(if (isSmallScreen) 2.dp else 4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (isSmallScreen) 6.dp else 8.dp)
                ) {
                    val lowMoves = movesRemaining <= 3
                    Text(
                        text = "MOVES",
                        fontSize = if (isSmallScreen) 11.sp else 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.6.sp
                    )
                    Text(
                        text = "$movesRemaining",
                        fontFamily = DisplayFontFamily,
                        fontSize = if (isSmallScreen) 18.sp else 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (lowMoves) Color(0xFFC62828) else MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "/$movesMax",
                        fontSize = if (isSmallScreen) 11.sp else 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (gameDifficulty != null) {
                        Spacer(modifier = Modifier.weight(1f))
                        val diffColor = when (gameDifficulty) {
                            GameDifficulty.EASY -> Color(0xFF43A047)
                            GameDifficulty.MEDIUM -> Color(0xFF1E88E5)
                            GameDifficulty.HARD -> Color(0xFFEF6C00)
                            GameDifficulty.VERY_HARD -> Color(0xFFC62828)
                            GameDifficulty.EXTREMELY_HARD -> Color(0xFF6A1B9A)
                        }
                        Text(
                            text = gameDifficulty.label,
                            fontSize = if (isSmallScreen) 11.sp else 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = diffColor
                        )
                    }
                }
            }
        }
    }
}
