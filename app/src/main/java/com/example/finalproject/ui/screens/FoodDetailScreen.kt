package com.example.finalproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finalproject.viewmodel.FoodDetailsViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.res.painterResource
import com.example.finalproject.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailScreen(
    userId: String,
    barcode: String,
    viewModel: FoodDetailsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(barcode) {
        viewModel.fetchFoodDetailsFromFirestore(userId, barcode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.addCurrentFoodToMainList(userId) // ✅ 传递 userId 添加食品
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = "Add to Diet"
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
            val details = uiState.foodItems.firstOrNull()

            if (details != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = details.product_name.ifEmpty { "Unknown Food" },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Barcode: ${details.barcode}",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Per 100g",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Energy values
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            NutrientInfo(
                                label = "Energy (kcal)",
                                value = "${details.energy_kcal.toInt()} kcal"
                            )
                            NutrientInfo(
                                label = "Energy (kJ)",
                                value = "${details.energy_kj.toInt()} kJ"
                            )
                        }

                        // Macronutrients
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            NutrientInfo(
                                label = "Carbohydrates",
                                value = "${details.carbohydrates.toInt()}g"
                            )
                            NutrientInfo(
                                label = "of which Sugars",
                                value = "${details.sugars.toInt()}g"
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            NutrientInfo(
                                label = "Fat",
                                value = "${details.fat.toInt()}g"
                            )
                            NutrientInfo(
                                label = "Protein",
                                value = "${details.proteins.toInt()}g"
                            )
                        }
                    }
                }
            } else {
                Text("No food data found", fontSize = 20.sp, color = Color.Gray)
            }
        }
    }
}


@Composable
private fun NutrientInfo(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}