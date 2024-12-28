package com.example.finalproject.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finalproject.R
import com.example.finalproject.viewmodel.FoodDetailsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailsScreen(
    viewModel: FoodDetailsViewModel,
    onScanButtonClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("FoodDetailsScreen", "Screen initialized with ViewModel: ${viewModel.hashCode()}")
    }

    LaunchedEffect(uiState) {
        Log.d("FoodDetailsScreen", "State updated: $uiState")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Diet Details", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6F6F6))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 能量汇总卡片
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${uiState.totalNutrition.energy} kcal",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${uiState.foodItems.size} Meals",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 营养信息
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                NutritionalInfo(
                    "Total Energy",
                    "${uiState.totalNutrition.energy}",
                    "${(uiState.totalNutrition.energy / 2000.0 * 100).toInt()}%"
                )
                NutritionalInfo(
                    "Carbohydrates",
                    "${uiState.totalNutrition.carbs.toInt()}g",
                    "${(uiState.totalNutrition.carbs / 300.0 * 100).toInt()}%"
                )
                NutritionalInfo(
                    "Fat",
                    "${uiState.totalNutrition.fat.toInt()}g",
                    "${(uiState.totalNutrition.fat / 65.0 * 100).toInt()}%"
                )
                NutritionalInfo(
                    "Protein",
                    "${uiState.totalNutrition.protein.toInt()}g",
                    "${(uiState.totalNutrition.protein / 50.0 * 100).toInt()}%"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 食物列表
            Text(text = "Diet Data", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            uiState.foodItems.forEach { food ->
                FoodItem(
                    name = food.name,
                    portion = food.portion,
                    calories = food.calories,
                    carbs = food.carbs.toInt(),
                    fat = food.fat.toInt(),
                    protein = food.protein.toInt()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 扫描按钮
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
fun FoodItem(name: String, portion: String, calories: Int, carbs: Int, fat: Int, protein: Int) {
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
                Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = portion, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(text = "${calories}kcal", fontSize = 14.sp, color = Color(0xFF004D40))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${carbs}g", fontSize = 14.sp, color = Color(0xFFFFA726))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${fat}g", fontSize = 14.sp, color = Color(0xFFF06292))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${protein}g", fontSize = 14.sp, color = Color(0xFF9575CD))
                }
            }

            Row {
                IconButton(onClick = { /* Edit item logic */ }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit"
                    )
                }
                IconButton(onClick = { /* Delete item logic */ }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }
}