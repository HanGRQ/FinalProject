// 文件路径：com/example/finalproject/ui/navigation/BottomNavScreen.kt

package com.example.finalproject.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavScreen("home", "首页", Icons.Filled.Home)
    object Weight : BottomNavScreen("weight", "体重", Icons.Filled.FitnessCenter)
    object Data : BottomNavScreen("data", "数据", Icons.Filled.PieChart)
    object Personal : BottomNavScreen("personal", "个人", Icons.Filled.Person)
}
