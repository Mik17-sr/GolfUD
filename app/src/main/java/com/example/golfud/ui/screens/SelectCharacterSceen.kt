package com.example.golfud.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.golfud.R
import com.example.golfud.ui.theme.GolfTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SelectCharacterScreen(
    selectedIndex: Int?,
    onCharacterSelected: (Int) -> Unit,
    onBackClicked: () -> Unit,
    onSelectClicked: () -> Unit
) {
    val colors = GolfTheme.colors
    var isConfirmed by remember { mutableStateOf(false) }
    val characterNames = listOf("Triple T", "Yermo", "Conker", "Crash")
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colors.skyTop, colors.skyBottom)
                )
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colors.grass, colors.grassDark)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 36.dp)
            ) {
                Text(
                    text = "SELECT\nCHARACTER",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.titleShadow,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(x = 2.dp, y = 2.dp)
                )
                Text(
                    text = "SELECT\nCHARACTER",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isConfirmed0 = isConfirmed && selectedIndex == 0
                val isConfirmed1 = isConfirmed && selectedIndex == 1
                val isConfirmed2 = isConfirmed && selectedIndex == 2
                val isConfirmed3 = isConfirmed && selectedIndex == 3
                CharacterCard(
                    isChosen = selectedIndex == 0,
                    imageResId = if (isConfirmed0) R.drawable.player1_selected else R.drawable.player1_default,
                    onClick = { onCharacterSelected(0) }
                )
                CharacterCard(
                    isChosen = selectedIndex == 1,
                    imageResId = if (isConfirmed1) R.drawable.player2_selected else R.drawable.player2_default,
                    onClick = { onCharacterSelected(1) }
                )
                CharacterCard(
                    isChosen = selectedIndex == 2,
                    imageResId = if (isConfirmed2) R.drawable.player3_selected else R.drawable.player3_default,
                    onClick = { onCharacterSelected(2) }
                )
                CharacterCard(
                    isChosen = selectedIndex == 3,
                    imageResId = if (isConfirmed3) R.drawable.player4_selected else R.drawable.player4_default,
                    onClick = { onCharacterSelected(3) }
                )
            }
            Text(
                text = selectedIndex?.let { characterNames[it] } ?: "Selecciona un personaje",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            SelectButton(
                text = "SELECCIONAR",
                backgroundColor = if (selectedIndex != null) colors.buttonPrimary else Color.Gray,
                enabled = selectedIndex != null,
                onClick = {
                    if ( selectedIndex != null) {
                        isConfirmed = true
                        coroutineScope.launch {
                            delay(1500)
                            onSelectClicked()
                        }
                    }
                }
            )

            BackButton(
                text = "REGRESAR",
                backgroundColor = colors.buttonSecondary,
                onClick = onBackClicked
            )
        }


    }
}



@Composable
private fun CharacterCard(
    isChosen: Boolean,
    imageResId: Int?,
    onClick: () -> Unit
) {
    val colors = GolfTheme.colors
    val scale = if (isChosen) 1.12f else 1f

    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .shadow(
                elevation = if (isChosen) 16.dp else 6.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = if (isChosen) Color(0xFFFFD700) else colors.buttonPrimary,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isChosen) 3.dp else 0.dp,
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imageResId != null) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Character",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            )
        } else {
            Text(
                text = "?",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
@Composable
private fun SelectButton(
    text: String,
    backgroundColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(240.dp)
            .height(58.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = Color.White
        )
    }
}

@Composable
private fun BackButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(240.dp)
            .height(58.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = Color.White
        )
    }
}