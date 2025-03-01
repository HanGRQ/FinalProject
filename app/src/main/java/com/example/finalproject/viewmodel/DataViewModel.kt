package com.example.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.utils.FoodResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DataViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _dietFoods = MutableStateFlow<List<FoodResponse>>(emptyList())
    val dietFoods: StateFlow<List<FoodResponse>> = _dietFoods

    private val _scannedFoods = MutableStateFlow<List<FoodResponse>>(emptyList())
    val scannedFoods: StateFlow<List<FoodResponse>> = _scannedFoods

    private val _totalEnergyKcal = MutableStateFlow(0.0)
    val totalEnergyKcal: StateFlow<Double> = _totalEnergyKcal

    private val _totalSugars = MutableStateFlow(0.0)
    val totalSugars: StateFlow<Double> = _totalSugars

    private val _emotionData = MutableStateFlow<Map<String, String>>(emptyMap())
    val emotionData: StateFlow<Map<String, String>> = _emotionData

    /**
     * ✅ **获取用户的 `diet_foods` 数据**
     */
    fun fetchDietFoods(userId: String) {
        viewModelScope.launch {
            db.collection("users").document(userId).collection("diet_foods") // ✅ 读取该用户数据
                .get()
                .addOnSuccessListener { result ->
                    val foods = result.toObjects(FoodResponse::class.java)
                    _dietFoods.value = foods
                    calculateTotals()
                }
        }
    }

    /**
     * ✅ **获取用户的 `scanned_foods` 数据**
     */
    fun fetchScannedFoods(userId: String) {
        viewModelScope.launch {
            db.collection("users").document(userId).collection("scanned_foods") // ✅ 读取该用户数据
                .get()
                .addOnSuccessListener { result ->
                    val foods = result.toObjects(FoodResponse::class.java)
                    _scannedFoods.value = foods
                    calculateTotals()
                }
        }
    }

    /**
     * ✅ **获取用户的 `emotion_status` 数据**
     */
    fun fetchEmotionData(userId: String) {
        viewModelScope.launch {
            db.collection("users").document(userId).collection("emotion_status") // ✅ 读取该用户数据
                .get()
                .addOnSuccessListener { result ->
                    val emotions = result.documents.associate { doc ->
                        val date = doc.id
                        val mood = doc.getString("mood") ?: "No Data"
                        date to mood
                    }
                    _emotionData.value = emotions
                }
        }
    }

    /**
     * ✅ **计算总能量和总糖分**
     */
    private fun calculateTotals() {
        val allFoods = _dietFoods.value + _scannedFoods.value
        var totalEnergy = 0.0
        var totalSugar = 0.0

        allFoods.forEach { food ->
            totalEnergy += food.energy_kcal ?: 0.0
            totalSugar += food.sugars ?: 0.0
        }

        _totalEnergyKcal.value = totalEnergy
        _totalSugars.value = totalSugar
    }
}
