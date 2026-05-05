package com.squaregarden.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.squaregarden.ui.theme.TileYellow

@Composable
fun StarDisplay(
    stars: Int,
    maxStars: Int = stars,
    fontSize: TextUnit = 28.sp,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(maxStars) { index ->
            Text(
                text = if (index < stars) "★" else "☆",
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = if (index < stars) TileYellow else Color.Gray.copy(alpha = 0.4f)
            )
        }
    }
}
