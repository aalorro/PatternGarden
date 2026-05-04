package com.patterngarden.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patterngarden.model.Goal
import com.patterngarden.model.TileColor
import com.patterngarden.ui.theme.*

@Composable
fun GoalPanel(
    goals: List<Goal>,
    completedIds: Set<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Goals",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        goals.forEach { goal ->
            val completed = goal.id in completedIds
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Color dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(goal.color.toComposeColor())
                )
                Text(
                    text = goal.description,
                    fontSize = 14.sp,
                    color = if (completed) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onBackground,
                    fontWeight = if (completed) FontWeight.Bold else FontWeight.Normal,
                    textDecoration = if (completed) TextDecoration.LineThrough else TextDecoration.None
                )
                if (completed) {
                    Text(text = "Done", fontSize = 12.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
