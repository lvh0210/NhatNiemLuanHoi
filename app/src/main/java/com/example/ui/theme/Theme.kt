package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val XianxiaColorScheme = darkColorScheme(
    primary = ImmortalGold,
    onPrimary = VoidBlack,
    primaryContainer = MysticSurfaceVariant,
    onPrimaryContainer = GoldenSun,
    secondary = SpiritCyan,
    onSecondary = VoidBlack,
    secondaryContainer = MysticSurfaceVariant,
    onSecondaryContainer = SpiritCyan,
    tertiary = LightningPurple,
    onTertiary = VoidBlack,
    background = VoidBlack,
    onBackground = TextPrimary,
    surface = DarkSlate,
    onSurface = TextPrimary,
    surfaceVariant = MysticSurface,
    onSurfaceVariant = TextSecondary,
    outline = MysticBorder,
    error = BloodRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for Xianxia atmosphere
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = XianxiaColorScheme,
        typography = Typography,
        content = content
    )
}
