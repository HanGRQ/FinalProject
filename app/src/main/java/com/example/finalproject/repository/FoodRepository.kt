package com.example.finalproject.repository

import android.util.Log
import com.example.finalproject.api.OpenFoodFactsService
import com.example.finalproject.utils.FoodResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
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

                // 修改转换逻辑以匹配 FoodResponse 的字段
                val foodResponse = FoodResponse(
                    barcode = barcode,
                    product_name = product.product.product_name ?: "Unknown",
                    energy_kj = product.product.nutriments?.energyKj ?: 0.0,
                    energy_kcal = product.product.nutriments?.energyKcal ?: 0.0,
                    carbohydrates = product.product.nutriments?.carbohydrates ?: 0.0,
                    sugars = product.product.nutriments?.sugars ?: 0.0,
                    fat = product.product.nutriments?.fat ?: 0.0,
                    proteins = product.product.nutriments?.proteins ?: 0.0
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
            db.collection("users").document(userId)
                .collection("scanned_foods")
                .document(food.barcode)
                .set(food)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }



}