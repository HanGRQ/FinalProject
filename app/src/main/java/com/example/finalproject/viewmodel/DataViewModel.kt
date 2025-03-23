package com.example.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.utils.FoodResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    // 新增: 按日期分组的食物数据
    private val _foodsByDate = MutableStateFlow<Map<String, List<FoodResponse>>>(emptyMap())
    val foodsByDate: StateFlow<Map<String, List<FoodResponse>>> = _foodsByDate

    // 新增: 每日糖分摄入量
    private val _dailySugarsIntake = MutableStateFlow<Map<String, Double>>(emptyMap())
    val dailySugarsIntake: StateFlow<Map<String, Double>> = _dailySugarsIntake

    // 日期格式转换工具
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    /**
     * ✅ **获取用户的 `diet_foods` 数据**
     */
    fun fetchDietFoods(userId: String) {
        viewModelScope.launch {
            db.collection("users").document(userId).collection("diet_foods")
                .get()
                .addOnSuccessListener { result ->
                    val foods = result.toObjects(FoodResponse::class.java)
                    _dietFoods.value = foods
                    calculateTotals()
                    processFoodsByDate() // 处理按日期分组的数据
                }
        }
    }

    /**
     * ✅ **获取用户的 `scanned_foods` 数据**
     */
    fun fetchScannedFoods(userId: String) {
        viewModelScope.launch {
            db.collection("users").document(userId).collection("scanned_foods")
                .get()
                .addOnSuccessListener { result ->
                    val foods = result.toObjects(FoodResponse::class.java)
                    _scannedFoods.value = foods
                    calculateTotals()
                    processFoodsByDate() // 处理按日期分组的数据
                }
        }
    }

    /**
     * ✅ **获取用户的 `emotion_status` 数据**
     */
    fun fetchEmotionData(userId: String) {
        viewModelScope.launch {
            db.collection("users").document(userId).collection("emotion_status")
                .get()
                .addOnSuccessListener { result ->
                    val emotions = result.documents.associate { doc ->
                        val dateId = doc.id
                        val mood = doc.getString("mood") ?: "No Data"

                        // 尝试正确格式化日期
                        val formattedDate = try {
                            // 如果 dateId 是 yyyy-MM-dd 格式
                            if (dateId.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                                val parsedDate = inputFormat.parse(dateId)
                                parsedDate?.let { outputFormat.format(it) } ?: dateId
                            } else {
                                dateId // 如果不是，保持原样
                            }
                        } catch (e: Exception) {
                            // 出错时使用原始日期ID
                            dateId
                        }

                        formattedDate to mood
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
            totalEnergy += food.energy_kcal
            totalSugar += food.sugars
        }

        _totalEnergyKcal.value = totalEnergy
        _totalSugars.value = totalSugar
    }

    /**
     * 格式化日期从 yyyy-MM-dd 到 dd-MM-yyyy
     */
    private fun formatDate(dateStr: String): String {
        return try {
            if (dateStr.isEmpty()) return "No Date"

            // 检查日期是否已经是 dd-MM-yyyy 格式
            if (dateStr.matches(Regex("\\d{2}-\\d{2}-\\d{4}"))) {
                return dateStr
            }

            val date = inputFormat.parse(dateStr)
            date?.let { outputFormat.format(it) } ?: "Unknown Date"
        } catch (e: ParseException) {
            dateStr // 如果解析失败，返回原始字符串
        }
    }

    /**
     * 日期字符串转为Date对象用于排序
     */
    private fun dateToMillis(dateStr: String): Long {
        return try {
            if (dateStr.isEmpty()) return 0L

            // 尝试解析不同格式的日期
            if (dateStr.matches(Regex("\\d{2}-\\d{2}-\\d{4}"))) {
                outputFormat.parse(dateStr)?.time ?: 0L
            } else {
                inputFormat.parse(dateStr)?.time ?: 0L
            }
        } catch (e: ParseException) {
            0L // 解析失败返回0
        }
    }

    /**
     * 按日期处理食物数据
     */
    private fun processFoodsByDate() {
        val allFoods = _dietFoods.value + _scannedFoods.value

        // 按日期分组，注意处理空日期和格式转换
        val foodsByDateMap = allFoods.groupBy { food ->
            val originalDate = food.scan_date.takeIf { it.isNotEmpty() } ?: "No Date"
            formatDate(originalDate)
        }
        _foodsByDate.value = foodsByDateMap

        // 计算每日糖分摄入量
        val dailySugarsByDate = foodsByDateMap.mapValues { (_, foodList) ->
            foodList.sumOf { it.sugars }
        }
        _dailySugarsIntake.value = dailySugarsByDate
    }

    /**
     * 获取指定日期的糖分摄入量
     */
    fun getSugarsForDate(date: String): Double {
        val formattedDate = formatDate(date)
        return _dailySugarsIntake.value[formattedDate] ?: 0.0
    }

    /**
     * 获取所有日期（按时间顺序排序）
     */
    fun getAllDates(): List<String> {
        return _foodsByDate.value.keys.sortedBy { dateStr ->
            dateToMillis(dateStr)
        }
    }

    // 在 DataViewModel 中添加
    // 糖分摄入量限额 (g)
    private val SUGAR_INTAKE_LIMIT = 50.0

    // 获取指定日期的情绪状态
    fun getMoodForDate(date: String): String {
        return _emotionData.value[date] ?: "No Data"
    }

    // 检查指定日期的糖分摄入是否超过限额
    fun isSugarExceedingLimit(date: String): Boolean {
        val sugarAmount = _dailySugarsIntake.value[date] ?: 0.0
        return sugarAmount > SUGAR_INTAKE_LIMIT
    }

    // 获取基于情绪和糖分摄入的提示信息
    fun getTipMessageForMoodAndSugar(date: String): String {
        val mood = getMoodForDate(date)
        val isExceeding = isSugarExceedingLimit(date)

        return when {
            mood == "Good" && isExceeding -> "Even when you're feeling good, watch your sugar intake!"
            mood == "Good" && !isExceeding -> "Keep up the good work!"
            mood == "Regular" && isExceeding -> "Please monitor your sugar intake."
            mood == "Regular" && !isExceeding -> "Keep it up!"
            mood == "Bad" && isExceeding -> "Even when you're feeling down, mind your sugar intake."
            mood == "Bad" && !isExceeding -> "Keep up the good work!"
            else -> "Keep monitoring your sugar intake."
        }
    }
}