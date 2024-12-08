// 文件路径：com/example/finalproject/ui/screens/SetupPlanFlow.kt

package com.example.finalproject.ui.screens

import androidx.compose.runtime.*
import com.example.finalproject.viewmodel.UserInfoViewModel

@Composable
fun SetupPlanFlow(viewModel: UserInfoViewModel, onFinish: () -> Unit) {
    val currentPage = remember { mutableStateOf(1) }

    when (currentPage.value) {
        1 -> UserNameScreen(onNext = { currentPage.value = 2 }, viewModel = viewModel)
        2 -> UserGoalScreen(onNext = { currentPage.value = 3 }, viewModel = viewModel)
        3 -> UserGenderScreen(onNext = { currentPage.value = 4 }, viewModel = viewModel)
        4 -> UserHeightScreen(onNext = { currentPage.value = 5 }, viewModel = viewModel)
        5 -> UserWeightScreen(onNext = { currentPage.value = 6 }, viewModel = viewModel)
        6 -> TargetWeightScreen(onNext = onFinish, viewModel = viewModel)
    }
}
