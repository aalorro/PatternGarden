package com.squaregarden.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun PlayerBadge(
    avatarEmoji: String,
    playerLevel: Int,
    totalStars: Int,
    gamesPlayed: Int,
    lives: Int,
    perfectGames: Int = 0,
    onSettingsClick: () -> Unit,
    onExitClick: () -> Unit,
    onStarPositioned: ((Offset) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val isCompact = LocalConfiguration.current.screenWidthDp < 600

    // Animated star counter: counts up over max 5 seconds then snaps to total
    var displayedStars by remember { mutableIntStateOf(totalStars) }
    LaunchedEffect(totalStars) {
        if (totalStars > displayedStars) {
            val diff = totalStars - displayedStars
            val frameDelay = 50L
            val maxFrames = (5000L / frameDelay).toInt() // 100 frames in 5s
            val increment = maxOf(1, diff / maxFrames)
            while (displayedStars < totalStars) {
                delay(frameDelay)
                displayedStars = minOf(displayedStars + increment, totalStars)
            }
        } else {
            displayedStars = totalStars
        }
    }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(if (isCompact) 10.dp else 16.dp))
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(if (isCompact) 10.dp else 16.dp)
                )
                .clickable { showMenu = true }
                .padding(
                    horizontal = if (isCompact) 5.dp else 8.dp,
                    vertical = if (isCompact) 3.dp else 6.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (isCompact) 4.dp else 6.dp)
        ) {
            // Avatar (compact)
            BasReliefAvatar(
                emoji = avatarEmoji,
                size = if (isCompact) 32.dp else 48.dp,
                animate = false
            )

            // Level
            Text(
                text = "Lv$playerLevel",
                fontSize = if (isCompact) 13.sp else 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            // Stars (with position tracking)
            Text(
                text = "$displayedStars\u2605",
                fontSize = if (isCompact) 12.sp else 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFD4A017),
                modifier = Modifier.onGloballyPositioned { coords ->
                    val pos = coords.positionInWindow()
                    val size = coords.size
                    onStarPositioned?.invoke(
                        Offset(pos.x + size.width / 2f, pos.y + size.height / 2f)
                    )
                }
            )

            // Lives
            Text(
                text = "\u2764".repeat(lives),
                fontSize = if (isCompact) 10.sp else 14.sp,
                color = Color(0xFFE53935)
            )

            // Perfect games (only if > 0)
            if (perfectGames > 0) {
                Text(
                    text = "\uD83C\uDFC6$perfectGames",
                    fontSize = if (isCompact) 10.sp else 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD4A017)
                )
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = {
                    showMenu = false
                    onSettingsClick()
                }
            )
            DropdownMenuItem(
                text = { Text("Exit") },
                onClick = {
                    showMenu = false
                    onExitClick()
                }
            )
        }
    }
}
