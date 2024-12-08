package com.example.finalproject.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.finalproject.ui.components.BottomNavigationBar

@Composable
fun WeightScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Text(
            text = "This is Weight Management Page.",
            modifier = Modifier.padding(innerPadding)
        )
    }
}
