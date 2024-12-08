package com.example.finalproject.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.finalproject.ui.components.BottomNavigationBar

@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        // 将 Text 放在 Scaffold 的 content 参数中
        Text(
            text = "欢迎来到首页！",
            modifier = Modifier.padding(innerPadding)
        )
    }
}
