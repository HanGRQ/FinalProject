package com.example.finalproject.ui.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.finalproject.ui.screens.*
import com.example.finalproject.viewmodel.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavGraph(navController: NavHostController) {
    val userInfoViewModel: UserInfoViewModel = hiltViewModel()
    val scanViewModel: ScanViewModel = hiltViewModel()
    val sharedViewModel: FoodDetailsViewModel = hiltViewModel()

    val userId by userInfoViewModel.userId.collectAsState()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onFinish = {
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        composable("onboarding") {
            OnboardingScreen(onFinish = {
                navController.navigate("login") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = { userId ->
                    userInfoViewModel.setUserId(userId)
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(onRegisterSuccess = {
                navController.popBackStack()
            })
        }

        /** ✅ HomeScreen 连接 FoodDetailsScreen 和 MoodDetailsScreen */
        composable("home") {
            if (userId == null) return@composable

            HomeScreen(
                userId = userId!!,
                userInfoViewModel = hiltViewModel(), // ✅ 传递 UserInfoViewModel，获取邮箱
                viewModel = hiltViewModel(),
                onNavigateToFoodDetails = { navController.navigate("food_details/$userId") },
                onNavigateToMoodDetails = { navController.navigate("mood_details/$userId") },
                onNavigateToWeight = { navController.navigate("weight") },
                onNavigateToData = { navController.navigate("data") },
                onNavigateToPersonal = { navController.navigate("personal/$userId") } // ✅ 修改：确保传递 userId
            )
        }

        /** ✅ DataScreen */
        composable("data") {
            if (userId == null) return@composable
            val viewModel: DataViewModel = hiltViewModel()
            val userInfoViewModel: UserInfoViewModel = hiltViewModel()

            DataScreen(
                userId = userId!!,
                viewModel = viewModel,
                userInfoViewModel = userInfoViewModel,
                onNavigateToWeight = { navController.navigate("weight") { launchSingleTop = true } },
                onNavigateToHome = { navController.navigate("home") { launchSingleTop = true } },
                onNavigateToPersonal = { navController.navigate("personal/$userId") { launchSingleTop = true } }
            )
        }

        /** ✅ WeightScreen */
        composable("weight") {
            if (userId == null) return@composable
            val viewModel: WeightViewModel = hiltViewModel()
            val userInfoViewModel: UserInfoViewModel = hiltViewModel()

            WeightScreen(
                viewModel = viewModel,
                userId = userId!!,
                userInfoViewModel = userInfoViewModel,
                onNavigateToHome = { navController.navigate("home") { launchSingleTop = true } },
                onNavigateToData = { navController.navigate("data") { launchSingleTop = true } },
                onNavigateToPersonal = { navController.navigate("personal/$userId") { launchSingleTop = true } }
            )
        }

        /** ✅ PersonalScreen */
        composable("personal/{userId}", arguments = listOf(navArgument("userId") { type = NavType.StringType })) {
            val userId = it.arguments?.getString("userId") ?: return@composable
            val viewModel: UserInfoViewModel = hiltViewModel()

            PersonalScreen(
                userId = userId,
                viewModel = viewModel,
                onNavigateToHome = { navController.navigate("home") { launchSingleTop = true } },
                onNavigateToData = { navController.navigate("data") { launchSingleTop = true } },
                onNavigateToWeight = { navController.navigate("weight") { launchSingleTop = true } },
                onNavigateToFoodDetails = { navController.navigate("food_details/$userId") { launchSingleTop = true } },
                onNavigateToPersonalDetails = { navController.navigate("personal_details/$userId") { launchSingleTop = true } },
                onNavigateToSettings = { navController.navigate("settings/$userId") { launchSingleTop = true } },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    userInfoViewModel.setUserId(null)
                    navController.navigate("login") {
                        popUpTo("personal/$userId") { inclusive = true }
                    }
                }
            )
        }

        /** ✅ FoodDetailsScreen 连接 ScanScreen */
        composable(
            route = "food_details/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            val viewModel: FoodDetailsViewModel = hiltViewModel()

            FoodDetailsScreen(
                userId = userId,
                viewModel = viewModel,
                onScanButtonClick = { navController.navigate("scan/$userId") }, // ✅ 传递 userId
                onNavigateBack = { navController.popBackStack() }
            )
        }

        /** ScanScreen 连接 FoodDetailScreen（仅在成功扫描时） */
        composable(
            route = "scan/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            val scanViewModel: ScanViewModel = hiltViewModel()

            ScanScreen(
                userId = userId,
                navController = navController,
                viewModel = scanViewModel
            ) { barcode ->
                if (barcode.isNotEmpty()) {
                    navController.navigate("food_detail/$userId/$barcode") {
                        popUpTo("scan/$userId") { inclusive = true } // ✅ 让 ScanScreen 退出
                    }
                }
            }
        }


        composable(
            "food_detail/{userId}/{barcode}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("barcode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            val barcode = backStackEntry.arguments?.getString("barcode") ?: return@composable
            Log.d("FoodDetailScreen", "Received barcode: $barcode")  // 🔍 确保 barcode 传入
            val viewModel: FoodDetailsViewModel = hiltViewModel()

            FoodDetailScreen(
                userId = userId,
                barcode = barcode,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }


        /** ✅ MoodDetailsScreen */
        composable(
            "mood_details/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            val viewModel: EmotionViewModel = hiltViewModel()

            MoodDetailsScreen(
                viewModel = viewModel,
                userId = userId,
                onNavigateTo = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }

    }
}
