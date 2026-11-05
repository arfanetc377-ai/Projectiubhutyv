package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = darkColorScheme(
    primary = CyberPrimary,
    onPrimary = Color(0xFF1F0055),
    primaryContainer = CyberPrimaryContainer,
    onPrimaryContainer = Color(0xFFEADBFF),
    secondary = CyberSecondary,
    onSecondary = Color(0xFF00382E),
    secondaryContainer = CyberSecondaryContainer,
    onSecondaryContainer = Color(0xFF70FDE3),
    tertiary = CyberTertiary,
    onTertiary = Color(0xFF490025),
    tertiaryContainer = CyberTertiaryContainer,
    onTertiaryContainer = Color(0xFFFFD8E4),
    background = CyberBgDark,
    onBackground = TextPrimaryDark,
    surface = CyberSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = CyberSurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = CyberCardBorder,
    outlineVariant = Color(0x22FFFFFF)
)

@Composable
fun TabuAiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
