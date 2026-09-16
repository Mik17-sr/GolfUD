package com.example.golfud.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.golfud.R
import com.example.golfud.ui.theme.GolfTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.golfud.data.LevelInfo

@Composable
fun SelectLevelScreen (
    levels: List<LevelInfo>,
    selectedLevelIndex: Int?,
    onLevelSelected: (Int) -> Unit,
    onBackClicked: () -> Unit,
    onPlayClicked: () -> Unit
) {
    val colors = GolfTheme.colors
    var isConfirmed by remember { mutableStateOf(false) }
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
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "SELECCIÓN\nDE NIVELES",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.titleShadow,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(x = 2.dp, y = 2.dp)
                )
                Text(
                    text = "SELECCIÓN\nDE NIVELES",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(levels) { index, level ->
                    val isChosen = selectedLevelIndex == index
                    LevelCard(
                        level = level,
                        isChosen = isChosen,
                        isConfirmed = isConfirmed && isChosen,
                        onClick = {
                            if (level.isUnlocked && !isConfirmed) {
                                onLevelSelected(index)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(
                    text = "REGRESAR",
                    backgroundColor = colors.buttonSecondary,
                    onClick = onBackClicked
                )

                SelectButton(
                    text = "JUGAR",
                    backgroundColor = if (selectedLevelIndex != null) colors.buttonPrimary else Color.Gray,
                    enabled = selectedLevelIndex != null && !isConfirmed,
                    onClick = {
                        if (selectedLevelIndex != null) {
                            isConfirmed = true
                            coroutineScope.launch {
                                delay(1500)
                                onPlayClicked()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LevelCard(
    level: LevelInfo,
    isChosen: Boolean,
    isConfirmed: Boolean,
    onClick: () -> Unit
) {
    val scale = if (isChosen) 1.03f else 1f

    val cardBackground = when {
        isConfirmed -> Color(0xFFFFD700)
        isChosen -> Color(0xFFE3F2FD)
        level.isUnlocked -> Color(0xFFFFFFFF)
        else -> Color(0xFFE0E0E0)
    }

    val borderColor = when {
        isChosen -> Color(0xFF2196F3)
        level.isUnlocked -> Color.LightGray
        else -> Color.DarkGray
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = if (isChosen) 12.dp else 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = cardBackground,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isChosen) 3.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = painterResource(id = level.levelImageRes),
                    contentDescription = "Level Scene",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (!level.isUnlocked) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(36.dp)
                        .background(Color(0xFF1E1E1E).copy(alpha = 0.85f), shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (level.isUnlocked) {
                        Text(
                            text = "${level.id}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    } else {
                        Text(
                            text = "🔒",
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = level.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (level.isUnlocked) Color(0xFF2C3E50) else Color(0xFF7F8C8D),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(if (level.isUnlocked) level.difficultyColor else Color.Gray, shape = RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = level.difficulty,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "★".repeat(level.stars) + "☆".repeat(5 - level.stars),
                                fontSize = 12.sp,
                                color = if (level.isUnlocked) Color(0xFFFFC107) else Color.Gray
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = level.bossImageRes),
                            contentDescription = "Boss",
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color.DarkGray, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(
                            modifier = Modifier.width(130.dp)
                        ) {
                            Text(
                                text = level.bossName,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = level.bossDescription,
                                fontSize = 8.sp,
                                color = Color(0xFF666666),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
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
        enabled = enabled,
        modifier = Modifier
            .width(150.dp)
            .height(50.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = Color.Gray
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
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
            .width(150.dp)
            .height(50.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}