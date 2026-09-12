package com.example.golfud.data

import androidx.compose.ui.geometry.Rect
import kotlin.math.sqrt

object PhysicsEngine {
    private const val FRICTION = 0.985f
    private const val MIN_VELOCITY = 0.5f
    private const val BOUNCE_RESTITUTION = 0.8f
    private const val IDLE_DURATION = 1.5f
    private const val TELEGRAPH_DURATION = 0.8f
    private const val ATTACK_DURATION = 1.2f
    private const val BOSS_MOVE_SPEED = 60f

    fun updateBall(
        ball: Ball,
        obstacles: List<Obstacle>,
        screenWidth: Float,
        screenHeight: Float
    ): Ball {
        if (ball.velocity.length() < MIN_VELOCITY) {
            return ball.copy(velocity = Vector2D(0f, 0f))
        }

        var newVelocity = ball.velocity * FRICTION
        var newPosition = ball.position + newVelocity

        if (newPosition.x - ball.radius < 0) {
            newPosition = newPosition.copy(x = ball.radius)
            newVelocity = newVelocity.copy(x = -newVelocity.x * BOUNCE_RESTITUTION)
        } else if (newPosition.x + ball.radius > screenWidth) {
            newPosition = newPosition.copy(x = screenWidth - ball.radius)
            newVelocity = newVelocity.copy(x = -newVelocity.x * BOUNCE_RESTITUTION)
        }

        if (newPosition.y - ball.radius < 0) {
            newPosition = newPosition.copy(y = ball.radius)
            newVelocity = newVelocity.copy(y = -newVelocity.y * BOUNCE_RESTITUTION)
        } else if (newPosition.y + ball.radius > screenHeight) {
            newPosition = newPosition.copy(y = screenHeight - ball.radius)
            newVelocity = newVelocity.copy(y = -newVelocity.y * BOUNCE_RESTITUTION)
        }
        for (obstacle in obstacles) {
            val rect = obstacle.rect
            if (circleIntersectsRect(newPosition, ball.radius, rect)) {
                val centerX = rect.center.x
                val centerY = rect.center.y

                if (Math.abs(newPosition.x - centerX) * rect.height > Math.abs(newPosition.y - centerY) * rect.width) {
                    newVelocity = newVelocity.copy(x = -newVelocity.x * BOUNCE_RESTITUTION)
                    newPosition = newPosition.copy(x = ball.position.x)
                } else {
                    newVelocity = newVelocity.copy(y = -newVelocity.y * BOUNCE_RESTITUTION)
                    newPosition = newPosition.copy(y = ball.position.y)
                }
                break
            }
        }

        return ball.copy(position = newPosition, velocity = newVelocity)
    }

    fun updateProjectiles(
        projectiles: List<Projectile>,
        screenWidth: Float,
        screenHeight: Float
    ): List<Projectile> {
        return projectiles.map { p ->
            var newVel = p.velocity
            var newPos = p.position + newVel

            if (newPos.x < 0 || newPos.x > screenWidth) newVel = newVel.copy(x = -newVel.x)
            if (newPos.y < 0 || newPos.y > screenHeight) newVel = newVel.copy(y = -newVel.y)

            p.copy(position = p.position + newVel, velocity = newVel)
        }
    }

    fun updateBoss(boss: Boss, deltaTime: Float, screenWidth: Float, screenHeight: Float): Boss {
        var newStateTimer = boss.stateTimer + deltaTime
        var newState = boss.state
        var newProjectiles = boss.projectiles
        var newWaypointIndex = boss.currentWaypointIndex
        var newPosition = boss.position

        if (boss.waypoints.isNotEmpty()) {
            val target = boss.waypoints[boss.currentWaypointIndex]
            val toTarget = target - boss.position
            val dist = toTarget.length()
            if (dist < 4f) {
                newWaypointIndex = (boss.currentWaypointIndex + 1) % boss.waypoints.size
            } else {
                val step = toTarget.normalized() * BOSS_MOVE_SPEED * deltaTime
                newPosition = boss.position + step
            }
        }

        when (boss.state) {
            BossState.IDLE -> if (newStateTimer >= IDLE_DURATION) {
                newState = BossState.TELEGRAPH; newStateTimer = 0f
            }
            BossState.TELEGRAPH -> if (newStateTimer >= TELEGRAPH_DURATION) {
                newState = BossState.ATTACK; newStateTimer = 0f
                newProjectiles = boss.projectiles + spawnProjectiles(boss)
            }
            BossState.ATTACK -> if (newStateTimer >= ATTACK_DURATION) {
                newState = BossState.IDLE; newStateTimer = 0f
            }
        }

        val newZone = computeAttackZone(boss.attackType, newPosition, boss.attackZone, screenWidth, screenHeight)

        return boss.copy(
            state = newState,
            stateTimer = newStateTimer,
            position = newPosition,
            currentWaypointIndex = newWaypointIndex,
            projectiles = newProjectiles,
            attackZone = newZone
        )
    }

