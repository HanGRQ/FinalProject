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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TotalNutrition(
    val energy: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val protein: Double = 0.0,
    val totalSugars: Double = 0.0
)

data class UiState(
    val foodItems: List<FoodResponse> = emptyList(),
    val totalNutrition: TotalNutrition = TotalNutrition(),
    val foodsByDate: Map<String, List<FoodResponse>> = emptyMap() // 添加按日期分组的食物
)

class FoodDetailsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    // 日期格式定义
    private val storageDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    // 保存扫描的食物并添加日期
    fun saveScannedFood(userId: String, barcode: String, food: FoodResponse) {
        viewModelScope.launch {
            try {
                // 确保食物有日期，以存储格式 yyyy-MM-dd 保存
                val foodToSave = if (food.scan_date.isEmpty()) {
                    val currentDate = storageDateFormat.format(Date())
                    food.copy(scan_date = currentDate)
                } else {
                    // 如果日期是 dd-MM-yyyy 格式，转换为 yyyy-MM-dd
                    try {
                        if (food.scan_date.matches(Regex("\\d{2}-\\d{2}-\\d{4}"))) {
                            val date = displayDateFormat.parse(food.scan_date)
                            val formattedDate = date?.let { storageDateFormat.format(it) } ?: food.scan_date
                            food.copy(scan_date = formattedDate)
                        } else {
                            food
                        }
                    } catch (e: Exception) {
                        food
                    }
                }

                db.collection("users").document(userId)
                    .collection("scanned_foods")
                    .document(barcode)
                    .set(foodToSave)
                    .await()
                Log.d("FoodDetailsViewModel", "Scanned food saved: ${food.product_name} on date: ${foodToSave.scan_date}")
            } catch (e: Exception) {
                Log.e("FoodDetailsViewModel", "Error saving scanned food", e)
            }
        }
    }

    // 从 Firestore 获取扫描食物的详情
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
                        val foodsByDate = groupFoodsByDate(listOf(it))
                        _uiState.value = UiState(
                            foodItems = listOf(it),
                            totalNutrition = calculateTotalNutrition(listOf(it)),
                            foodsByDate = foodsByDate
                        )
                        Log.d("FoodDetailsViewModel", "Loaded scanned food: ${it.product_name} from date: ${it.scan_date}")
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

    // 加载所有饮食数据并按日期分组
    fun loadAllDietFoods(userId: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("users").document(userId)
                    .collection("diet_foods")
                    .get()
                    .await()

                val dietFoods = querySnapshot.toObjects(FoodResponse::class.java)

                if (dietFoods.isNotEmpty()) {
                    val foodsByDate = groupFoodsByDate(dietFoods)
                    _uiState.value = UiState(
                        foodItems = dietFoods,
                        totalNutrition = calculateTotalNutrition(dietFoods),
                        foodsByDate = foodsByDate
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

    // 加载用户已扫描的食物并按日期分组
    fun loadScannedFoods(userId: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("users").document(userId)
                    .collection("scanned_foods")
                    .get()
                    .await()

                val scannedFoods = querySnapshot.toObjects(FoodResponse::class.java)

                if (scannedFoods.isNotEmpty()) {
                    val foodsByDate = groupFoodsByDate(scannedFoods)
                    _uiState.value = UiState(
                        foodItems = scannedFoods,
                        totalNutrition = calculateTotalNutrition(scannedFoods),
                        foodsByDate = foodsByDate
                    )
                } else {
                    _uiState.value = UiState()
                }
            } catch (e: Exception) {
                Log.e("FoodDetailsViewModel", "Error loading scanned foods", e)
                _uiState.value = UiState()
            }
        }
    }

    // 添加食物到 diet_foods 并确保有日期
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
                        // 确保食物有日期，以存储格式 yyyy-MM-dd 保存
                        val foodToSave = if (food.scan_date.isEmpty()) {
                            val currentDate = storageDateFormat.format(Date())
                            food.copy(scan_date = currentDate)
                        } else {
                            // 如果日期是 dd-MM-yyyy 格式，转换为 yyyy-MM-dd
                            try {
                                if (food.scan_date.matches(Regex("\\d{2}-\\d{2}-\\d{4}"))) {
                                    val date = displayDateFormat.parse(food.scan_date)
                                    val formattedDate = date?.let { storageDateFormat.format(it) } ?: food.scan_date
                                    food.copy(scan_date = formattedDate)
                                } else {
                                    food
                                }
                            } catch (e: Exception) {
                                food
                            }
                        }

                        db.collection("users").document(userId)
                            .collection("diet_foods")
                            .document(food.barcode)
                            .set(foodToSave)
                            .await()
                        Log.d("FoodDetailsViewModel", "Added to diet: ${food.product_name} with date: ${foodToSave.scan_date}")
                    }

                    // 重新加载用户的 diet_foods
                    loadAllDietFoods(userId)
                } catch (e: Exception) {
                    Log.e("FoodDetailsViewModel", "Error adding food to diet", e)
                }
            }
        }
    }

    // 删除 diet_foods 里的食物
    fun deleteFoodFromFirestore(userId: String, food: FoodResponse) {
        viewModelScope.launch {
            try {
                db.collection("users").document(userId)
                    .collection("diet_foods")
                    .document(food.barcode)
                    .delete()
                    .await()

                Log.d("FoodDetailsViewModel", "Deleted from diet: ${food.product_name}")

                // 重新加载 diet_foods
                loadAllDietFoods(userId)
            } catch (e: Exception) {
                Log.e("FoodDetailsViewModel", "Error deleting food", e)
            }
        }
    }

    // 格式化日期显示（从存储格式转为显示格式）
    private fun formatDateForDisplay(dateStr: String): String {
        if (dateStr.isEmpty()) return "No Date"

        try {
            // 已经是显示格式，直接返回
            if (dateStr.matches(Regex("\\d{2}-\\d{2}-\\d{4}"))) {
                return dateStr
            }

            // 从存储格式转为显示格式
            val date = storageDateFormat.parse(dateStr)
            return date?.let { displayDateFormat.format(it) } ?: dateStr
        } catch (e: ParseException) {
            return dateStr
        }
    }

    // 获取日期的时间戳，用于排序
    private fun getDateTimestamp(dateStr: String): Long {
        try {
            // 尝试解析不同格式的日期
            val date = when {
                dateStr.isEmpty() -> null
                dateStr.matches(Regex("\\d{2}-\\d{2}-\\d{4}")) -> displayDateFormat.parse(dateStr)
                else -> storageDateFormat.parse(dateStr)
            }
            return date?.time ?: 0L
        } catch (e: ParseException) {
            return 0L
        }
    }

    // 按日期分组食物列表，并转换日期格式
    private fun groupFoodsByDate(foods: List<FoodResponse>): Map<String, List<FoodResponse>> {
        // 按日期分组
        val groupedFoods = foods.groupBy {
            val originalDate = it.scan_date.takeIf { date -> date.isNotEmpty() } ?: "No Date"
            formatDateForDisplay(originalDate) // 转换为显示格式
        }

        // 返回按日期排序的 Map
        return groupedFoods.toSortedMap { date1, date2 ->
            // 比较日期，按时间先后排序
            val time1 = getDateTimestamp(date1)
            val time2 = getDateTimestamp(date2)
            time1.compareTo(time2)
        }
    }

    // 计算给定日期的总糖分
    fun calculateSugarsByDate(date: String): Double {
        // 日期可能是显示格式或存储格式，先尝试转换为显示格式
        val displayDate = formatDateForDisplay(date)
        return _uiState.value.foodsByDate[displayDate]?.sumOf { it.sugars } ?: 0.0
    }

    // 计算所有食物的总营养信息
    private fun calculateTotalNutrition(foodItems: List<FoodResponse>): TotalNutrition {
        return TotalNutrition(
            energy = foodItems.sumOf { it.energy_kcal },
            carbs = foodItems.sumOf { it.carbohydrates },
            fat = foodItems.sumOf { it.fat },
            protein = foodItems.sumOf { it.proteins },
            totalSugars = foodItems.sumOf { it.sugars }
        )
    }
}