package com.squaregarden.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.squaregarden.ui.theme.DisplayFontFamily
import com.squaregarden.ui.theme.TileRed

@Composable
fun MoveCounter(
    remaining: Int,
    max: Int,
    difficultyLabel: String = "",
    modifier: Modifier = Modifier
) {
    val lowMoves = remaining <= 3

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MOVES",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.8.sp
            )
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "$remaining",
                    fontFamily = DisplayFontFamily,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (lowMoves) Color(0xFFC62828) else MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "/$max",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            if (difficultyLabel.isNotEmpty()) {
                Text(
                    text = difficultyLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
