package com.example.golfud.data

import androidx.compose.ui.graphics.Color
import com.example.golfud.R

data class LevelInfo(
    val id: Int,
    val name: String,
    val difficulty: String,
    val difficultyColor: Color,
    val stars: Int,
    val bossName: String,
    val bossDescription: String,
    val bossImageRes: Int,
    val levelImageRes: Int,
    val isUnlocked: Boolean = true
)

object LevelDataProvider {
    val defaultLevels = listOf(
        LevelInfo(
            id = 1,
            name = "CAMPUS DISTRITAL",
            difficulty = "FÁCIL",
            difficultyColor = Color(0xFF4CAF50),
            stars = 1,
            bossName = "BOSS: LIZCANO RECTOR UD",
            bossDescription = "Defiende tu promedio académico entre pasillos estudiantiles.",
            bossImageRes = R.drawable.enemy1_default,
            levelImageRes = R.drawable.scene1,
            isUnlocked = true
        ),
        LevelInfo(
            id = 2,
            name = "PLENARIA DEL CAOS",
            difficulty = "MEDIO",
            difficultyColor = Color(0xFFFF9800),
            stars = 2,
            bossName = "BOSS: POLO POLO",
            bossDescription = "Prepárate para esquivar trinos furiosos y debates encendidos en el recinto.",
            bossImageRes = R.drawable.enemy2_default,
            levelImageRes = R.drawable.scene2,
            isUnlocked = false
        ),
        LevelInfo(
            id = 3,
            name = "ESTADIO DE LA OPINIÓN",
            difficulty = "DIFÍCIL",
            difficultyColor = Color(0xFFF44336),
            stars = 3,
            bossName = "BOSS: VICKY DÁVILA",
            bossDescription = "Un green implacable bajo la lupa de los titulares de primicia mundial.",
            bossImageRes = R.drawable.enemy3_default,
            levelImageRes = R.drawable.scene3,
            isUnlocked = false
        ),
        LevelInfo(
            id = 4,
            name = "CASA ROSADA ECONÓMICA",
            difficulty = "EXTREMO",
            difficultyColor = Color(0xFF9C27B0),
            stars = 3,
            bossName = "BOSS: MILEI",
            bossDescription = "Un campo minado de motosierras y recortes fiscales donde el handicap sale carísimo.",
            bossImageRes = R.drawable.enemy4_default,
            levelImageRes = R.drawable.scene4,
            isUnlocked = false
        ),
        LevelInfo(
            id = 5,
            name = "TRIBUNAL DE TINTES",
            difficulty = "LEYENDA",
            difficultyColor = Color(0xFFE91E63),
            stars = 3,
            bossName = "BOSS: ABELARDO DE LA ESPRIELLA",
            bossDescription = "Un duelo de elegancia y retórica suprema donde cada golpe viene con armas a la italiana y fallo definitivo.",
            bossImageRes = R.drawable.enemy5_default,
            levelImageRes = R.drawable.scene5,
            isUnlocked = false
        )
    )
}