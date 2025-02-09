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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailScreen(
    navController: NavController,
    barcode: String,
    viewModel: FoodDetailsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(barcode) {
        viewModel.fetchFoodDetailsFromFirestore(barcode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
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
                        text = details.product_name ?: "Unknown Food",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1 serving 100g",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("${details.energy_kcal.toInt()} kcal", fontWeight = FontWeight.Bold)
                                Text("Energy", color = Color.Gray)
                            }
                            Column {
                                Text("${details.carbohydrates}g", fontWeight = FontWeight.Bold)
                                Text("Carbs", color = Color.Gray)
                            }
                        }
                    }
                }
            } else {
                Text("No food data found", fontSize = 20.sp, color = Color.Gray)
            }
        }
    }
}