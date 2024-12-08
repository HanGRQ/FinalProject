package com.example.finalproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.finalproject.ui.screens.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.viewmodel.UserInfoViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        // 启动页面
        composable("splash") {
            SplashScreen(onFinish = {
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // 引导页
        composable("onboarding") {
            OnboardingScreen(onFinish = {
                navController.navigate("login") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }

        // 登录页
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("loginsuccess") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        // 登录成功页
        composable("loginsuccess") {
            LoginSuccessScreen(onNavigateToSetup = {
                navController.navigate("setup_plan_flow") {
                    popUpTo("loginsuccess") { inclusive = true }
                }
            })
        }

        // AppNavGraph.kt 中的相关部分
        composable("setup_plan_flow") {
            val viewModel: UserInfoViewModel = viewModel()
            SetupPlanFlow(
                viewModel = viewModel,
                onFinish = {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }  // 清除整个导航栈
                        launchSingleTop = true  // 确保只有一个实例
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToFoodDetails = { navController.navigate("food_details") },
                onNavigateToMoodDetails = { navController.navigate("mood_details") },
                onNavigateToWeight = { navController.navigate("weight") },
                onNavigateToData = { navController.navigate("data") },
                onNavigateToPersonal = { navController.navigate("personal") }
            )
        }

        // 其他底部导航页面
        composable("weight") {
            WeightScreen(
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("data") {
            DataScreen(
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("personal") {
            PersonalScreen(
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("food_details") {
            FoodDetailsScreen(
                onScanButtonClick = { navController.navigate("scan") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("mood_details") {
            MoodDetailsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("scan") {
            ScanScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}