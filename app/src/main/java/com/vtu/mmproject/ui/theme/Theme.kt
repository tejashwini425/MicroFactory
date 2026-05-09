package com.vtu.mmproject.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkMarketTeal,
    onPrimary = DeepTeal,
    primaryContainer = DarkCard,
    onPrimaryContainer = DarkMarketTeal,
    secondary = Indigo,
    secondaryContainer = DarkCard,
    tertiary = HarvestGold,
    background = DarkSurface,
    surface = DarkSurface,
    surfaceVariant = DarkCard,
    onBackground = Color(0xFFE6F2EF),
    onSurface = Color(0xFFE6F2EF),
    onSurfaceVariant = Color(0xFFB8C7C3),
    outline = Color(0xFF52615E)
)

private val LightColorScheme = lightColorScheme(
    primary = MarketTeal,
    onPrimary = Color.White,
    primaryContainer = FreshMint,
    onPrimaryContainer = DeepTeal,
    secondary = Indigo,
    onSecondary = Color.White,
    secondaryContainer = IndigoMist,
    onSecondaryContainer = Ink,
    tertiary = HarvestGold,
    onTertiary = Color.White,
    tertiaryContainer = PaleGold,
    background = Cloud,
    surface = CardWhite,
    surfaceVariant = MintMist,
    onBackground = Ink,
    onSurface = Ink,
    onSurfaceVariant = Slate,
    outline = Line,
    error = WarmCoral
)

@Composable
fun MmProjectTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
