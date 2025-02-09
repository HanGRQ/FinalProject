package com.example.finalproject.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.utils.FoodResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
        val formattedBarcode = barcode.padStart(13, '0')
        Log.d("Firestore", "Querying barcode: $formattedBarcode")

        db.collection("foods")
            .whereEqualTo("barcode", formattedBarcode)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val foodList = documents.mapNotNull { it.toObject(FoodResponse::class.java) }
                    _uiState.value = UiState(foodList, calculateTotalNutrition(foodList))
                    Log.d("Firestore", "Successfully fetched food items")
                } else {
                    Log.d("Firestore", "No food items found for barcode: $formattedBarcode")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching food items", e)
            }
    }

    fun deleteFoodFromFirestore(food: FoodResponse) {
        val formattedBarcode = food.barcode.padStart(13, '0')
        db.collection("foods").document(formattedBarcode)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "✅ 已删除食品: ${food.product_name}")
                val updatedList = _uiState.value.foodItems.filter { it.barcode != formattedBarcode }
                _uiState.value = UiState(updatedList, calculateTotalNutrition(updatedList))
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ 删除失败: ${e.message}")
            }
    }

    private fun calculateTotalNutrition(foodItems: List<FoodResponse>): TotalNutrition {
        val energy = foodItems.sumOf { it.energy_kcal ?: 0.0 }
        val carbs = foodItems.sumOf { it.carbohydrates ?: 0.0 }
        val fat = foodItems.sumOf { it.fat ?: 0.0 }
        val protein = foodItems.sumOf { it.proteins ?: 0.0 }
        return TotalNutrition(energy, carbs, fat, protein)
    }
}