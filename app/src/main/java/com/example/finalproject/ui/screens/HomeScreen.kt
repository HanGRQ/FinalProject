package com.example.finalproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.finalproject.R
import com.example.finalproject.ui.components.BottomNavigationBar
import com.example.finalproject.viewmodel.FoodDetailsViewModel
import com.example.finalproject.viewmodel.UserInfoViewModel
import com.example.finalproject.viewmodel.WeightViewModel
import com.example.finalproject.viewmodel.WeightEntry
import kotlin.math.abs

@Composable
fun HomeScreen(
    userId: String,
    userInfoViewModel: UserInfoViewModel,
    viewModel: FoodDetailsViewModel,
    weightViewModel: WeightViewModel,
    onNavigateToFoodDetails: () -> Unit,
    onNavigateToMoodDetails: () -> Unit,
    onNavigateToWeight: () -> Unit,
    onNavigateToData: () -> Unit,
    onNavigateToPersonal: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userEmail by userInfoViewModel.userEmail.collectAsState()
    val profileImageUrl by userInfoViewModel.profileImageUrl.collectAsState()
    val userPlan by userInfoViewModel.userPlan.collectAsState()

    // 获取体重数据
    val weightState by weightViewModel.weightState.collectAsState()

    // 计算体重变化消息
    val weightChangeMessage = remember(weightState.weightEntries, userPlan) {
        calculateWeightChangeMessage(weightState.weightEntries, userPlan)
    }

    LaunchedEffect(userId) {
        viewModel.loadAllDietFoods(userId) // 加载饮食数据
        weightViewModel.fetchWeightEntries(userId) // 加载体重数据
    }

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
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = if (profileImageUrl != null && profileImageUrl!!.isNotEmpty()) {
                            rememberImagePainter(
                                data = profileImageUrl,
                                builder = { crossfade(true) }
                            )
                        } else {
                            painterResource(id = R.drawable.profile_image)
                        },
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { onNavigateToPersonal() },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = userEmail.ifEmpty { "User Email" },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = weightChangeMessage,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // 其余部分保持不变
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF00796B)
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Total Energy",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Total Energy: ${uiState.totalNutrition.energy.toInt()} kcal",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalDrink,
                                contentDescription = "Total Sugars",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Total Sugars: ${uiState.totalNutrition.totalSugars.toInt()} g",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(${(uiState.totalNutrition.totalSugars / 100).toInt()}%)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            item {
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
            }

            if (uiState.foodItems.isNotEmpty()) {
                items(uiState.foodItems) { food ->
                    FoodItem(food = food)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No food items added yet.",
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToMoodDetails)
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Emotion",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_forward),
                        contentDescription = "Forward",
                        tint = Color.Gray
                    )
                }
            }

            item {
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
    food: com.example.finalproject.utils.FoodResponse,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(
                id = when (food.product_name) {
                    "Hot Dog" -> R.drawable.ic_hotdog
                    "Donut" -> R.drawable.ic_donut
                    "Cake" -> R.drawable.ic_cake
                    else -> R.drawable.ic_food_default
                }
            ),
            contentDescription = food.product_name,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = food.product_name.ifEmpty { "Unknown Food" },
                    fontSize = 14.sp
                )
                Text(
                    text = "100g",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${food.energy_kcal.toInt()}kcal",
                    fontSize = 12.sp,
                    color = Color(0xFF4CAF50)
                )
                Text(
                    text = "${food.carbohydrates.toInt()}g",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "${food.fat.toInt()}g",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "${food.proteins.toInt()}g",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// 计算并生成体重变化消息
private fun calculateWeightChangeMessage(weightEntries: List<WeightEntry>, userPlan: String): String {
    // 如果体重记录少于2条，则无法比较变化
    if (weightEntries.size < 2) {
        return when (userPlan) {
            "Weight Gain" -> "Keep working on gaining weight!"
            "Weight Loss" -> "Keep working on losing weight!"
            "Healthy" -> "Keep Healthy!"
            else -> "Monitor your weight regularly!"
        }
    }

    // 获取最新的两条记录
    val sortedEntries = weightEntries.sortedByDescending { it.timestamp }
    val latestWeight = sortedEntries[0].weight
    val previousWeight = sortedEntries[1].weight

    // 计算差值（保留一位小数）
    val weightDiff = latestWeight - previousWeight
    val absWeightDiff = String.format("%.1f", abs(weightDiff))

    return when (userPlan) {
        "Weight Gain" -> {
            if (weightDiff > 0) {
                "You gained ${absWeightDiff}kg recently, keep it up!"
            } else {
                "Keep working on gaining weight!"
            }
        }
        "Weight Loss" -> {
            if (weightDiff < 0) {
                "You lost ${absWeightDiff}kg recently, great job!"
            } else {
                "Keep working on losing weight!"
            }
        }
        "Healthy" -> "Keep Healthy!"
        else -> "Monitor your weight regularly!"
    }
}