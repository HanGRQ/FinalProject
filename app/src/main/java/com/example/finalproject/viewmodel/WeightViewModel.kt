package com.example.finalproject.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class WeightEntry(
    val date: String = "",
    val weight: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

data class WeightState(
    val weightEntries: List<WeightEntry> = emptyList(),
    val targetWeight: Double = 0.0,
    val currentWeight: Double = 0.0
)

class WeightViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _weightState = MutableStateFlow(WeightState())
    val weightState: StateFlow<WeightState> = _weightState.asStateFlow()
    private val _bmiResult = MutableStateFlow(0.0)
    val bmiResult: StateFlow<Double> = _bmiResult.asStateFlow()

    fun addWeightEntry(userId: String, date: String, weight: Double) {
        viewModelScope.launch {
            try {
                val entry = WeightEntry(date, weight)
                db.collection("users").document(userId)
                    .collection("weight_entries")
                    .add(entry)
                    .await()

                fetchWeightEntries(userId) // ✅ 确保 fetch 仅获取当前用户数据
            } catch (e: Exception) {
                Log.e("WeightViewModel", "Error adding weight entry", e)
            }
        }
    }


    fun setTargetWeight(userId: String, weight: Double) {
        viewModelScope.launch {
            try {
                db.collection("users").document(userId)
                    .collection("user_settings")
                    .document("target_weight")
                    .set(mapOf("weight" to weight))
                    .await()

                _weightState.update { currentState ->
                    currentState.copy(targetWeight = weight)
                }
            } catch (e: Exception) {
                Log.e("WeightViewModel", "Error setting target weight", e)
            }
        }
    }

    fun setHeight(userId: String, height: Double) {
        viewModelScope.launch {
            try {
                db.collection("users").document(userId)
                    .collection("user_settings")
                    .document("height")
                    .set(mapOf("value" to height))
                    .await()
            } catch (e: Exception) {
                Log.e("WeightViewModel", "Error setting height", e)
            }
        }
    }

    fun fetchWeightEntries(userId: String) {
        viewModelScope.launch {
            try {
                val weightQuery = db.collection("users").document(userId)
                    .collection("weight_entries")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val entries = weightQuery.toObjects(WeightEntry::class.java)

                val targetWeightDoc = db.collection("users").document(userId)
                    .collection("user_settings")
                    .document("target_weight")
                    .get()
                    .await()

                val targetWeight = targetWeightDoc.getDouble("weight") ?: 0.0

                _weightState.update {
                    WeightState(
                        weightEntries = entries,
                        targetWeight = targetWeight,
                        currentWeight = entries.firstOrNull()?.weight ?: 0.0
                    )
                }
            } catch (e: Exception) {
                Log.e("WeightViewModel", "Error fetching weight entries", e)
            }
        }
    }

    fun calculateBMI(userId: String) {
        viewModelScope.launch {
            try {
                val heightDoc = db.collection("users").document(userId)
                    .collection("user_settings")
                    .document("height")
                    .get()
                    .await()

                val heightInCentimeters = heightDoc.getDouble("value") ?: 0.0
                val weight = weightState.value.currentWeight

                if (heightInCentimeters > 0 && weight > 0) {
                    val heightInMeters = heightInCentimeters / 100
                    val bmi = weight / (heightInMeters * heightInMeters)

                    db.collection("users").document(userId)
                        .collection("user_settings")
                        .document("bmi")
                        .set(mapOf("value" to bmi))
                        .await()

                    _bmiResult.value = bmi
                }
            } catch (e: Exception) {
                Log.e("WeightViewModel", "Error calculating BMI", e)
            }
        }
    }

    fun fetchBMI(userId: String) {
        viewModelScope.launch {
            try {
                val bmiDoc = db.collection("users").document(userId)
                    .collection("user_settings")
                    .document("bmi")
                    .get()
                    .await()

                val bmi = bmiDoc.getDouble("value") ?: 0.0
                _bmiResult.value = bmi
            } catch (e: Exception) {
                Log.e("WeightViewModel", "Error fetching BMI", e)
            }
        }
    }
}
