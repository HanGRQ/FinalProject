package com.example.finalproject.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.utils.DatabaseHelper
import com.example.finalproject.viewmodel.FoodDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailScreen(
    navController: NavController,
    barcode: String,
    databaseHelper: DatabaseHelper,
    viewModel: FoodDetailsViewModel
) {
    val foodDetails = remember {
        databaseHelper.getFoodDetailsByBarcode(barcode)
    }

    var isAdded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            foodDetails?.let { details ->
                                Log.d("FoodDetailScreen", "Adding food: ${details.name}")
                                viewModel.addFood(details)
                                isAdded = true
                                navController.popBackStack(
                                    route = "food_details",
                                    inclusive = false
                                )
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "Add"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            foodDetails?.let { details ->
                // Header section with serving info
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = details.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1 serving 62g",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nutrition overview card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // First row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "${details.totalEnergyKcal.toInt()}kcal",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "Energy", color = Color.Gray)
                                LinearProgressIndicator(
                                    progress = (details.totalEnergyKcal / 2000).toFloat(), // 假设每日推荐摄入量为2000kcal
                                    modifier = Modifier.width(100.dp),
                                    color = Color(0xFF4CAF50)
                                )
                            }
                            Column {
                                Text(
                                    text = "${details.carbohydrates}g",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "Carbs", color = Color.Gray)
                                LinearProgressIndicator(
                                    progress = (details.carbohydrates / 300).toFloat(), // 假设每日推荐摄入量为300g
                                    modifier = Modifier.width(100.dp),
                                    color = Color(0xFF2196F3)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Second row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "${details.fat}g",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "Fat", color = Color.Gray)
                                LinearProgressIndicator(
                                    progress = (details.fat / 65).toFloat(), // 假设每日推荐摄入量为65g
                                    modifier = Modifier.width(100.dp),
                                    color = Color(0xFFFF9800)
                                )
                            }
                            Column {
                                Text(
                                    text = "${details.protein}g",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "Protein", color = Color.Gray)
                                LinearProgressIndicator(
                                    progress = (details.protein / 50).toFloat(), // 假设每日推荐摄入量为50g
                                    modifier = Modifier.width(100.dp),
                                    color = Color(0xFFF44336)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Detailed nutrition facts
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NutritionRow("Energy (kJ)", "${details.totalEnergyKJ} kJ")
                    NutritionRow("Energy (kcal)", "${details.totalEnergyKcal} kcal")
                    NutritionRow("Fat", "${details.fat}g")
                    NutritionRow("Carbohydrates", "${details.carbohydrates}g")
                    NutritionRow("Protein", "${details.protein}g")
                    NutritionRow("Sodium", "${details.sodium}mg")
                }
            } ?: Text("Product not found")
        }
    }
}

@Composable
private fun NutritionRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray)
        Text(text = value)
    }
}