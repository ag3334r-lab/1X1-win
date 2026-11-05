package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RoyalCasinoColorScheme = darkColorScheme(
    primary = ImperialGold,
    onPrimary = VelvetMaroonDark,
    primaryContainer = BurnishedGold,
    onPrimaryContainer = VelvetMaroonDark,
    secondary = EmeraldJade,
    onSecondary = VelvetMaroonDark,
    secondaryContainer = EmeraldDark,
    onSecondaryContainer = CreamWhite,
    tertiary = NeonMagenta,
    onTertiary = CreamWhite,
    background = VelvetMaroonDark,
    onBackground = CreamWhite,
    surface = RubySurface,
    onSurface = CreamWhite,
    surfaceVariant = RubySurfaceLight,
    onSurfaceVariant = PaleGold,
    error = CrimsonDanger,
    onError = CreamWhite
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RoyalCasinoColorScheme,
        typography = Typography,
        content = content
    )
}
