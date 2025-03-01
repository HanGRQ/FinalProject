package com.example.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class EmotionRecord(val date: String, val mood: String)

class EmotionViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _selectedDate = MutableStateFlow(getCurrentDate())
    val selectedDate: StateFlow<String> = _selectedDate

    private val _moodStatus = MutableStateFlow<String?>(null)
    val moodStatus: StateFlow<String?> = _moodStatus

    private val _allEmotions = MutableStateFlow<List<EmotionRecord>>(emptyList())
    val allEmotions: StateFlow<List<EmotionRecord>> = _allEmotions

    fun updateDate(newDate: String) {
        _selectedDate.value = newDate
        fetchEmotionStatus(newDate)
    }

    fun fetchEmotionStatus(date: String) {
        firestore.collection("emotion_status")
            .document(date)
            .get()
            .addOnSuccessListener { document ->
                _moodStatus.value = document.getString("mood") ?: "No Data"
            }
    }

    fun fetchAllEmotions() {
        firestore.collection("emotion_status")
            .get()
            .addOnSuccessListener { result ->
                val emotions = result.documents.mapNotNull { doc ->
                    val date = doc.id
                    val mood = doc.getString("mood") ?: return@mapNotNull null
                    EmotionRecord(date, mood)
                }
                _allEmotions.value = emotions.sortedByDescending { it.date }
            }
    }

    fun saveEmotionStatus(date: String, mood: String) {
        viewModelScope.launch {
            firestore.collection("emotion_status")
                .document(date)
                .set(mapOf("mood" to mood))
                .addOnSuccessListener {
                    _moodStatus.value = mood
                    fetchAllEmotions() // 更新记录
                }
        }
    }
}

// 获取当前日期
fun getCurrentDate(): String {
    val calendar = Calendar.getInstance()
    return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
}
