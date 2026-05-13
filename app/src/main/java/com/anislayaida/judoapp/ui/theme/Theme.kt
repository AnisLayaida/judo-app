package com.anislayaida.judoapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JudoColorScheme = darkColorScheme(
    primary = Red,
    onPrimary = White,
    secondary = Gold,
    onSecondary = Navy,
    tertiary = LightBlue,
    background = Navy,
    onBackground = White,
    surface = SurfaceBlue,
    onSurface = White,
    surfaceVariant = SurfaceBlue,
    onSurfaceVariant = White,
    error = Red,
    onError = White
)

@Composable
fun JudoAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JudoColorScheme,
        typography = Typography,
        content = content
    )
}