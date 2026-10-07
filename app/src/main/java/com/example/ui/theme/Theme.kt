package com.example.ui.theme

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

private val CyberDarkColorScheme = darkColorScheme(
    primary = CyberPrimary,
    onPrimary = CyberOnPrimary,
    primaryContainer = CyberPrimaryContainer,
    onPrimaryContainer = Color(0xFF8CF4FF),
    secondary = CyberSecondary,
    onSecondary = CyberOnSecondary,
    secondaryContainer = CyberSecondaryContainer,
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = CyberTertiary,
    onTertiary = Color.White,
    tertiaryContainer = CyberTertiaryContainer,
    background = CyberBg,
    onBackground = Color(0xFFF1F5F9),
    surface = CyberSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = CyberCardBorder,
    error = SecurityRed,
    onError = Color.White,
    errorContainer = SecurityRedBg,
    onErrorContainer = Color(0xFFFFDAD6)
)

private val CyberLightColorScheme = lightColorScheme(
    primary = CyberLightPrimary,
    onPrimary = CyberLightOnPrimary,
    primaryContainer = Color(0xFFBAE6FD),
    onPrimaryContainer = Color(0xFF001F2A),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    background = CyberLightBg,
    onBackground = Color(0xFF0F172A),
    surface = CyberLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = CyberLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    outline = CyberLightBorder,
    error = SecurityRed,
    onError = Color.White
)

@Composable
fun CyberGuardTheme(
    darkTheme: Boolean = true, // Default to dark-first cybersecurity interface
    dynamicColor: Boolean = false, // Keep consistent cyber aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> CyberDarkColorScheme
        else -> CyberLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
