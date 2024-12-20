package com.example.finalproject.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavScreen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    object Weight : BottomNavScreen(
        route = "weight",
        title = "Weight",
        icon = Icons.Default.MonitorWeight
    )

    object Data : BottomNavScreen(
        route = "data",
        title = "Data",
        icon = Icons.Default.Assessment
    )

    object Personal : BottomNavScreen(
        route = "personal",
        title = "Profile",
        icon = Icons.Default.Person
    )
}