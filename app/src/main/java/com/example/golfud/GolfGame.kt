package com.example.golfud

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class GameStatus {
    PLAYING, GAME_OVER
}

@Composable
fun GolfGame(onReturnToMenu: () -> Unit = {}) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var gameStatus by remember { mutableStateOf(GameStatus.PLAYING) }
    var currentLevelIndex by remember { mutableIntStateOf(0) }
    var levels by remember { mutableStateOf<List<LevelData>>(emptyList()) }
    var ball by remember { mutableStateOf<Ball?>(null) }
    var boss by remember { mutableStateOf<Boss?>(null) }
    var strokes by remember { mutableIntStateOf(0) }
    var isLevelComplete by remember { mutableStateOf(false) }

    var dragStart by remember { mutableStateOf<Offset?>(null) }
    var dragEnd by remember { mutableStateOf<Offset?>(null) }

    var isDashMode by remember { mutableStateOf(false) }

    // Visual effects
    val goalScale = remember { Animatable(1f) }
    val bossHitFlash = remember { Animatable(0f) }
    val shakeOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    val infiniteTransition = rememberInfiniteTransition(label = "telegraph")
    val telegraphAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(180, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Screen dimensions initialization & level loading
    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0 && screenHeight > 0) {
            val loadedLevels = LevelManager.getLevels(screenWidth, screenHeight)
            levels = loadedLevels
            val initialLevel = loadedLevels[currentLevelIndex]
            ball = Ball(position = initialLevel.startPosition)
            boss = initialLevel.boss
            strokes = 0
            isLevelComplete = false
        }
    }

    // Reset when level index changes
    LaunchedEffect(currentLevelIndex, levels) {
        if (levels.isNotEmpty() && currentLevelIndex < levels.size) {
            val level = levels[currentLevelIndex]
            ball = Ball(position = level.startPosition)
            boss = level.boss
            strokes = 0
            isLevelComplete = false
        }
    }

    // Boss State Machine Loop: Keyed ONLY to (currentLevelIndex, gameStatus) so it NEVER restarts on state mutations
    LaunchedEffect(currentLevelIndex, gameStatus) {
        if (gameStatus != GameStatus.PLAYING) return@LaunchedEffect

        while (true) {
            val currentBoss = boss ?: break
            if (currentBoss.health <= 0) break

            val isRage = currentBoss.health.toFloat() / currentBoss.maxHealth <= 0.5f
            val moveDuration = if (isRage) 1400L else 2200L
            val telegraphDuration = if (isRage) 1000L else 1600L
            val attackDuration = if (isRage) 700L else 1000L

            // 1. IDLE & MOVEMENT PHASE
            boss = boss?.copy(state = BossState.IDLE)
            val waypoints = currentBoss.waypoints
            if (waypoints.isNotEmpty()) {
                val nextIdx = (currentBoss.currentWaypointIndex + 1) % waypoints.size
                val startPos = currentBoss.position
                val targetPos = waypoints[nextIdx]
                val startTime = System.currentTimeMillis()

                while (System.currentTimeMillis() - startTime < moveDuration) {
                    val progress = (System.currentTimeMillis() - startTime).toFloat() / moveDuration
                    val easedProgress = progress.coerceIn(0f, 1f)
                    val newPos = Vector2D(
                        startPos.x + (targetPos.x - startPos.x) * easedProgress,
                        startPos.y + (targetPos.y - startPos.y) * easedProgress
                    )
                    boss = boss?.copy(position = newPos, currentWaypointIndex = nextIdx)
                    delay(16)
                    if ((boss?.health ?: 0) <= 0) break
                }
            } else {
                delay(moveDuration)
            }
            if ((boss?.health ?: 0) <= 0) break

            // 2. TELEGRAPH PHASE (Directional warning & flashing)
            boss = boss?.copy(state = BossState.TELEGRAPH)
            val telegraphStart = System.currentTimeMillis()
            while (System.currentTimeMillis() - telegraphStart < telegraphDuration) {
                // Update dynamic attack zone (e.g. tracking ball for homing attacks)
                val activeBoss = boss ?: break
                if (activeBoss.attackType == AttackType.HOMING_BEAM && ball != null) {
                    val targetX = ball!!.position.x
                    boss = activeBoss.copy(attackZone = Rect(targetX - 45f, 0f, targetX + 45f, screenHeight))
                } else if (activeBoss.attackType == AttackType.HORIZONTAL_BEAM) {
                    val y = activeBoss.position.y
                    boss = activeBoss.copy(attackZone = Rect(0f, y - 45f, screenWidth, y + 45f))
                } else if (activeBoss.attackType == AttackType.VERTICAL_BEAM) {
                    val x = activeBoss.position.x
                    boss = activeBoss.copy(attackZone = Rect(x - 45f, 0f, x + 45f, screenHeight))
                }
                delay(16)
                if ((boss?.health ?: 0) <= 0) break
            }
            if ((boss?.health ?: 0) <= 0) break

            // 3. ATTACK PHASE (Instakill activation & screen shake)
            boss = boss?.copy(state = BossState.ATTACK)
            shakeOffset.animateTo(Offset(Random.nextFloat() * 14f - 7f, Random.nextFloat() * 14f - 7f), tween(40))
            shakeOffset.animateTo(Offset.Zero, spring(dampingRatio = Spring.DampingRatioHighBouncy))
            delay(attackDuration)
            if ((boss?.health ?: 0) <= 0) break
        }
    }

    // Boss Projectile Loop: Keyed ONLY to (currentLevelIndex, gameStatus)
    LaunchedEffect(currentLevelIndex, gameStatus) {
        if (gameStatus != GameStatus.PLAYING) return@LaunchedEffect

        while (true) {
            delay(2200)
            val currentBoss = boss ?: continue
            if (currentBoss.health > 0 && currentBoss.state == BossState.IDLE) {
                val angle = Random.nextFloat() * 360f
                val rad = Math.toRadians(angle.toDouble())
                val vel = Vector2D(Math.cos(rad).toFloat() * 5.5f, Math.sin(rad).toFloat() * 5.5f)
                val p = Projectile(position = currentBoss.position, velocity = vel)
                boss = boss?.copy(projectiles = (boss?.projectiles ?: emptyList()) + p)
            }
        }
    }

    // Main Physics & Combat Loop: Keyed ONLY to (currentLevelIndex, gameStatus)
    LaunchedEffect(currentLevelIndex, gameStatus) {
        if (gameStatus != GameStatus.PLAYING) return@LaunchedEffect

        while (true) {
            delay(16)
            val currentBall = ball ?: continue
            val currentLevel = levels.getOrNull(currentLevelIndex) ?: continue
            val currentBoss = boss

            // 1. Update Projectiles & Check Player Hit
            if (currentBoss != null && currentBoss.projectiles.isNotEmpty()) {
                val updatedProjectiles = PhysicsEngine.updateProjectiles(currentBoss.projectiles, screenWidth, screenHeight)
                boss = boss?.copy(projectiles = updatedProjectiles)

                if (PhysicsEngine.isBallHitByProjectile(currentBall, updatedProjectiles)) {
                    // Instakill by projectile
                    ball = Ball(position = currentLevel.startPosition)
                    shakeOffset.animateTo(Offset(22f, 22f), tween(80))
                    shakeOffset.animateTo(Offset.Zero, spring())
                    continue
                }
            }

            // 2. Check Instakill by Boss Telegraphed Attack
            if (currentBoss != null && currentBoss.health > 0 && PhysicsEngine.isBallHitByAttack(currentBall, currentBoss)) {
                // Instakill by boss attack
                ball = Ball(position = currentLevel.startPosition)
                shakeOffset.animateTo(Offset(32f, 32f), tween(100))
                shakeOffset.animateTo(Offset.Zero, spring())
                continue
            }

            // 3. Check Golf Ball Hitting Boss (Combat Damage)
            if (currentBoss != null && currentBoss.health > 0 && PhysicsEngine.isBallHittingBoss(currentBall, currentBoss)) {
                if (currentBall.velocity.length() > 2.2f) {
                    val newHealth = (currentBoss.health - 1).coerceAtLeast(0)
                    boss = currentBoss.copy(health = newHealth)
                    bossHitFlash.animateTo(1f, tween(80))
                    bossHitFlash.animateTo(0f, tween(100))

                    // Bounce ball off boss
                    val diff = currentBall.position - currentBoss.position
                    val bounceDirection = if (diff.length() > 0) diff.normalized() else Vector2D(0f, 1f)
                    ball = currentBall.copy(velocity = bounceDirection * 14f)
                }
            }

            // 4. Update Ball Physics
            if (currentBall.velocity.length() > 0.1f) {
                ball = PhysicsEngine.updateBall(currentBall, currentLevel.obstacles, screenWidth, screenHeight)
            }

            // 5. Goal Check (Only active if Boss is absent or defeated)
            val isBossDefeated = boss == null || (boss?.health ?: 0) <= 0
            if (isBossDefeated && !isLevelComplete && PhysicsEngine.isGoal(currentBall, currentLevel.hole)) {
                isLevelComplete = true
                goalScale.animateTo(1.6f, tween(250))
                goalScale.animateTo(1f, tween(250))
                delay(600)

                if (currentLevelIndex < levels.size - 1) {
                    currentLevelIndex++
                } else {
                    gameStatus = GameStatus.GAME_OVER
                }
                break
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF388E3C))
            .onGloballyPositioned { coordinates ->
                screenWidth = coordinates.size.width.toFloat()
                screenHeight = coordinates.size.height.toFloat()
            }
    ) {
        when (gameStatus) {
            GameStatus.PLAYING -> {
                if (levels.isNotEmpty() && ball != null) {
                    val level = levels[currentLevelIndex]
                    val isBossDefeated = boss == null || (boss?.health ?: 0) <= 0
                    val isRage = boss != null && (boss!!.health.toFloat() / boss!!.maxHealth) <= 0.5f

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationX = shakeOffset.value.x
                                translationY = shakeOffset.value.y
                            }
                            .pointerInput(ball?.velocity?.length(), isDashMode) {
                                if ((ball?.velocity?.length() ?: 0f) < 0.5f && !isLevelComplete) {
                                    detectDragGestures(
                                        onDragStart = { offset -> dragStart = offset },
                                        onDragEnd = {
                                            if (dragStart != null && dragEnd != null) {
                                                val diff = dragStart!! - dragEnd!!
                                                val direction = Vector2D(diff.x, diff.y).normalized()
                                                if (isDashMode) {
                                                    ball = ball?.copy(velocity = direction * 18f)
                                                } else {
                                                    val power = diff.getDistance().coerceAtMost(320f) / 9.5f
                                                    ball = ball?.copy(velocity = direction * power)
                                                    strokes++
                                                }
                                            }
                                            dragStart = null
                                            dragEnd = null
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragEnd = (dragEnd ?: dragStart!!) + dragAmount
                                        }
                                    )
                                }
                            }
                    ) {
                        // Draw Goal Hole (Only visible when Boss is defeated)
                        if (isBossDefeated) {
                            drawCircle(
                                color = level.hole.color,
                                radius = level.hole.radius * goalScale.value,
                                center = level.hole.position.toOffset()
                            )
                            // Hole ring flag effect
                            drawCircle(
                                color = Color.Yellow,
                                radius = (level.hole.radius + 3f) * goalScale.value,
                                center = level.hole.position.toOffset(),
                                style = Stroke(width = 3f)
                            )
                        }

                        // Draw Obstacles
                        level.obstacles.forEach { obstacle ->
                            drawRect(color = obstacle.color, topLeft = obstacle.rect.topLeft, size = obstacle.rect.size)
                        }

                        // Draw Boss Attacks & Telegraphs
                        boss?.let { b ->
                            if (b.health > 0) {
                                // Draw Projectiles
                                b.projectiles.forEach { p ->
                                    drawCircle(color = Color(0xFFFF5252), radius = p.radius, center = p.position.toOffset())
                                    drawCircle(color = Color.Yellow, radius = p.radius * 0.4f, center = p.position.toOffset())
                                }

                                // Telegraph & Attack Visuals
                                if (b.state == BossState.TELEGRAPH || b.state == BossState.ATTACK) {
                                    val isAttacking = b.state == BossState.ATTACK
                                    val zoneColor = if (isAttacking) Color(0xFFFF1744).copy(alpha = 0.88f) else Color(0xFFFF1744).copy(alpha = telegraphAlpha * 0.55f)
                                    val strokeColor = if (isAttacking) Color.White else Color.Yellow

                                    when (b.attackType) {
                                        AttackType.ZONE -> {
                                            drawRect(color = zoneColor, topLeft = b.attackZone.topLeft, size = b.attackZone.size)
                                            drawRect(color = strokeColor, topLeft = b.attackZone.topLeft, size = b.attackZone.size, style = Stroke(width = 4f))
                                        }
                                        AttackType.VERTICAL_BEAM, AttackType.HOMING_BEAM -> {
                                            drawRect(color = zoneColor, topLeft = Offset(b.attackZone.left, 0f), size = androidx.compose.ui.geometry.Size(b.attackZone.width, size.height))
                                            drawLine(color = strokeColor, start = Offset(b.attackZone.left, 0f), end = Offset(b.attackZone.left, size.height), strokeWidth = 3f)
                                            drawLine(color = strokeColor, start = Offset(b.attackZone.right, 0f), end = Offset(b.attackZone.right, size.height), strokeWidth = 3f)
                                        }
                                        AttackType.HORIZONTAL_BEAM -> {
                                            drawRect(color = zoneColor, topLeft = Offset(0f, b.attackZone.top), size = androidx.compose.ui.geometry.Size(size.width, b.attackZone.height))
                                            drawLine(color = strokeColor, start = Offset(0f, b.attackZone.top), end = Offset(size.width, b.attackZone.top), strokeWidth = 3f)
                                            drawLine(color = strokeColor, start = Offset(0f, b.attackZone.bottom), end = Offset(size.width, b.attackZone.bottom), strokeWidth = 3f)
                                        }
                                        AttackType.CIRCLE_BURST -> {
                                            drawCircle(color = zoneColor, radius = 230f, center = b.position.toOffset())
                                            drawCircle(color = strokeColor, radius = 230f, center = b.position.toOffset(), style = Stroke(width = 4f))
                                        }
                                    }

                                    // Directional Telegraph Arrow from Boss
                                    if (b.state == BossState.TELEGRAPH) {
                                        drawLine(
                                            color = Color.Yellow,
                                            start = b.position.toOffset(),
                                            end = b.attackZone.center,
                                            strokeWidth = 4f
                                        )
                                    }
                                }

                                // Draw Boss Body
                                val baseColor = if (isRage) Color(0xFF7F0000) else Color(0xFFD32F2F)
                                val finalColor = if (bossHitFlash.value > 0.5f) Color.White else baseColor
                                drawCircle(color = finalColor, radius = b.radius, center = b.position.toOffset())
                                drawCircle(color = Color.Black, radius = b.radius, center = b.position.toOffset(), style = Stroke(width = 3f))

                                if (isRage) {
                                    drawCircle(color = Color.Red.copy(alpha = 0.35f), radius = b.radius + 12f, center = b.position.toOffset())
                                }
                            }
                        }

                        // Draw Golf Ball
                        ball?.let { bl ->
                            drawCircle(color = bl.color, radius = bl.radius, center = bl.position.toOffset())
                            drawCircle(color = Color.DarkGray, radius = bl.radius, center = bl.position.toOffset(), style = Stroke(width = 1.5f))

                            // Draw Aim Line
                            if (dragStart != null && dragEnd != null) {
                                val aimColor = if (isDashMode) Color.Cyan else Color.White
                                val lineEnd = bl.position.toOffset() + (dragStart!! - dragEnd!!)
                                drawLine(
                                    color = aimColor,
                                    start = bl.position.toOffset(),
                                    end = lineEnd,
                                    strokeWidth = 6f
                                )
                                drawCircle(color = aimColor, radius = 6f, center = lineEnd)
                            }
                        }
                    }

                    // UI Overlay HUD
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 44.dp, start = 16.dp, end = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LEVEL ${level.id} / ${levels.size}",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Strokes: $strokes (Par: ${level.par})",
                            color = Color(0xFFE0E0E0),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Movement Mode Selector Buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { isDashMode = false },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!isDashMode) Color.White else Color(0x66FFFFFF)
                                )
                            ) {
                                Text("⚔️ SHOT (ATTACK)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = { isDashMode = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDashMode) Color(0xFF00E5FF) else Color(0x66FFFFFF)
                                )
                            ) {
                                Text("💨 DASH (DODGE)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        // Boss Health Bar & Status Warning
                        boss?.let { b ->
                            if (b.health > 0) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (isRage) "⚠️ BOSS ENRAGED! (HP: ${b.health}/${b.maxHealth})" else "👾 BOSS HP: ${b.health} / ${b.maxHealth}",
                                    color = if (isRage) Color(0xFFFF5252) else Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { b.health.toFloat() / b.maxHealth },
                                    modifier = Modifier
                                        .width(240.dp)
                                        .height(10.dp),
                                    color = if (isRage) Color(0xFFFF1744) else Color(0xFFFF5252),
                                    trackColor = Color(0x88000000)
                                )

                                if (b.state == BossState.TELEGRAPH) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "⚡ DANGER! ATTACK INCOMING! ⚡",
                                        color = Color.Yellow,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "🎉 BOSS DEFEATED! PUTT INTO THE HOLE! ⛳",
                                    color = Color.Yellow,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
            GameStatus.GAME_OVER -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏆 VICTORY! 🏆", color = Color.Yellow, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("You defeated all 10 Bosses!", color = Color.White, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(
                            onClick = { onReturnToMenu() },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("Main Menu", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
