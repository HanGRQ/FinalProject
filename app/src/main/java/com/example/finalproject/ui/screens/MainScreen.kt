// MainScreen.kt
package com.example.finalproject.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect

@Composable
fun MainScreen(onNavigateToHome: () -> Unit) {
    LaunchedEffect(Unit) {
        onNavigateToHome()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "欢迎来到 FoodMind 应用的主页面！")
    }
}
