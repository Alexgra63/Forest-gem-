package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ForestColorScheme = darkColorScheme(
    primary = ForestEmerald,
    onPrimary = Color.Black,
    primaryContainer = ForestSurfaceVariant,
    onPrimaryContainer = ForestEmeraldLight,
    secondary = ForestGold,
    onSecondary = Color.Black,
    secondaryContainer = ForestSurfaceVariant,
    onSecondaryContainer = ForestGold,
    tertiary = FairyPink,
    onTertiary = Color.Black,
    background = ForestDarkBackground,
    onBackground = StarWhite,
    surface = ForestSurface,
    onSurface = StarWhite,
    surfaceVariant = ForestSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = ForestCardBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ForestColorScheme,
        typography = Typography,
        content = content
    )
}
