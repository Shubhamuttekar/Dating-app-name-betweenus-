package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val MidnightPresenceLightScheme = darkColorScheme(
    primary = MidnightPrimary,
    onPrimary = MidnightOnPrimary,
    primaryContainer = MidnightPrimaryContainer,
    onPrimaryContainer = MidnightOnPrimaryContainer,
    secondary = SoftLavender,
    onSecondary = MidnightOnPrimary,
    secondaryContainer = SoftLavenderContainer,
    onSecondaryContainer = OnSoftLavenderContainer,
    tertiary = MutedRose,
    onTertiary = MidnightOnPrimary,
    tertiaryContainer = MutedRoseContainer,
    onTertiaryContainer = OnMutedRoseContainer,
    background = WarmCreamBackground,
    onBackground = OnWarmCreamBackground,
    surface = SurfaceIvory,
    onSurface = OnSurfaceIvory,
    surfaceVariant = SurfaceIvory,
    onSurfaceVariant = OnSurfaceVariantMuted,
    outline = OutlineSoft,
    outlineVariant = OutlineVariantSoft
)

private val MidnightPresenceDarkScheme = darkColorScheme(
    primary = MidnightPrimary,
    onPrimary = MidnightOnPrimary,
    primaryContainer = MidnightPrimaryContainer,
    onPrimaryContainer = MidnightOnPrimaryContainer,
    secondary = SoftLavenderLight,
    onSecondary = MidnightOnPrimary,
    secondaryContainer = SoftLavenderContainer,
    onSecondaryContainer = OnSoftLavenderContainer,
    tertiary = MutedRoseAccent,
    onTertiary = MidnightOnPrimary,
    tertiaryContainer = MutedRoseContainer,
    onTertiaryContainer = OnMutedRoseContainer,
    background = WarmCreamBackground,
    onBackground = OnWarmCreamBackground,
    surface = SurfaceIvory,
    onSurface = OnSurfaceIvory,
    surfaceVariant = SurfaceIvory,
    onSurfaceVariant = OnSurfaceVariantMuted,
    outline = OutlineSoft,
    outlineVariant = OutlineVariantSoft
)

@Composable
fun MidnightPresenceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Force brand aesthetic by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> MidnightPresenceDarkScheme
        else -> MidnightPresenceLightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
