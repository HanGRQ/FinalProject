package com.example.finalproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.R
import com.example.finalproject.ui.components.NutritionPieChart
import com.example.finalproject.ui.components.NutritionBarChart
import com.example.finalproject.viewmodel.FoodDetailsViewModel
import com.example.finalproject.utils.FoodResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailsScreen(
    userId: String,
    viewModel: FoodDetailsViewModel,
    onScanButtonClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadAllDietFoods(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Diet Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6F6F6)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // **✅ 替换总能量卡片，改用 `NutritionPieChart`**
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nutrition Breakdown",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        NutritionPieChart(
                            totalEnergy = uiState.totalNutrition.energy.toFloat(),
                            carbs = uiState.totalNutrition.carbs.toFloat(),
                            fat = uiState.totalNutrition.fat.toFloat(),
                            protein = uiState.totalNutrition.protein.toFloat(),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            item {
                // **✅ 营养信息**
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NutritionalInfo("Total Energy", "${uiState.totalNutrition.energy.toInt()} kcal", "${(uiState.totalNutrition.energy / 2000.0 * 100).toInt()}%")
                    NutritionalInfo("Carbohydrates", "${uiState.totalNutrition.carbs.toInt()}g", "${(uiState.totalNutrition.carbs / 300.0 * 100).toInt()}%")
                    NutritionalInfo("Fat", "${uiState.totalNutrition.fat.toInt()}g", "${(uiState.totalNutrition.fat / 65.0 * 100).toInt()}%")
                    NutritionalInfo("Protein", "${uiState.totalNutrition.protein.toInt()}g", "${(uiState.totalNutrition.protein / 50.0 * 100).toInt()}%")
                }
            }

            item {
                // **✅ 营养趋势图**
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Nutrition Trends",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        NutritionBarChart(
                            foodItems = uiState.foodItems,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )
                    }
                }
            }

            if (uiState.foodItems.isNotEmpty()) {
                item {
                    Text(
                        text = "Diet Data",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(uiState.foodItems) { food ->
                    FoodItem(
                        food = food,
                        onDelete = { viewModel.deleteFoodFromFirestore(userId, food) }
                    )
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
                // **✅ 扫描按钮**
                Button(
                    onClick = onScanButtonClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.QrCodeScanner,
                        contentDescription = "Barcode Scanner",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Scan to Add Food", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}




@Composable
fun NutritionalInfo(label: String, value: String, percentage: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = percentage, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun FoodItem(
    food: FoodResponse,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = food.product_name.ifEmpty { "Unknown Food" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "100g",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = "${food.energy_kcal.toInt()}kcal",
                        fontSize = 14.sp,
                        color = Color(0xFF004D40)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${food.carbohydrates.toInt()}g",
                        fontSize = 14.sp,
                        color = Color(0xFFFFA726)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${food.fat.toInt()}g",
                        fontSize = 14.sp,
                        color = Color(0xFFF06292)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${food.proteins.toInt()}g",
                        fontSize = 14.sp,
                        color = Color(0xFF9575CD)
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
    }
}