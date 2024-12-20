package com.example.finalproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun HomeScreen(
    onNavigateToFoodDetails: () -> Unit,
    onNavigateToMoodDetails: () -> Unit,
    onNavigateToWeight: () -> Unit,
    onNavigateToData: () -> Unit,
    onNavigateToPersonal: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "home",
                onNavigate = { route ->
                    when (route) {
                        "weight" -> onNavigateToWeight()
                        "data" -> onNavigateToData()
                        "personal" -> onNavigateToPersonal()
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_image),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Hello, Hello World",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "You gained 2kg yesterday, keep it up!",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* Handle calendar click */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "Calendar",
                        tint = Color(0xFF00BFA5)
                    )
                }
            }

            // Calorie Progress Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "1739cal\n2925 kcal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "134",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Total Carbohydrates",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        LinearProgressIndicator(
                            progress = 0.28f,
                            modifier = Modifier.width(100.dp),
                            color = Color(0xFFE57373)
                        )
                    }
                    Column {
                        Text(
                            text = "94",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Protein",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        LinearProgressIndicator(
                            progress = 0.81f,
                            modifier = Modifier.width(100.dp),
                            color = Color(0xFFFF7043)
                        )
                    }
                }
            }

            // Food Data Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToFoodDetails)
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Diet Data",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_forward),
                        contentDescription = "Forward",
                        tint = Color.Gray
                    )
                }

                // Food Items List
                FoodItem(
                    name = "Hot Dog",
                    servings = "1 serving",
                    calories = "152kcal",
                    nutrients = listOf("0g", "5g", "13g")
                )
                FoodItem(
                    name = "Donut",
                    servings = "1 serving",
                    calories = "170kcal",
                    nutrients = listOf("13g", "5g", "8g")
                )
                FoodItem(
                    name = "Cake",
                    servings = "1 serving",
                    calories = "253kcal",
                    nutrients = listOf("253g", "39g", "88g")
                )
            }

            // Mood Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToMoodDetails)
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Mood",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_forward),
                        contentDescription = "Forward",
                        tint = Color.Gray
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_mood_good),
                        contentDescription = "Good mood",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Good",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FoodItem(
    name: String,
    servings: String,
    calories: String,
    nutrients: List<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(
                id = when (name) {
                    "Hot Dog" -> R.drawable.ic_hotdog
                    "Donut" -> R.drawable.ic_donut
                    else -> R.drawable.ic_cake
                }
            ),
            contentDescription = name,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = name, fontSize = 14.sp)
                Text(text = servings, fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = calories, fontSize = 12.sp, color = Color(0xFF4CAF50))
                nutrients.forEach { nutrient ->
                    Text(text = nutrient, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}