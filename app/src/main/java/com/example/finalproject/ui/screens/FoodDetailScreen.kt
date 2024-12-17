package com.example.finalproject.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalproject.utils.DatabaseHelper

@Composable
fun FoodDetailScreen(
    navController: NavController,
    barcode: String,
    databaseHelper: DatabaseHelper
) {
    val foodDetails = remember {
        databaseHelper.getFoodDetailsByBarcode(barcode)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        foodDetails?.let { details ->
            Text(text = "商品名称: ${details.name}", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "规格: ${details.spec}")
            Text(text = "单位: ${details.unit}")
            Text(text = "价格: ¥${details.price}")
            Text(text = "品牌: ${details.brand}")
            Text(text = "供应商: ${details.supplier}")
            Text(text = "产地: ${details.madeIn}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigateUp() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("返回")
            }
        } ?: Text("未找到商品信息")
    }
}