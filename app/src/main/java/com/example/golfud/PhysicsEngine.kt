package com.example.golfud

import androidx.compose.ui.geometry.Rect
import kotlin.math.sqrt

object PhysicsEngine {
    private const val FRICTION = 0.985f
    private const val MIN_VELOCITY = 0.5f
    private const val BOUNCE_RESTITUTION = 0.8f

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

        // Wall collisions
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

        // Obstacle collisions
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
        
        // Check all active attack zones
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
