package com.example.golfud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.golfud.ui.screens.MainMenuScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf(Screen.MAIN_MENU) }

            when (currentScreen) {
                Screen.MAIN_MENU -> {
                    MainMenuScreen(
                        onPlayClicked = {
                            currentScreen = Screen.GAME
                        },
                        onExitClicked = {
                            finish()
                        }
                    )
                }
                Screen.GAME -> {
                    GolfGame(onReturnToMenu = {
                        currentScreen = Screen.MAIN_MENU
                    })
                }
                else -> {
                    // Handle other screens if needed
                }
            }
        }
    }
}

enum class Screen {
    MAIN_MENU,
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