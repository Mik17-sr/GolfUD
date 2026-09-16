package com.example.golfud.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.golfud.R

@Composable
fun VictoryScreen(
    selectedCharacterIndex: Int?,
    onReturnToMenu: () -> Unit
) {
    val imageResId = when (selectedCharacterIndex) {
        0 -> R.drawable.player1_victory
        1 -> R.drawable.player2_victory
        2 -> R.drawable.player4_victory
        3 -> R.drawable.player3_victory
        else -> R.drawable.player1_victory
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "¡Victoria!", color = Color.White, fontSize = 32.sp)
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "Personaje venciendo al jefe final",
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Button(onClick = onReturnToMenu) { Text("Volver al menú") }
    }
}