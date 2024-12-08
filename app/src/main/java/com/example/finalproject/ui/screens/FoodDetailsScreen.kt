package com.example.finalproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finalproject.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "饮食详情", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Add food logic */ }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = "Add"
                        )
                    }
                }
            )
        },
        content = { padding ->
            // 添加垂直滚动
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF6F6F6))
                    .verticalScroll(rememberScrollState()) // 使页面可滚动
                    .padding(16.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = "",
                    onValueChange = { /* Handle search input */ },
                    placeholder = { Text("输入关键字搜索") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "Search",
                            modifier = Modifier.size(24.dp) // Adjusted size
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nutritional Chart
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
                        Text(text = "582 kcal", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "12月1日  -  3餐物  -  49 kcal 剩余", fontSize = 14.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(16.dp))
                        Image(
                            painter = painterResource(R.drawable.ic_chart),
                            contentDescription = "Nutritional Chart",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nutritional Breakdown
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NutritionalInfo("总热量", "582", "92%")
                    NutritionalInfo("碳水化合物", "58g", "17%")
                    NutritionalInfo("脂肪", "26g", "28%")
                    NutritionalInfo("蛋白质", "28g", "45%")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Food Data Section
                Text(text = "饮食数据", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                FoodItem("热狗", "1份", 152, 0, 8, 13)
                FoodItem("甜甜圈", "1份", 170, 13, 9, 8)
                FoodItem("蛋糕", "1份", 253, 39, 88, 253)

                Spacer(modifier = Modifier.height(16.dp))

                // Barcode Scan Button
                Button(
                    onClick = { navController.navigate("scan") },
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
                    Text(text = "扫描添加饮食", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    )
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
