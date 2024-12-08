package com.example.finalproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(
    onNavigateTo: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("体重管理") }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "weight",
                onNavigate = onNavigateTo
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("体重管理页面内容")
        }
    }
}