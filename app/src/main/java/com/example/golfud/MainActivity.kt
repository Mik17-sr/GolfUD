package com.example.golfud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.golfud.ui.screens.MainMenuScreen
import com.example.golfud.ui.screens.SelectCharacterScreen
import com.example.golfud.ui.screens.SelectLevelScreen
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.example.golfud.data.LevelDataProvider
import com.example.golfud.data.LevelInfo
import com.example.golfud.ui.screens.GolfGame
import com.example.golfud.ui.screens.SelectLevelScreen
import com.example.golfud.ui.screens.VictoryScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf(Screen.MAIN_MENU) }
            var selectedCharacterIndex by remember { mutableStateOf<Int?>(null) }
            var selectedLevelIndex by remember { mutableStateOf<Int?>(null) }
            val levels = remember {
                mutableStateListOf<LevelInfo>().apply {
                    addAll(LevelDataProvider.defaultLevels)
                }
            }
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    slideInVertically(
                        animationSpec = tween(500),
                        initialOffsetY = { fullHeight -> -fullHeight }
                    ) togetherWith slideOutVertically(
                        animationSpec = tween(500),
                        targetOffsetY = { fullHeight -> fullHeight }
                    )
                },
                label = "screenTransition"
            ) { screen ->
                when (screen) {
                    Screen.MAIN_MENU -> {
                        MainMenuScreen(
                            onPlayClicked = {
                                currentScreen = Screen.CHARACTER_SELECT
                            },
                            onExitClicked = {
                                finish()
                            }
                        )
                    }
                    Screen.CHARACTER_SELECT -> {
                        SelectCharacterScreen(
                            selectedIndex = selectedCharacterIndex,
                            onCharacterSelected = { index ->
                                selectedCharacterIndex = index
                            },
                            onSelectClicked = {
                                if (selectedCharacterIndex != null) {
                                    currentScreen = Screen.LEVEL_SELECT
                                }

                            },
                            onBackClicked = {
                                currentScreen = Screen.MAIN_MENU
                            }
                        )
                    }
                    Screen.LEVEL_SELECT -> {
                        SelectLevelScreen(
                            levels = levels,
                            selectedLevelIndex = selectedLevelIndex,
                            onLevelSelected = { index ->
                                selectedLevelIndex = index
                            },
                            onBackClicked = {
                                currentScreen = Screen.CHARACTER_SELECT
                            },
                            onPlayClicked = {
                                if (selectedLevelIndex != null){
                                    currentScreen = Screen.GAME
                                }
                            }
                        )
                    }
                    Screen.GAME -> {
                        GolfGame(
                            selectedLevelIndex = selectedLevelIndex ?: 0,
                            onReturnToMenu = {
                                currentScreen = Screen.LEVEL_SELECT
                            },
                            totalLevels = levels.size,
                            onLevelWon = { wonIndex ->
                                val nextIndex = wonIndex + 1
                                if (nextIndex in levels.indices && !levels[nextIndex].isUnlocked) {
                                    levels[nextIndex] = levels[nextIndex].copy(isUnlocked = true)
                                }
                            },
                            onNextLevel = {
                                val nextIndex = (selectedLevelIndex ?: 0) + 1
                                if (nextIndex in levels.indices) {
                                    selectedLevelIndex = nextIndex
                                }
                            },
                            victory = {
                                currentScreen = Screen.VICTORY
                            }
                        )
                    }

                    Screen.VICTORY -> {
                        VictoryScreen(
                            selectedCharacterIndex = selectedCharacterIndex,
                            onReturnToMenu = {
                                currentScreen = Screen.MAIN_MENU
                            }
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

enum class Screen {
    MAIN_MENU,
    CHARACTER_SELECT,
    LEVEL_SELECT,
    GAME,
    VICTORY
}
