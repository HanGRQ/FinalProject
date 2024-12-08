package com.example.finalproject.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.finalproject.ui.components.BottomNavigationBar

@Composable
fun DataScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Text(
            text = "This is the data page! View your diet and health data here.",
            modifier = Modifier.padding(innerPadding)
        )
    }
}
