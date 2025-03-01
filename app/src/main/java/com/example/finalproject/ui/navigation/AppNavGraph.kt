package com.example.finalproject.ui.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.ui.screens.*
import com.example.finalproject.viewmodel.DataViewModel
import com.example.finalproject.viewmodel.EmotionViewModel
import com.example.finalproject.viewmodel.ScanViewModel
import com.example.finalproject.viewmodel.UserInfoViewModel
import com.example.finalproject.viewmodel.FoodDetailsViewModel
import com.example.finalproject.viewmodel.WeightViewModel

private const val TAG = "AppNavGraph"

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavGraph(navController: NavHostController) {
    val scanViewModel: ScanViewModel = viewModel()
    val sharedViewModel: FoodDetailsViewModel = remember { FoodDetailsViewModel() }

    NavHost(navController = navController, startDestination = "splash") {
        // Startup Page
        composable("splash") {
            SplashScreen(onFinish = {
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // Guide page
        composable("onboarding") {
            OnboardingScreen(onFinish = {
                navController.navigate("login") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }

        // Login Page
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("loginsuccess") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        // Login success page
        composable("loginsuccess") {
            LoginSuccessScreen(onNavigateToSetup = {
                navController.navigate("setup_plan_flow") {
                    popUpTo("loginsuccess") { inclusive = true }
                }
            })
        }

        // Setting up the planning process
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

        // main
        composable("home") {
            val viewModel = viewModel<FoodDetailsViewModel>() // 如果使用 androidx.lifecycle:lifecycle-viewmodel-compose

            HomeScreen(
                viewModel = viewModel, // 添加 viewModel 参数
                onNavigateToFoodDetails = { navController.navigate("food_details") },
                onNavigateToMoodDetails = { navController.navigate("mood_details") },
                onNavigateToWeight = { navController.navigate("weight") },
                onNavigateToData = { navController.navigate("data") },
                onNavigateToPersonal = { navController.navigate("personal") }
            )
        }

        composable("weight") {
            val viewModel: WeightViewModel = viewModel() // 或 hiltViewModel()，取决于您使用的依赖注入方式
            WeightScreen(
                viewModel = viewModel,
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("data") {
            val viewModel: DataViewModel = viewModel() // 或 hiltViewModel()，取决于您使用的依赖注入方式
            DataScreen(
                viewModel = viewModel,
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

        composable("personal_details") {
            PersonalDetailsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("scan") {
            Log.d(TAG, "scan page")
            ScanScreen(
                navController = navController,
                viewModel = scanViewModel,
                onScanComplete = { barcode ->
                    sharedViewModel.fetchFoodDetailsFromFirestore(barcode)
                    navController.navigate("food_details/$barcode")
                }
            )
        }

        composable(
            route = "food_details/{barcode}",
            arguments = listOf(navArgument("barcode") {
                type = NavType.StringType
                nullable = false
            })
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode")
            if (barcode != null) {
                Log.d(TAG, "Navigate to food details page, barcode: $barcode")
                FoodDetailScreen(
                    navController = navController,
                    barcode = barcode,
                    viewModel = sharedViewModel
                )
            }
        }

        composable("food_details") {
            Log.d(TAG, "Navigate to food list page")
            FoodDetailsScreen(
                viewModel = sharedViewModel,
                onScanButtonClick = { navController.navigate("scan") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("mood_details") {
            val viewModel: EmotionViewModel = viewModel() // 或 hiltViewModel()，取决于您的依赖注入方式
            MoodDetailsScreen(
                viewModel = viewModel,
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }


    }
}