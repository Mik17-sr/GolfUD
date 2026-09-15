package com.example.golfud.data

import androidx.compose.ui.geometry.Rect
import com.example.golfud.R

object LevelManager {
    fun getLevels(screenWidth: Float, screenHeight: Float): List<LevelData> {
        val w = screenWidth
        val h = screenHeight
        return listOf(
            LevelData(
                id = 1,
                startPosition = Vector2D(w / 2, h * 0.82f),
                hole = Hole(Vector2D(w / 2, h * 0.18f)),
                boss = Boss(
                    position = Vector2D(w / 2, h * 0.25f),
                    attackZone = Rect(0f, h * 0.45f, w, h * 0.58f),
                    health = 3,
                    maxHealth = 3,
                    waypoints = listOf(Vector2D(w * 0.25f, h * 0.25f), Vector2D(w * 0.75f, h * 0.25f)),
                    attackType = AttackType.HORIZONTAL_BEAM,
                    radius = 90f,
                    imageRes = R.drawable.enemy1_fullbody
                ),
                par = 3,
                sceneImageRes = R.drawable.stadium1
            ),
            LevelData(
                id = 2,
                startPosition = Vector2D(w / 2, h * 0.82f),
                hole = Hole(Vector2D(w / 2, h * 0.18f)),
                boss = Boss(
                    position = Vector2D(w * 0.3f, h * 0.25f),
                    attackZone = Rect(w * 0.4f, 0f, w * 0.6f, h),
                    health = 4,
                    maxHealth = 4,
                    waypoints = listOf(Vector2D(w * 0.2f, h * 0.25f), Vector2D(w * 0.8f, h * 0.25f)),
                    attackType = AttackType.VERTICAL_BEAM,
                    radius = 90f,
                    imageRes = R.drawable.enemy2_fullbody
                ),
                par = 4,
                sceneImageRes = R.drawable.stadium2
            ),
            LevelData(
                id = 3,
                startPosition = Vector2D(w / 2, h * 0.85f),
                hole = Hole(Vector2D(w / 2, h * 0.15f)),
                obstacles = listOf(Obstacle(Rect(w * 0.25f, h * 0.48f, w * 0.75f, h * 0.52f))),
                boss = Boss(
                    position = Vector2D(w / 2, h * 0.3f),
                    attackZone = Rect(0f, 0f, 1f, 1f),
                    health = 5,
                    maxHealth = 5,
                    waypoints = listOf(Vector2D(w * 0.3f, h * 0.28f), Vector2D(w * 0.7f, h * 0.28f), Vector2D(w * 0.5f, h * 0.4f)),
                    attackType = AttackType.CIRCLE_BURST,
                    radius = 90f,
                    imageRes = R.drawable.enemy3_fullbody
                ),
                par = 4,
                sceneImageRes = R.drawable.stadium3
            ),
            LevelData(
                id = 4,
                startPosition = Vector2D(w / 2, h * 0.88f),
                hole = Hole(Vector2D(w / 2, h * 0.12f)),
                obstacles = listOf(
                    Obstacle(Rect(0f, h * 0.35f, w * 0.32f, h * 0.45f)),
                    Obstacle(Rect(w * 0.68f, h * 0.35f, w, h * 0.45f)),
                    Obstacle(Rect(w * 0.25f, h * 0.62f, w * 0.75f, h * 0.67f))
                ),
                boss = Boss(
                    position = Vector2D(w / 2, h * 0.18f),
                    attackZone = Rect(w * 0.2f, h * 0.15f, w * 1f, h * 0.4f),
                    health = 6,
                    maxHealth = 6,
                    waypoints = listOf(Vector2D(w * 0.25f, h * 0.18f), Vector2D(w * 0.75f, h * 0.18f)),
                    attackType = AttackType.ZONE,
                    radius = 90f,
                    imageRes = R.drawable.enemy4_fullbody
                ),
                par = 5,
                sceneImageRes = R.drawable.stadium4
            ),
            LevelData(
                id = 5,
                startPosition = Vector2D(w * 0.15f, h * 0.88f),
                hole = Hole(Vector2D(w * 0.85f, h * 0.12f)),
                obstacles = listOf(
                    Obstacle(Rect(w * 0.3f, 0f, w * 0.38f, h * 0.65f)),
                    Obstacle(Rect(w * 0.62f, h * 0.35f, w * 0.7f, h))
                ),
                boss = Boss(
                    position = Vector2D(w / 2, h / 2),
                    attackZone = Rect(0f, 0f, w, h),
                    health = 8,
                    maxHealth = 8,
                    waypoints = listOf(
                        Vector2D(w * 0.18f, h * 0.22f),
                        Vector2D(w * 0.82f, h * 0.22f),
                        Vector2D(w * 0.82f, h * 0.78f),
                        Vector2D(w * 0.18f, h * 0.78f)
                    ),
                    attackType = AttackType.HOMING_BEAM,
                    radius = 90f,
                    imageRes = R.drawable.enemy5_fullbodydefeat
                ),
                par = 6,
                sceneImageRes = R.drawable.stadium5
            )
        )
    }
}