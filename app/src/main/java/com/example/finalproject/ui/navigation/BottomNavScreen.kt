// 文件路径：com/example/finalproject/ui/navigation/BottomNavScreen.kt

package com.example.finalproject.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavScreen("home", "Home", Icons.Filled.Home)
    object Weight : BottomNavScreen("weight", "Weight", Icons.Filled.FitnessCenter)
    object Data : BottomNavScreen("data", "Data", Icons.Filled.PieChart)
    object Personal : BottomNavScreen("personal", "Personal", Icons.Filled.Person)
}
