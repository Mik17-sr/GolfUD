package com.example.golfud.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.golfud.ui.theme.GolfTheme

@Composable
fun MainMenuScreen(onPlayClicked: () -> Unit, onExitClicked: () -> Unit) {
    val colors = GolfTheme.colors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colors.skyTop, colors.skyBottom)
                )
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colors.grass, colors.grassDark)
                    )
                )
        )

        BouncingGolfBall(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "GOLF IT! UD",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.titleShadow,
                    modifier = Modifier.offset(x = 2.dp, y = 2.dp)
                )
                Text(
                    text = "GOLF IT! UD",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Text(
                text = "PPC Competition",
                fontSize = 15.sp,
                color = colors.titleShadow,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 48.dp)
            )

            MenuButton(
                text = "JUGAR",
                backgroundColor = colors.buttonPrimary,
                onClick = onPlayClicked
            )

            Spacer(modifier = Modifier.height(18.dp))

            MenuButton(
                text = "SALIR",
                backgroundColor = colors.buttonSecondary,
                onClick = onExitClicked
            )
        }
    }
}

@Composable
private fun MenuButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(240.dp)
            .height(58.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = Color.White
        )
    }
}

@Composable
private fun BouncingGolfBall(modifier: Modifier = Modifier) {
    val colors = GolfTheme.colors
    val maxBounceHeight = 220f
    val ballRadius = 40f

    val infiniteTransition = rememberInfiniteTransition(label = "golfBall")

    val bounceProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 900
                0f at 0 using FastOutSlowInEasing
                1f at 400 using LinearOutSlowInEasing
                0f at 900 using FastOutSlowInEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "bounceProgress"
    )

    val squash by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 900
                1f at 0
                0.55f at 40 using FastOutSlowInEasing
                1f at 150 using FastOutSlowInEasing
                1f at 750
                0.55f at 870 using FastOutSlowInEasing
                1f at 900
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "squash"
    )

    Canvas(
        modifier = modifier
            .width(160.dp)
            .height(280.dp)
    ) {
        val centerX = size.width / 2f
        val groundY = size.height - ballRadius - 20f
        val ballY = groundY - (bounceProgress * maxBounceHeight)

        val shadowScale = 1f - (bounceProgress * 0.6f)
        val shadowAlpha = (0.4f - (bounceProgress * 0.25f)).coerceAtLeast(0f)

        drawOval(
            color = Color.Black.copy(alpha = shadowAlpha),
            topLeft = Offset(centerX - (ballRadius * shadowScale), groundY + ballRadius * 0.6f),
            size = Size(ballRadius * 2 * shadowScale, ballRadius * 0.7f * shadowScale)
        )

        withTransform({
            scale(squash, 1f / squash, pivot = Offset(centerX, groundY + ballRadius))
        }) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, colors.ballHighlight),
                    center = Offset(centerX - ballRadius * 0.3f, ballY - ballRadius * 0.3f),
                    radius = ballRadius * 1.8f
                ),
                radius = ballRadius,
                center = Offset(centerX, ballY)
            )

            val dimpleRadius = ballRadius * 0.08f
            val dimpleOffsets = listOf(
                Offset(-0.3f, -0.3f), Offset(0.2f, -0.35f), Offset(-0.1f, 0f),
                Offset(0.35f, 0.05f), Offset(-0.35f, 0.15f), Offset(0.05f, 0.3f)
            )
            dimpleOffsets.forEach { (dx, dy) ->
                drawCircle(
                    color = colors.ballDimple,
                    radius = dimpleRadius,
                    center = Offset(centerX + dx * ballRadius, ballY + dy * ballRadius)
                )
            }
        }
    }
}