package com.example.finalproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.MonitorWeight

@Composable
fun BottomNavigationBar(
    currentRoute: String = "home",
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF004D40)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavigationItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "首页") },
                label = { Text("首页") },
                selected = currentRoute == "home",
                onClick = { onNavigate("home") },
                selectedContentColor = Color.White,
                unselectedContentColor = Color(0xFF80CBC4)
            )

            BottomNavigationItem(
                icon = { Icon(Icons.Default.MonitorWeight, contentDescription = "体重") },
                label = { Text("体重") },
                selected = currentRoute == "weight",
                onClick = { onNavigate("weight") },
                selectedContentColor = Color.White,
                unselectedContentColor = Color(0xFF80CBC4)
            )

            BottomNavigationItem(
                icon = { Icon(Icons.Default.Assessment, contentDescription = "数据") },
                label = { Text("数据") },
                selected = currentRoute == "data",
                onClick = { onNavigate("data") },
                selectedContentColor = Color.White,
                unselectedContentColor = Color(0xFF80CBC4)
            )

            BottomNavigationItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "我的") },
                label = { Text("我的") },
                selected = currentRoute == "personal",
                onClick = { onNavigate("personal") },
                selectedContentColor = Color.White,
                unselectedContentColor = Color(0xFF80CBC4)
            )
        }
    }
}