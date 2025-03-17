package com.example.finalproject.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.utils.FoodResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class TotalNutrition(
    val energy: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val protein: Double = 0.0,
    val totalSugars: Double = 0.0 // ✅ 添加 `totalSugars`
)


data class UiState(
    val foodItems: List<FoodResponse> = emptyList(),
    val totalNutrition: TotalNutrition = TotalNutrition()
)

class FoodDetailsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    // ✅ **扫描完成后，存入用户的 `scanned_foods`**
    fun saveScannedFood(userId: String, barcode: String, food: FoodResponse) {
        viewModelScope.launch {
            try {
                db.collection("users").document(userId)
                    .collection("scanned_foods")
                    .document(barcode)
                    .set(food)
                    .await()
                Log.d("FoodDetailsViewModel", "Scanned food saved: ${food.product_name}")
            } catch (e: Exception) {
                Log.e("FoodDetailsViewModel", "Error saving scanned food", e)
            }
        }
    }

    // ✅ **从 `scanned_foods` 获取扫描的食物**
    fun fetchFoodDetailsFromFirestore(userId: String, barcode: String) {
        viewModelScope.launch {
            try {
                val docSnapshot = db.collection("users").document(userId)
                    .collection("scanned_foods")
                    .document(barcode)
                    .get()
                    .await()

                if (docSnapshot.exists()) {
                    val food = docSnapshot.toObject(FoodResponse::class.java)
                    food?.let {
                        _uiState.value = UiState(
                            foodItems = listOf(it),
                            totalNutrition = calculateTotalNutrition(listOf(it))
                        )
                        Log.d("FoodDetailsViewModel", "Loaded scanned food: ${it.product_name}")
                    }
                } else {
                    _uiState.value = UiState()
                    Log.d("FoodDetailsViewModel", "No scanned food found for barcode: $barcode")
                }
            } catch (e: Exception) {
                Log.e("FoodDetailsViewModel", "Error fetching food details", e)
                _uiState.value = UiState()
            }
        }
    }

    // ✅ **从 `diet_foods` 获取用户的所有饮食数据**
    fun loadAllDietFoods(userId: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("users").document(userId)
                    .collection("diet_foods")
                    .get()
                    .await()

                val dietFoods = querySnapshot.toObjects(FoodResponse::class.java)

                _uiState.value = if (dietFoods.isNotEmpty()) {
                    UiState(
                        foodItems = dietFoods,
                        totalNutrition = calculateTotalNutrition(dietFoods) // ✅ 计算 `totalEnergy` 和 `totalSugars`
                    )
                } else {
                    UiState()
                }
            } catch (e: Exception) {
                Log.e("FoodDetailsViewModel", "Error loading diet foods", e)
                _uiState.value = UiState()
            }
        }
    }


    // ✅ **加载用户已扫描的食物**
    fun loadScannedFoods(userId: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("users").document(userId)
                    .collection("scanned_foods")
                    .get()
                    .await()

                val scannedFoods = querySnapshot.toObjects(FoodResponse::class.java)

                _uiState.value = if (scannedFoods.isNotEmpty()) {
                    UiState(
                        foodItems = scannedFoods,
                        totalNutrition = calculateTotalNutrition(scannedFoods)
                    )
                } else {
                    UiState()
                }
            } catch (e: Exception) {
                Log.e("FoodDetailsViewModel", "Error loading scanned foods", e)
                _uiState.value = UiState()
            }
        }
    }

    // ✅ **添加食物到 `diet_foods`**
    fun addCurrentFoodToMainList(userId: String) {
        viewModelScope.launch {
            val currentFood = _uiState.value.foodItems.firstOrNull()
            currentFood?.let { food ->
                try {
                    val existingDoc = db.collection("users").document(userId)
                        .collection("diet_foods")
                        .whereEqualTo("barcode", food.barcode)
                        .get()
                        .await()

                    if (existingDoc.isEmpty) {
                        db.collection("users").document(userId)
                            .collection("diet_foods")
                            .document(food.barcode)
                            .set(food)
                            .await()
                        Log.d("FoodDetailsViewModel", "Added to diet: ${food.product_name}")
                    }

                    // **重新加载用户的 `diet_foods`**
                    loadAllDietFoods(userId)
                } catch (e: Exception) {
                    Log.e("FoodDetailsViewModel", "Error adding food to diet", e)
                }
            }
        }
    }

    // ✅ **删除 `diet_foods` 里的食物**
    fun deleteFoodFromFirestore(userId: String, food: FoodResponse) {
        viewModelScope.launch {
            try {
                db.collection("users").document(userId)
                    .collection("diet_foods")
                    .document(food.barcode)
                    .delete()
                    .await()

                Log.d("FoodDetailsViewModel", "Deleted from diet: ${food.product_name}")

                // **重新加载 `diet_foods`**
                loadAllDietFoods(userId)
            } catch (e: Exception) {
                Log.e("FoodDetailsViewModel", "Error deleting food", e)
            }
        }
    }

    // **计算所有食物的总营养信息**
    private fun calculateTotalNutrition(foodItems: List<FoodResponse>): TotalNutrition {
        return TotalNutrition(
            energy = foodItems.sumOf { it.energy_kcal },
            carbs = foodItems.sumOf { it.carbohydrates },
            fat = foodItems.sumOf { it.fat },
            protein = foodItems.sumOf { it.proteins },
            totalSugars = foodItems.sumOf { it.sugars } // ✅ 计算 `totalSugars`
        )
    }

}
