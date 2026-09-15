package com.example.golfud.data

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color

data class Vector2D(val x: Float, val y: Float) {
    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    operator fun minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)
    operator fun times(scale: Float) = Vector2D(x * scale, y * scale)
    fun length() = Math.sqrt((x * x + y * y).toDouble()).toFloat()
    fun normalized(): Vector2D {
        val l = length()
        return if (l > 0) Vector2D(x / l, y / l) else Vector2D(0f, 0f)
    }
    fun toOffset() = Offset(x, y)
}

data class Ball(
    val position: Vector2D,
    val velocity: Vector2D = Vector2D(0f, 0f),
    val radius: Float = 15f,
    val color: Color = Color.White
)

data class Hole(
    val position: Vector2D,
    val radius: Float = 25f,
    val color: Color = Color.Black
)

data class Obstacle(
    val rect: Rect,
    val color: Color = Color.DarkGray
)

data class Projectile(
    val position: Vector2D,
    val velocity: Vector2D,
    val radius: Float = 10f,
    val color: Color = Color.Red
)

enum class BossState {
    IDLE, TELEGRAPH, ATTACK
}

enum class AttackType {
    ZONE, VERTICAL_BEAM, HORIZONTAL_BEAM, CIRCLE_BURST, HOMING_BEAM
}

data class Boss(
    val position: Vector2D,
    val attackZone: Rect,
    val attackZones: List<Rect> = emptyList(),
    val state: BossState = BossState.IDLE,
    val stateTimer: Float = 0f,
    val color: Color = Color.Red,
    val health: Int = 100,
    val maxHealth: Int = 100,
    val waypoints: List<Vector2D> = emptyList(),
    val currentWaypointIndex: Int = 0,
    val attackType: AttackType = AttackType.ZONE,
    val radius: Float = 40f,
    val projectiles: List<Projectile> = emptyList(),
    val imageRes: Int? = null
)

data class LevelData(
    val id: Int,
    val startPosition: Vector2D,
    val hole: Hole,
    val obstacles: List<Obstacle> = emptyList(),
    val boss: Boss? = null,
    val par: Int = 3,
    val sceneImageRes: Int
)