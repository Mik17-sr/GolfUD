package com.example.golfud.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.golfud.data.Ball
import com.example.golfud.data.BossState
import com.example.golfud.data.LevelManager
import com.example.golfud.data.PhysicsEngine
import com.example.golfud.sensors.SwingDetector
import kotlinx.coroutines.delay

private enum class GameStatus {
    AIMING,
    ROLLING,
    WON,
    LOST
}

@Composable
fun GolfGame(
    selectedLevelIndex: Int,
    onReturnToMenu: () -> Unit,
    totalLevels: Int,
    onLevelWon: (Int) -> Unit,
    onNextLevel: () -> Unit,
    victory: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.toFloat() * 2.5f
    val screenHeight = configuration.screenHeightDp.toFloat() * 2.5f

    val levelsList = remember { LevelManager.getLevels(screenWidth, screenHeight) }
    val currentLevel = levelsList.getOrElse(selectedLevelIndex) { levelsList[0] }

    var ball by remember (selectedLevelIndex) { mutableStateOf(Ball(position = currentLevel.startPosition)) }
    val hole = currentLevel.hole
    val obstacles = currentLevel.obstacles
    var boss by remember (selectedLevelIndex) { mutableStateOf(currentLevel.boss) }
    var projectiles by remember (selectedLevelIndex) { mutableStateOf(currentLevel.boss?.projectiles ?: emptyList()) }

    var status by remember (selectedLevelIndex) { mutableStateOf(GameStatus.AIMING) }
    var strokes by remember (selectedLevelIndex) { mutableStateOf(0) }

    val swingDetector = remember { SwingDetector(context) }

    DisposableEffect(selectedLevelIndex) {
        swingDetector.onSwingDetected = { impulse ->
            if (status == GameStatus.AIMING) {
                ball = ball.copy(velocity = impulse)
                strokes++
                status = GameStatus.ROLLING
            }
        }
        swingDetector.start()
        onDispose { swingDetector.stop() }
    }

    LaunchedEffect(status) {
        if (status == GameStatus.WON) {
            onLevelWon(selectedLevelIndex)
        }
    }

    LaunchedEffect(selectedLevelIndex) {
        while (true) {
            boss = boss?.let { PhysicsEngine.updateBoss(it, 0.016f, screenWidth, screenHeight) }

            if (status == GameStatus.ROLLING) {
                ball = PhysicsEngine.updateBall(ball, obstacles, screenWidth, screenHeight)
            }

            projectiles = PhysicsEngine.updateProjectiles(
                boss?.projectiles ?: projectiles,
                screenWidth,
                screenHeight
            )
            boss = boss?.copy(projectiles = projectiles)

            if (status == GameStatus.ROLLING) {
                val currentBoss = boss
                if (currentBoss != null) {
                    if (PhysicsEngine.isBallHittingBoss(ball, currentBoss)) {
                        status = GameStatus.LOST
                    } else if (PhysicsEngine.isBallHitByAttack(ball, currentBoss)) {
                        status = GameStatus.LOST
                    }
                }
                if (status == GameStatus.ROLLING && PhysicsEngine.isBallHitByProjectile(ball, projectiles)) {
                    status = GameStatus.LOST
                }
                if (status == GameStatus.ROLLING && PhysicsEngine.isGoal(ball, hole)) {
                    status = GameStatus.WON
                }
                if (status == GameStatus.ROLLING && ball.velocity.length() < 0.01f) {
                    status = GameStatus.AIMING
                }
            }

            delay(16L)
        }
    }

    swingDetector.enabled = (status == GameStatus.AIMING)

    val bossImage = boss?.imageRes?.let { painterResource(id = it) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = currentLevel.sceneImageRes),
            contentDescription = "Background Scene",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            obstacles.forEach { obstacle ->
                drawRect(color = obstacle.color, topLeft = obstacle.rect.topLeft, size = obstacle.rect.size)
            }

            drawCircle(color = hole.color, radius = hole.radius, center = hole.position.toOffset())

            boss?.let { b ->
                val zoneAlpha = if (b.state == BossState.ATTACK) 0.5f else 0.15f
                drawRect(
                    color = Color.Red.copy(alpha = zoneAlpha),
                    topLeft = b.attackZone.topLeft,
                    size = b.attackZone.size
                )

                if (bossImage != null) {
                    val size = Size(b.radius * 4, b.radius * 4)
                    translate(left = b.position.x - b.radius, top = b.position.y - b.radius) {
                        with(bossImage) {
                            draw(size = size)
                        }
                    }
                } else {
                    drawCircle(color = b.color, radius = b.radius, center = b.position.toOffset())
                }
            }

            projectiles.forEach { p ->
                drawCircle(color = p.color, radius = p.radius, center = p.position.toOffset())
            }

            drawCircle(color = ball.color, radius = ball.radius, center = ball.position.toOffset())

            if (status == GameStatus.AIMING) {
                swingDetector.previewVector?.let { vec ->
                    val start = ball.position.toOffset()
                    val visualScale = 3f
                    val end = Offset(
                        x = start.x + vec.x * visualScale,
                        y = start.y + vec.y * visualScale
                    )
                    drawLine(color = Color.Yellow, start = start, end = end, strokeWidth = 6f)
                    drawCircle(color = Color.Yellow, radius = 10f, center = end)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Golpes: $strokes", color = Color.White, fontSize = 18.sp)
                }
                Button(onClick = onReturnToMenu) { Text("Menú") }
            }

            when (status) {
                GameStatus.WON -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "¡Hoyo completado en $strokes golpes!", color = Color.White, fontSize = 22.sp)
                        if (selectedLevelIndex + 1 < totalLevels) {
                            Button(onClick = onNextLevel) { Text("Siguiente nivel")}
                        } else if (selectedLevelIndex == 4) {
                            Button(onClick = victory) { Text("Victoria")}
                        }
                        Button(onClick = onReturnToMenu) { Text("Volver al menú") }
                    }
                }
                GameStatus.LOST -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "¡Golpeado por el jefe!", color = Color.White, fontSize = 22.sp)
                        Button(onClick = {
                            ball = Ball(position = currentLevel.startPosition)
                            boss = currentLevel.boss
                            status = GameStatus.AIMING
                        }) { Text("Reintentar") }
                    }
                }
                else -> {}
            }
        }
    }
}