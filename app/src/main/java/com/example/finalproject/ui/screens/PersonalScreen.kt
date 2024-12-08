package com.example.finalproject.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.finalproject.ui.components.BottomNavigationBar

@Composable
fun PersonalScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Text(
            text = "这是个人页面！查看并编辑您的个人资料。",
            modifier = Modifier.padding(innerPadding)
        )
    }
}
