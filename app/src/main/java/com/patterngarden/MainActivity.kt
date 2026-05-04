package com.patterngarden

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.patterngarden.data.ProfileRepository
import com.patterngarden.data.ProgressRepository
import com.patterngarden.model.UserProfile
import com.patterngarden.ui.components.PlayerBadge
import com.patterngarden.ui.components.getAvatar
import com.patterngarden.ui.navigation.PatternGardenNavGraph
import com.patterngarden.ui.navigation.Screen
import com.patterngarden.ui.theme.PatternGardenTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val profileRepo = remember { ProfileRepository(context) }
            val progressRepo = remember { ProgressRepository(context) }

            // Observe profile and progress reactively
            val profile by profileRepo.profileFlow.collectAsState(initial = UserProfile())
            val totalStars by progressRepo.totalStarsFlow.collectAsState(initial = 0)
            val gamesPlayed by progressRepo.gamesPlayedFlow.collectAsState(initial = 0)
            val lives by progressRepo.livesFlow.collectAsState(initial = 3)
            val cooldownUntil by progressRepo.cooldownUntilFlow.collectAsState(initial = 0L)
            val scope = rememberCoroutineScope()

            // One-time migration + restore lives on launch
            LaunchedEffect(Unit) {
                progressRepo.migrateStarsIfNeeded()
                progressRepo.checkAndRestoreLives()
            }

            PatternGardenTheme(themeId = profile.themeId) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    PatternGardenNavGraph(navController = navController)

                    // Cooldown overlay when lives are exhausted
                    if (lives <= 0 && cooldownUntil > 0L) {
                        CooldownOverlay(
                            cooldownUntil = cooldownUntil,
                            onExpired = {
                                scope.launch {
                                    progressRepo.checkAndRestoreLives()
                                }
                            }
                        )
                    }

                    // Player badge in top-right corner
                    if (profile.isSetUp) {
                        val avatar = getAvatar(profile.avatarId)
                        PlayerBadge(
                            avatarEmoji = avatar.emoji,
                            playerLevel = profile.playerLevel,
                            totalStars = totalStars,
                            gamesPlayed = gamesPlayed,
                            lives = lives,
                            onSettingsClick = {
                                navController.navigate(Screen.Settings.route)
                            },
                            onExitClick = {
                                finish()
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .systemBarsPadding()
                                .padding(top = 8.dp, end = 12.dp)
                        )
                    }

                }
            }
        }
    }
}

@Composable
private fun CooldownOverlay(cooldownUntil: Long, onExpired: () -> Unit) {
    var remainingMs by remember { mutableLongStateOf(cooldownUntil - System.currentTimeMillis()) }

    LaunchedEffect(cooldownUntil) {
        while (true) {
            remainingMs = cooldownUntil - System.currentTimeMillis()
            if (remainingMs <= 0) {
                onExpired()
                break
            }
            delay(1000)
        }
    }

    if (remainingMs <= 0) return

    val minutes = (remainingMs / 60000).toInt()
    val seconds = ((remainingMs % 60000) / 1000).toInt()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(24.dp)
                )
                .padding(40.dp)
        ) {
            Text(
                text = "No Lives Left",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = "You've used all your lives.\nPlease wait to continue playing.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "%d:%02d".format(minutes, seconds),
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "until lives restore",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
