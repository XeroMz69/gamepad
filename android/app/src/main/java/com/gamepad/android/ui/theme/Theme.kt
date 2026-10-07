package com.gamepad.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF03DAC6),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFF1F1F1F),
    background = Color(0xFF121212),
    surface = Color(0xFF1F1F1F),
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF03DAC6),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFFE8E8E8),
    background = Color(0xFFFAFAFA),
    surface = Color(0xFFF5F5F5),
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun VirtualGamepadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
