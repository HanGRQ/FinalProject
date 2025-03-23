package com.example.finalproject.repository

import android.util.Log
import com.example.finalproject.api.OpenFoodFactsService
import com.example.finalproject.utils.FoodResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepository @Inject constructor(
    private val service: OpenFoodFactsService,
    private val firestore: FirebaseFirestore
) {
    suspend fun getFoodDetails(barcode: String): Result<FoodResponse> {
        return try {
            val response = service.getProduct(barcode)
            if (response.isSuccessful && response.body() != null) {
                val product = response.body()!!

                Log.d("FoodRepository", "API Response: $product")
                Log.d("FoodRepository", "Nutriments: ${product.product.nutriments}")

                // 获取当前日期
                // 获取当前日期，格式为 dd-MM-yyyy
                val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

                // 修改转换逻辑以匹配 FoodResponse 的字段，并添加日期
                val foodResponse = FoodResponse(
                    barcode = barcode,
                    product_name = product.product.product_name ?: "Unknown",
                    energy_kj = product.product.nutriments?.energyKj ?: 0.0,
                    energy_kcal = product.product.nutriments?.energyKcal ?: 0.0,
                    carbohydrates = product.product.nutriments?.carbohydrates ?: 0.0,
                    sugars = product.product.nutriments?.sugars ?: 0.0,
                    fat = product.product.nutriments?.fat ?: 0.0,
                    proteins = product.product.nutriments?.proteins ?: 0.0,
                    scan_date = currentDate // 添加当前日期
                )

                Log.d("FoodRepository", "Converted FoodResponse: $foodResponse")

                Result.success(foodResponse)
            } else {
                Log.e("FoodRepository", "API Error: ${response.errorBody()?.string()}")
                Result.failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error getting food details", e)
            Result.failure(e)
        }
    }

    suspend fun saveFoodToUserFirestore(userId: String, food: FoodResponse) {
        try {
            val db = FirebaseFirestore.getInstance()

            // 确保食物有日期，如果没有就添加当前日期
            val foodToSave = if (food.scan_date.isEmpty()) {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                food.copy(scan_date = currentDate)
            } else {
                food
            }

            db.collection("users").document(userId)
                .collection("scanned_foods")
                .document(food.barcode)
                .set(foodToSave)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }
}