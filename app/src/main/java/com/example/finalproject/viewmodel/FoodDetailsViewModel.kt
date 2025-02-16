package com.example.finalproject.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.utils.FoodResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class TotalNutrition(
    val energy: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val protein: Double = 0.0
)

data class UiState(
    val foodItems: List<FoodResponse> = emptyList(),
    val totalNutrition: TotalNutrition = TotalNutrition()
)

class FoodDetailsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun fetchFoodDetailsFromFirestore(barcode: String) {
        viewModelScope.launch {
            try {
                Log.d("FoodDetailsViewModel", "Fetching food details for barcode: $barcode")

                val docSnapshot = db.collection("scanned_foods")
                    .document(barcode)
                    .get()
                    .await()

                if (docSnapshot.exists()) {
                    val food = docSnapshot.toObject(FoodResponse::class.java)
                    Log.d("FoodDetailsViewModel", "Found food: $food")

                    food?.let {
                        val foodList = listOf(it)
                        _uiState.value = UiState(
                            foodItems = foodList,
                            totalNutrition = calculateTotalNutrition(foodList)
                        )
                    }
                } else {
                    Log.d("FoodDetailsViewModel", "No food found for barcode: $barcode")
                    _uiState.value = UiState()
                }
            } catch (e: Exception) {
                Log.e("FoodDetailsViewModel", "Error fetching food details", e)
                _uiState.value = UiState()
            }
        }
    }

    fun addCurrentFoodToMainList() {
        viewModelScope.launch {
            val currentFood = _uiState.value.foodItems.firstOrNull()
            currentFood?.let { food ->
                try {
                    // 先检查是否已经存在于 diet_foods 集合
                    val existingDoc = db.collection("diet_foods")
                        .whereEqualTo("barcode", food.barcode)
                        .get()
                        .await()

                    if (existingDoc.isEmpty) {
                        // 如果不存在，添加到 diet_foods 集合
                        db.collection("diet_foods")
                            .document(food.barcode)
                            .set(food)
                            .await()

                        Log.d("FoodDetailsViewModel", "Added food to diet: ${food.product_name}")
                    }

                    // 重新加载所有 diet_foods
                    loadAllDietFoods()
                } catch (e: Exception) {
                    Log.e("FoodDetailsViewModel", "Error adding food to diet", e)
                }
            }
        }
    }

    fun deleteFoodFromFirestore(food: FoodResponse) {
        viewModelScope.launch {
            try {
                db.collection("diet_foods")
                    .document(food.barcode)
                    .delete()
                    .await()

                Log.d("FoodDetailsViewModel", "Successfully deleted food: ${food.product_name}")

                // 重新加载所有 diet_foods
                loadAllDietFoods()
            } catch (e: Exception) {
                Log.e("FoodDetailsViewModel", "Error deleting food", e)
            }
        }
    }

    // 在初始化时加载所有已添加的食物
    init {
        loadAllDietFoods()
    }

    private fun loadAllDietFoods() {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("diet_foods")
                    .get()
                    .await()

                val dietFoods = querySnapshot.toObjects(FoodResponse::class.java)

                if (dietFoods.isNotEmpty()) {
                    _uiState.value = UiState(
                        foodItems = dietFoods,
                        totalNutrition = calculateTotalNutrition(dietFoods)
                    )
                } else {
                    _uiState.value = UiState()
                }
            } catch (e: Exception) {
                Log.e("FoodDetailsViewModel", "Error loading diet foods", e)
                _uiState.value = UiState()
            }
        }
    }

    private fun calculateTotalNutrition(foodItems: List<FoodResponse>): TotalNutrition {
        return TotalNutrition(
            energy = foodItems.sumOf { it.energy_kcal },
            carbs = foodItems.sumOf { it.carbohydrates },
            fat = foodItems.sumOf { it.fat },
            protein = foodItems.sumOf { it.proteins }
        )
    }
}