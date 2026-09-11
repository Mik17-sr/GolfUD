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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf(Screen.MAIN_MENU) }
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
                            onCharacterSelected = { characterIndex ->

                            },
                            onBackClicked = {
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
    GAME
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}