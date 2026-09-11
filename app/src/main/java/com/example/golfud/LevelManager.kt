package com.example.golfud

import androidx.compose.ui.geometry.Rect

object LevelManager {
    fun getLevels(screenWidth: Float, screenHeight: Float): List<LevelData> {
        val w = screenWidth
        val h = screenHeight

        return listOf(
            // Level 1: Introduction - 3 Hits required, Horizontal Beam attack
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
                    attackType = AttackType.HORIZONTAL_BEAM
                ),
                par = 3
            ),
            // Level 2: Vertical Beam attack - 4 Hits
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
                    attackType = AttackType.VERTICAL_BEAM
                ),
                par = 4
            ),
            // Level 3: Circle Burst & Obstacle - 4 Hits
            LevelData(
                id = 3,
                startPosition = Vector2D(w / 2, h * 0.85f),
                hole = Hole(Vector2D(w / 2, h * 0.15f)),
                obstacles = listOf(Obstacle(Rect(w * 0.25f, h * 0.48f, w * 0.75f, h * 0.52f))),
                boss = Boss(
                    position = Vector2D(w / 2, h * 0.3f),
                    attackZone = Rect(0f, 0f, 1f, 1f),
                    health = 4,
                    maxHealth = 4,
                    waypoints = listOf(Vector2D(w * 0.3f, h * 0.28f), Vector2D(w * 0.7f, h * 0.28f), Vector2D(w * 0.5f, h * 0.4f)),
                    attackType = AttackType.CIRCLE_BURST
                ),
                par = 4
            ),
            // Level 4: Square Patrol & Danger Zone - 5 Hits
            LevelData(
                id = 4,
                startPosition = Vector2D(w * 0.2f, h * 0.82f),
                hole = Hole(Vector2D(w * 0.8f, h * 0.18f)),
                boss = Boss(
                    position = Vector2D(w * 0.5f, h * 0.5f),
                    attackZone = Rect(w * 0.15f, h * 0.35f, w * 0.85f, h * 0.65f),
                    health = 5,
                    maxHealth = 5,
                    waypoints = listOf(
                        Vector2D(w * 0.25f, h * 0.25f),
                        Vector2D(w * 0.75f, h * 0.25f),
                        Vector2D(w * 0.75f, h * 0.6f),
                        Vector2D(w * 0.25f, h * 0.6f)
                    ),
                    attackType = AttackType.ZONE
                ),
                par = 5
            ),
            // Level 5: Homing Beam (Tracks your ball!) - 6 Hits
            LevelData(
                id = 5,
                startPosition = Vector2D(w / 2, h * 0.82f),
                hole = Hole(Vector2D(w / 2, h * 0.18f)),
                obstacles = listOf(
                    Obstacle(Rect(w * 0.2f, h * 0.4f, w * 0.4f, h * 0.45f)),
                    Obstacle(Rect(w * 0.6f, h * 0.4f, w * 0.8f, h * 0.45f))
                ),
                boss = Boss(
                    position = Vector2D(w / 2, h * 0.22f),
                    attackZone = Rect(0f, 0f, 90f, h),
                    health = 6,
                    maxHealth = 6,
                    waypoints = listOf(Vector2D(w * 0.2f, h * 0.22f), Vector2D(w * 0.8f, h * 0.22f)),
                    attackType = AttackType.HOMING_BEAM
                ),
                par = 6
            ),
            // Level 6: Split Path & Circle Burst - 7 Hits
            LevelData(
                id = 6,
                startPosition = Vector2D(w / 2, h * 0.88f),
                hole = Hole(Vector2D(w / 2, h * 0.12f)),
                obstacles = listOf(
                    Obstacle(Rect(w * 0.38f, h * 0.32f, w * 0.62f, h * 0.68f))
                ),
                boss = Boss(
                    position = Vector2D(w * 0.2f, h * 0.18f),
                    attackZone = Rect(0f, 0f, 1f, 1f),
                    health = 7,
                    maxHealth = 7,
                    waypoints = listOf(Vector2D(w * 0.2f, h * 0.18f), Vector2D(w * 0.8f, h * 0.18f), Vector2D(w * 0.5f, h * 0.25f)),
                    attackType = AttackType.CIRCLE_BURST
                ),
                par = 6
            ),
            // Level 7: Maze Navigation & Homing Beam - 8 Hits
            LevelData(
                id = 7,
                startPosition = Vector2D(w * 0.15f, h * 0.88f),
                hole = Hole(Vector2D(w * 0.85f, h * 0.12f)),
                obstacles = listOf(
                    Obstacle(Rect(0f, h * 0.3f, w * 0.7f, h * 0.35f)),
                    Obstacle(Rect(w * 0.3f, h * 0.65f, w, h * 0.7f))
                ),
                boss = Boss(
                    position = Vector2D(w * 0.5f, h * 0.5f),
                    attackZone = Rect(0f, 0f, 90f, h),
                    health = 8,
                    maxHealth = 8,
                    waypoints = listOf(Vector2D(w * 0.5f, h * 0.15f), Vector2D(w * 0.5f, h * 0.85f)),
                    attackType = AttackType.HOMING_BEAM
                ),
                par = 7
            ),
            // Level 8: Funnel & Danger Zone - 9 Hits
            LevelData(
                id = 8,
                startPosition = Vector2D(w / 2, h * 0.88f),
                hole = Hole(Vector2D(w / 2, h * 0.12f)),
                obstacles = listOf(
                    Obstacle(Rect(0f, h * 0.35f, w * 0.32f, h * 0.45f)),
                    Obstacle(Rect(w * 0.68f, h * 0.35f, w, h * 0.45f)),
                    Obstacle(Rect(w * 0.25f, h * 0.62f, w * 0.75f, h * 0.67f))
                ),
                boss = Boss(
                    position = Vector2D(w / 2, h * 0.18f),
                    attackZone = Rect(w * 0.2f, h * 0.15f, w * 0.8f, h * 0.4f),
                    health = 9,
                    maxHealth = 9,
                    waypoints = listOf(Vector2D(w * 0.25f, h * 0.18f), Vector2D(w * 0.75f, h * 0.18f)),
                    attackType = AttackType.ZONE
                ),
                par = 7
            ),
            // Level 9: Pillars of Doom & Homing Beam - 10 Hits
            LevelData(
                id = 9,
                startPosition = Vector2D(w / 2, h * 0.88f),
                hole = Hole(Vector2D(w / 2, h * 0.12f)),
                obstacles = listOf(
                    Obstacle(Rect(w * 0.18f, h * 0.32f, w * 0.32f, h * 0.42f)),
                    Obstacle(Rect(w * 0.68f, h * 0.32f, w * 0.82f, h * 0.42f)),
                    Obstacle(Rect(w * 0.18f, h * 0.62f, w * 0.32f, h * 0.72f)),
                    Obstacle(Rect(w * 0.68f, h * 0.62f, w * 0.82f, h * 0.72f)),
                    Obstacle(Rect(w * 0.44f, h * 0.48f, w * 0.56f, h * 0.58f))
                ),
                boss = Boss(
                    position = Vector2D(w * 0.85f, h * 0.5f),
                    attackZone = Rect(0f, 0f, 90f, h),
                    health = 10,
                    maxHealth = 10,
                    waypoints = listOf(Vector2D(w * 0.5f, h * 0.25f), Vector2D(w * 0.85f, h * 0.5f), Vector2D(w * 0.5f, h * 0.75f), Vector2D(w * 0.15f, h * 0.5f)),
                    attackType = AttackType.HOMING_BEAM
                ),
                par = 8
            ),
            // Level 10: Final Boss Battle - 12 Hits
            LevelData(
                id = 10,
                startPosition = Vector2D(w * 0.15f, h * 0.88f),
                hole = Hole(Vector2D(w * 0.85f, h * 0.12f)),
                obstacles = listOf(
                    Obstacle(Rect(w * 0.3f, 0f, w * 0.38f, h * 0.65f)),
                    Obstacle(Rect(w * 0.62f, h * 0.35f, w * 0.7f, h))
                ),
                boss = Boss(
                    position = Vector2D(w / 2, h / 2),
                    attackZone = Rect(0f, 0f, w, h),
                    health = 12,
                    maxHealth = 12,
                    waypoints = listOf(
                        Vector2D(w * 0.18f, h * 0.22f),
                        Vector2D(w * 0.82f, h * 0.22f),
                        Vector2D(w * 0.82f, h * 0.78f),
                        Vector2D(w * 0.18f, h * 0.78f)
                    ),
                    attackType = AttackType.ZONE
                ),
                par = 10
            )
        )
    }
}
