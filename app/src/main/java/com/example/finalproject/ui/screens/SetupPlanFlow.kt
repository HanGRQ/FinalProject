package com.example.finalproject.ui.screens

import androidx.compose.runtime.*
import com.example.finalproject.viewmodel.UserInfoViewModel

@Composable
fun SetupPlanFlow(
    viewModel: UserInfoViewModel,
    onFinish: () -> Unit
) {
    var currentPage by remember { mutableStateOf(1) }

    when (currentPage) {
        1 -> UserNameScreen(onNext = { currentPage = 2 }, viewModel = viewModel)
        2 -> UserGoalScreen(onNext = { currentPage = 3 }, viewModel = viewModel)
        3 -> UserGenderScreen(onNext = { currentPage = 4 }, viewModel = viewModel)
        4 -> UserHeightScreen(onNext = { currentPage = 5 }, viewModel = viewModel)
        5 -> UserWeightScreen(onNext = { currentPage = 6 }, viewModel = viewModel)
        6 -> TargetWeightScreen(
            onNext = {
                // 在这里保存所有用户数据
                viewModel.saveUserInfo()
                onFinish()
            },
            viewModel = viewModel
        )
    }
}