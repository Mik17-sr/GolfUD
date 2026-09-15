package com.example.golfud.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun GolfUDTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalGolfColors provides GolfColorPalette(
        skyTop = SkyTop,
        skyBottom = SkyBottom,
        grass = GrassColor,
        grassDark = GrassDark,
        buttonPrimary = ButtonGreen,
        buttonSecondary = ButtonRed,
        titleShadow = TitleShadow,
        ballHighlight = BallHighlight,
        ballDimple = BallDimple
    )) {
        MaterialTheme(content = content)
    }
}

object GolfTheme {
    val colors: GolfColorPalette
        @Composable
        get() = LocalGolfColors.current
}