    private fun computeAttackZone(
        type: AttackType,
        position: Vector2D,
        currentZone: Rect,
        screenWidth: Float,
        screenHeight: Float
    ): Rect {
        return when (type) {
            AttackType.HORIZONTAL_BEAM -> {
                val thickness = currentZone.height
                Rect(0f, position.y - thickness / 2, screenWidth, position.y + thickness / 2)
            }
            AttackType.VERTICAL_BEAM -> {
                val thickness = currentZone.width
                Rect(position.x - thickness / 2, 0f, position.x + thickness / 2, screenHeight)
            }
            AttackType.ZONE -> {
                val w = currentZone.width
                val h = currentZone.height
                Rect(position.x - w / 2, position.y - h / 2, position.x + w / 2, position.y + h / 2)
            }
            else -> currentZone
        }
    }

    private fun spawnProjectiles(boss: Boss): List<Projectile> {
        return when (boss.attackType) {
            AttackType.CIRCLE_BURST -> {
                val count = 8
                (0 until count).map { i ->
                    val angle = (2 * Math.PI * i / count).toFloat()
                    val dir = Vector2D(kotlin.math.cos(angle), kotlin.math.sin(angle))
                    Projectile(position = boss.position, velocity = dir * 4f)
                }
            }
            AttackType.HOMING_BEAM -> listOf(
                Projectile(position = boss.position, velocity = Vector2D(0f, 5f))
            )
            else -> emptyList()
        }
    }

    private fun circleIntersectsRect(center: Vector2D, radius: Float, rect: Rect): Boolean {
        val closestX = clamp(center.x, rect.left, rect.right)
        val closestY = clamp(center.y, rect.top, rect.bottom)
        val distanceX = center.x - closestX
        val distanceY = center.y - closestY
        return (distanceX * distanceX + distanceY * distanceY) < (radius * radius)
    }

    private fun clamp(value: Float, min: Float, max: Float): Float {
        return if (value < min) min else if (value > max) max else value
    }

    fun isGoal(ball: Ball, hole: Hole): Boolean {
        val dist = (ball.position - hole.position).length()
        return dist < hole.radius && ball.velocity.length() < 25f
    }

    fun isBallHittingBoss(ball: Ball, boss: Boss): Boolean {
        val dist = (ball.position - boss.position).length()
        return dist < (ball.radius + boss.radius)
    }

    fun isBallHitByProjectile(ball: Ball, projectiles: List<Projectile>): Boolean {
        return projectiles.any { p ->
            (ball.position - p.position).length() < (ball.radius + p.radius)
        }
    }

    fun isBallHitByAttack(ball: Ball, boss: Boss): Boolean {
        if (boss.state != BossState.ATTACK) return false
        val zones = if (boss.attackZones.isNotEmpty()) boss.attackZones else listOf(boss.attackZone)
        return zones.any { zone ->
            when (boss.attackType) {
                AttackType.ZONE -> zone.contains(ball.position.toOffset())
                AttackType.VERTICAL_BEAM, AttackType.HOMING_BEAM -> {
                    val centerX = zone.center.x
                    Math.abs(ball.position.x - centerX) < (zone.width / 2)
                }
                AttackType.HORIZONTAL_BEAM -> {
                    val centerY = zone.center.y
                    Math.abs(ball.position.y - centerY) < (zone.height / 2)
                }
                AttackType.CIRCLE_BURST -> {
                    val dist = (ball.position - boss.position).length()
                    dist < 220f
                }
            }
        }
    }
}