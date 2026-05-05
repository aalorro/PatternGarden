package com.squaregarden.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun SquareGardenTheme(
    themeId: String = "light",
    content: @Composable () -> Unit
) {
    val tc = themeById(themeId)

    val colorScheme = if (tc.isDark) {
        darkColorScheme(
            primary = tc.primary,
            onPrimary = tc.onPrimary,
            primaryContainer = tc.primaryContainer,
            onPrimaryContainer = tc.onPrimaryContainer,
            secondary = tc.secondary,
            onSecondary = tc.onSecondary,
            background = tc.background,
            onBackground = tc.onBackground,
            surface = tc.surface,
            onSurface = tc.onSurface,
            surfaceVariant = tc.surfaceVariant,
            onSurfaceVariant = tc.onSurfaceVariant
        )
    } else {
        lightColorScheme(
            primary = tc.primary,
            onPrimary = tc.onPrimary,
            primaryContainer = tc.primaryContainer,
            onPrimaryContainer = tc.onPrimaryContainer,
            secondary = tc.secondary,
            onSecondary = tc.onSecondary,
            background = tc.background,
            onBackground = tc.onBackground,
            surface = tc.surface,
            onSurface = tc.onSurface,
            surfaceVariant = tc.surfaceVariant,
            onSurfaceVariant = tc.onSurfaceVariant
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SquareGardenTypography,
        content = content
    )
}
