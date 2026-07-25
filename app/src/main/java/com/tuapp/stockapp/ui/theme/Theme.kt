package com.tuapp.stockapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryNavy,
    secondary = SecondarySlate,
    background = BackgroundCanvas,
    surface = SurfaceCard,
    onPrimary = SurfaceCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun VentasMaxiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}