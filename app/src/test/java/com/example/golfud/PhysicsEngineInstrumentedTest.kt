package com.example.golfud

import androidx.compose.ui.geometry.Rect
import com.example.golfud.data.Ball
import com.example.golfud.data.Boss
import com.example.golfud.data.Hole
import com.example.golfud.data.PhysicsEngine
import com.example.golfud.data.Vector2D
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhysicsEngineInstrumentedTest {

    @Test
    fun updateBall_appliesFriction_ballSlowsDown() {
        val ball = Ball(position = Vector2D(200f, 200f), velocity = Vector2D(10f, 0f))
        val updated = PhysicsEngine.updateBall(
            ball = ball,
            obstacles = emptyList(),
            screenWidth = 1000f,
            screenHeight = 1000f
        )
        assertTrue(
            "Speed after friction should be lower than before",
            updated.velocity.length() < ball.velocity.length()
        )
    }

    @Test
    fun updateBall_belowMinVelocity_ballStops() {
        val ball = Ball(position = Vector2D(200f, 200f), velocity = Vector2D(0.1f, 0f))
        val updated = PhysicsEngine.updateBall(
            ball = ball,
            obstacles = emptyList(),
            screenWidth = 1000f,
            screenHeight = 1000f
        )
        assertEquals(0f, updated.velocity.x)
        assertEquals(0f, updated.velocity.y)
    }

    @Test
    fun isGoal_ballSlowAndInsideHole_returnsTrue() {
        val hole = Hole(position = Vector2D(500f, 500f), radius = 25f)
        val slowBallInHole = Ball(position = Vector2D(505f, 500f), velocity = Vector2D(2f, 0f))
        val fastBallInHole = Ball(position = Vector2D(505f, 500f), velocity = Vector2D(30f, 0f))
        val ballFarAway = Ball(position = Vector2D(0f, 0f), velocity = Vector2D(0f, 0f))

        assertTrue("Slow ball inside the hole should count as goal", PhysicsEngine.isGoal(slowBallInHole, hole))
        assertFalse("Ball moving too fast should not count as goal yet", PhysicsEngine.isGoal(fastBallInHole, hole))
        assertFalse("Ball far from the hole should not count as goal", PhysicsEngine.isGoal(ballFarAway, hole))
    }

    @Test
    fun isBallHittingBoss_detectsCollisionByDistance() {
        val boss = Boss(position = Vector2D(300f, 300f), attackZone = Rect(0f, 0f, 0f, 0f), radius = 40f)
        val closeBall = Ball(position = Vector2D(320f, 300f), radius = 15f)
        val farBall = Ball(position = Vector2D(700f, 700f), radius = 15f)

        assertTrue("Ball right next to the boss should count as a hit", PhysicsEngine.isBallHittingBoss(closeBall, boss))
        assertFalse("Ball far from the boss should not count as a hit", PhysicsEngine.isBallHittingBoss(farBall, boss))
    }
}