package com.example.seapedia.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = SeaPrimary,
    onPrimary = SeaOnPrimary,
    primaryContainer = SeaPrimaryContainer,
    onPrimaryContainer = SeaOnPrimary,
    secondary = Coral,
    onSecondary = OnCoral,
    tertiary = SeaTertiary,
    onTertiary = Color.White,
    error = SeaError,
    background = Surface,
    onBackground = OnSurface,
    surface = SurfaceContainerLowest,
    onSurface = OnSurface,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary = SeaPrimaryContainer,
    onPrimary = SeaOnPrimary,
    primaryContainer = SeaPrimary,
    onPrimaryContainer = SeaOnPrimary,
    secondary = Coral,
    onSecondary = OnCoral,
    tertiary = SeaTertiary,
    onTertiary = Color.White,
    error = SeaError,
    background = Color(0xFF0F1416),
    onBackground = Color(0xFFE1E3E5),
    surface = Color(0xFF161C1E),
    onSurface = Color(0xFFE1E3E5),
    surfaceVariant = Color(0xFF222A2E),
    onSurfaceVariant = OutlineVariant,
    outline = Outline,
    outlineVariant = Color(0xFF3A4248),
)

@Composable
fun SeaPediaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color dimatikan agar warna brand SEAPEDIA (Oceanic) konsisten
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}