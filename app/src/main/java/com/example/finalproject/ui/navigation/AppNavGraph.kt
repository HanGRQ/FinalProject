package com.example.finalproject.ui.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.ui.screens.*
import com.example.finalproject.viewmodel.ScanViewModel
import com.example.finalproject.viewmodel.UserInfoViewModel
import com.example.finalproject.utils.DatabaseHelper
import androidx.compose.ui.platform.LocalContext

private const val TAG = "AppNavGraph"

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val databaseHelper = remember { DatabaseHelper(context) }
    val scanViewModel: ScanViewModel = viewModel()

    // 使用 DisposableEffect 来处理数据库验证
    DisposableEffect(Unit) {
        val isValid = databaseHelper.isDatabaseValid()
        if (!isValid) {
            Log.e(TAG, "数据库初始化失败")
        }

        onDispose {
            databaseHelper.close()
        }
    }

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

        // 设置计划流程
        composable("setup_plan_flow") {
            val viewModel: UserInfoViewModel = viewModel()
            SetupPlanFlow(
                viewModel = viewModel,
                onFinish = {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 主页
        composable("home") {
            HomeScreen(
                onNavigateToFoodDetails = { navController.navigate("food_details") },
                onNavigateToMoodDetails = { navController.navigate("mood_details") },
                onNavigateToWeight = { navController.navigate("weight") },
                onNavigateToData = { navController.navigate("data") },
                onNavigateToPersonal = { navController.navigate("personal") }
            )
        }

        // 体重页面
        composable("weight") {
            WeightScreen(
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // 数据页面
        composable("data") {
            DataScreen(
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // 个人页面
        composable("personal") {
            PersonalScreen(
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // 扫描页面
        composable("scan") {
            Log.d(TAG, "导航到扫描页面")
            ScanScreen(
                navController = navController,
                viewModel = scanViewModel,
                databaseHelper = databaseHelper
            )
        }

        // 食品详情页面（扫描后）
        composable(
            route = "food_details/{barcode}",
            arguments = listOf(navArgument("barcode") {
                type = NavType.StringType
                nullable = false
            })
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode")
            if (barcode != null) {
                Log.d(TAG, "导航到食品详情页面，条形码: $barcode")
                FoodDetailScreen(
                    navController = navController,
                    barcode = barcode,
                    databaseHelper = databaseHelper
                )
            }
        }

        // 食品列表页面
        composable("food_details") {
            FoodDetailsScreen(
                onScanButtonClick = { navController.navigate("scan") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 心情详情页面
        composable("mood_details") {
            MoodDetailsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}