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

    // 将日期格式从 YYYY-MM-DD 转换为 DD-MM-YYYY
    private fun formatDateToDisplay(date: String): String {
        return if (date.matches(Regex("\\d{4}-\\d{1,2}-\\d{1,2}"))) {
            val parts = date.split("-")
            "${parts[2]}-${parts[1]}-${parts[0]}"
        } else {
            date
        }
    }

    fun updateDate(userId: String, newDate: String) {
        _selectedDate.value = newDate
        fetchEmotionStatus(userId, newDate)
    }

    fun fetchEmotionStatus(userId: String, date: String) {
        // 尝试新格式
        firestore.collection("users").document(userId)
            .collection("emotion_status")
            .document(date)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _moodStatus.value = document.getString("mood") ?: "No Data"
                } else {
                    // 如果新格式不存在，尝试旧格式
                    val oldFormatDate = if (date.matches(Regex("\\d{1,2}-\\d{1,2}-\\d{4}"))) {
                        val parts = date.split("-")
                        "${parts[2]}-${parts[1]}-${parts[0]}"
                    } else {
                        date
                    }

                    firestore.collection("users").document(userId)
                        .collection("emotion_status")
                        .document(oldFormatDate)
                        .get()
                        .addOnSuccessListener { oldDocument ->
                            _moodStatus.value = oldDocument.getString("mood") ?: "No Data"
                        }
                }
            }
    }

    fun fetchAllEmotions(userId: String) {
        firestore.collection("users").document(userId)
            .collection("emotion_status")
            .get()
            .addOnSuccessListener { result ->
                val emotions = result.documents.mapNotNull { doc ->
                    val date = doc.id
                    val mood = doc.getString("mood") ?: return@mapNotNull null

                    // 将日期格式统一为 DD-MM-YYYY 显示
                    val formattedDate = formatDateToDisplay(date)

                    EmotionRecord(formattedDate, mood)
                }
                _allEmotions.value = emotions.sortedByDescending { it.date }
            }
    }

    fun saveEmotionStatus(userId: String, date: String, mood: String) {
        viewModelScope.launch {
            // 确保日期格式为 DD-MM-YYYY
            val formattedDate = formatDateToDisplay(date)

            firestore.collection("users").document(userId)
                .collection("emotion_status")
                .document(formattedDate)
                .set(mapOf("mood" to mood))
                .addOnSuccessListener {
                    _moodStatus.value = mood
                    fetchAllEmotions(userId)
                    fetchEmotionStatus(userId, formattedDate)
                }
        }
    }

    // 数据迁移函数 - 将旧格式(YYYY-MM-DD)转换为新格式(DD-MM-YYYY)
    fun migrateEmotionData(userId: String) {
        firestore.collection("users").document(userId)
            .collection("emotion_status")
            .get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    val oldDate = document.id
                    val mood = document.getString("mood") ?: continue

                    // 如果是旧格式，创建新格式文档
                    if (oldDate.matches(Regex("\\d{4}-\\d{1,2}-\\d{1,2}"))) {
                        val parts = oldDate.split("-")
                        val newDate = "${parts[2]}-${parts[1]}-${parts[0]}"

                        // 创建新文档
                        firestore.collection("users").document(userId)
                            .collection("emotion_status")
                            .document(newDate)
                            .set(mapOf("mood" to mood))
                            .addOnSuccessListener {
                                // 成功后删除旧文档
                                document.reference.delete()
                            }
                    }
                }
            }
    }
}

fun getCurrentDate(): String {
    val calendar = Calendar.getInstance()
    return "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"
}