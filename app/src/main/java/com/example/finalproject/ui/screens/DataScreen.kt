package com.example.finalproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.R
import com.example.finalproject.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataScreen(
    onNavigateTo: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "data",
                onNavigate = onNavigateTo
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Header with profile and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_image),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = "2024-12-01",
                    color = Color.Gray
                )
                IconButton(
                    onClick = { /* Handle message */ },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF00BFA5), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "Calendar",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Data Report",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tab selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TabButton(
                    text = "Energy Analysis",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                Spacer(modifier = Modifier.width(8.dp))
                TabButton(
                    text = "Mood Analysis",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Graph Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Area chart would go here
                    if (selectedTab == 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            repeat(5) { index ->
                                Icon(
                                    painter = painterResource(
                                        id = when (index) {
                                            0 -> R.drawable.ic_mood_very_good
                                            1 -> R.drawable.ic_mood_good
                                            2 -> R.drawable.ic_mood_neutral
                                            3 -> R.drawable.ic_mood_bad
                                            else -> R.drawable.ic_mood_very_bad
                                        }
                                    ),
                                    contentDescription = "Mood",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                // Nutrition stats
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        NutritionStat("Carbohydrates", "2456g", 0.48f, Color(0xFFE57373))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            NutritionStat("Fat", "985g", 0.20f, Color(0xFFFFB74D))
                            NutritionStat("Protein", "1667g", 0.32f, Color(0xFF9575CD))
                        }
                    }
                }
            }

            // History section
            Text(
                text = "History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Column {
                HistoryItem(
                    date = "Today",
                    carbs = "134g",
                    fat = "84g",
                    protein = "253g",
                    mood = if (selectedTab == 1) R.drawable.ic_mood_good else null
                )
                HistoryItem(
                    date = "2022-11-15",
                    carbs = "365g",
                    fat = "156g",
                    protein = "354g",
                    mood = if (selectedTab == 1) R.drawable.ic_mood_good else null
                )
                HistoryItem(
                    date = "2022-11-01",
                    carbs = "253g",
                    fat = "253g",
                    protein = "253g",
                    mood = if (selectedTab == 1) R.drawable.ic_mood_good else null
                )
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF1B3434) else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.height(36.dp)
    ) {
        Text(text = text)
    }
}

@Composable
private fun NutritionStat(
    label: String,
    value: String,
    progress: Float,
    color: Color
) {
    Column {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.Gray
        )
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .width(100.dp)
                .padding(vertical = 4.dp),
            color = color
        )
        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun HistoryItem(
    date: String,
    carbs: String,
    fat: String,
    protein: String,
    mood: Int? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date,
            color = Color.Gray,
            modifier = Modifier.width(80.dp)
        )

        mood?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = "Mood",
                tint = Color(0xFFFFB74D),
                modifier = Modifier.size(24.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = carbs, color = Color(0xFFE57373))
            Text(text = fat, color = Color(0xFFFFB74D))
            Text(text = protein, color = Color(0xFF9575CD))
        }
    }
}