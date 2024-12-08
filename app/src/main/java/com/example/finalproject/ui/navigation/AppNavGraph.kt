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

        // 计划设置流程
        composable("setup_plan_flow") {
            val viewModel: UserInfoViewModel = viewModel() // 使用 Compose 的 viewModel() 函数
            SetupPlanFlow(
                viewModel = viewModel,
                onFinish = {
                    navController.navigate("main") {
                        popUpTo("setup_plan_flow") { inclusive = true }
                    }
                }
            )
        }

        // 主页面
        composable("main") {
            MainScreen(onNavigateToHome = {
                navController.navigate("home") {
                    popUpTo("main") { inclusive = true }
                }
            })
        }

        // Bottom navigation screens
        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("food_details") {
            FoodDetailsScreen(navController = navController)
        }

        composable("mood_details") {
            MoodDetailsScreen(navController = navController)
        }


        composable("weight") {
            WeightScreen(navController = navController)
        }

        composable("data") {
            DataScreen(navController = navController)
        }

        composable("personal") {
            PersonalScreen(navController = navController)
        }

        composable("scan") {
            ScanScreen(navController = navController)
        }


    }
}
