package com.xaarlox.pw01.task04.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = SunOrangeDark,
    onPrimary = Color(0xFF4A2100),
    primaryContainer = Color(0xFF6D3200),
    onPrimaryContainer = Color(0xFFFFDCC2),

    secondary = GoldenYellowDark,
    onSecondary = Color(0xFF3E2E00),

    tertiary = SunsetBrownDark,
    onTertiary = Color(0xFF4A1500),

    background = DarkWarmBackground,
    onBackground = WarmOnDarkSurface,

    surface = DarkWarmSurface,
    onSurface = WarmOnDarkSurface,
    surfaceVariant = Color(0xFF4A3527),
    onSurfaceVariant = Color(0xFFE8C4A0),

    outline = WarmOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = SunOrange,
    onPrimary = WarmOnPrimary,
    primaryContainer = SunOrangeLight,
    onPrimaryContainer = Color(0xFF3E1400),

    secondary = GoldenYellow,
    onSecondary = Color(0xFF3E2E00),
    secondaryContainer = GoldenYellowLight,
    onSecondaryContainer = Color(0xFF4A3800),

    tertiary = SunsetBrown,
    onTertiary = Color.White,

    background = CreamBackground,
    onBackground = WarmOnSurface,

    surface = CreamSurface,
    onSurface = WarmOnSurface,
    surfaceVariant = Color(0xFFFFE0B2),
    onSurfaceVariant = Color(0xFF5D4037),

    outline = WarmOutline
)

@Composable
fun EcoGridTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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