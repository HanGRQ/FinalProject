package com.example.finalproject.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.finalproject.utils.FoodData
import com.example.finalproject.utils.FoodDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TotalNutrition(
    val energy: Int = 0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val protein: Double = 0.0
)

data class FoodUiState(
    val foodItems: List<FoodData> = emptyList(),
    val totalNutrition: TotalNutrition = TotalNutrition(
        energy = 0,
        carbs = 0.0,
        fat = 0.0,
        protein = 0.0
    )
)

class FoodDetailsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FoodUiState())
    val uiState = _uiState.asStateFlow()

    init {
        Log.d("FoodDetailsViewModel", "ViewModel initialized with hashCode: ${this.hashCode()}")
        Log.d("FoodDetailsViewModel", "Initial state: ${_uiState.value}")
    }

    fun addFood(foodDetails: FoodDetails) {
        Log.d("FoodDetailsViewModel", "Adding food: ${foodDetails.name}")
        Log.d("FoodDetailsViewModel", "Current state before update: ${_uiState.value}")

        val newFood = FoodData(
            name = foodDetails.name,
            portion = "1 serving",
            calories = foodDetails.totalEnergyKcal.toInt(),
            carbs = foodDetails.carbohydrates,
            fat = foodDetails.fat,
            protein = foodDetails.protein
        )

        val currentState = _uiState.value
        val updatedFoodItems = currentState.foodItems + newFood
        val updatedTotalNutrition = currentState.totalNutrition.copy(
            energy = currentState.totalNutrition.energy + foodDetails.totalEnergyKcal.toInt(),
            carbs = currentState.totalNutrition.carbs + foodDetails.carbohydrates,
            fat = currentState.totalNutrition.fat + foodDetails.fat,
            protein = currentState.totalNutrition.protein + foodDetails.protein
        )

        _uiState.value = currentState.copy(
            foodItems = updatedFoodItems,
            totalNutrition = updatedTotalNutrition
        )

        Log.d("FoodDetailsViewModel", "State after update: ${_uiState.value}")
    }

    fun deleteFood(food: FoodData) {
        _uiState.update { currentState ->
            val updatedFoodItems = currentState.foodItems.filter { it != food }
            val updatedTotalNutrition = currentState.totalNutrition.copy(
                energy = currentState.totalNutrition.energy - food.calories,
                carbs = currentState.totalNutrition.carbs - food.carbs,
                fat = currentState.totalNutrition.fat - food.fat,
                protein = currentState.totalNutrition.protein - food.protein
            )
            currentState.copy(
                foodItems = updatedFoodItems,
                totalNutrition = updatedTotalNutrition
            )
        }
    }
}