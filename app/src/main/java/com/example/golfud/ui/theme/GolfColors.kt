package com.example.golfud.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class GolfColorPalette(
    val skyTop: Color,
    val skyBottom: Color,
    val grass: Color,
    val grassDark: Color,
    val buttonPrimary: Color,
    val buttonSecondary: Color,
    val titleShadow: Color,
    val ballHighlight: Color,
    val ballDimple: Color
)

val LocalGolfColors = staticCompositionLocalOf {
    GolfColorPalette(
        skyTop = SkyTop,
        skyBottom = SkyBottom,
        grass = GrassColor,
        grassDark = GrassDark,
        buttonPrimary = ButtonGreen,
        buttonSecondary = ButtonRed,
        titleShadow = TitleShadow,
        ballHighlight = BallHighlight,
        ballDimple = BallDimple
    )
}