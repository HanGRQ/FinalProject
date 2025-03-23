package com.example.finalproject.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import com.example.finalproject.ui.components.BottomNavigationBar
import com.example.finalproject.viewmodel.DataViewModel
import com.example.finalproject.utils.FoodResponse
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.example.finalproject.R
import com.example.finalproject.viewmodel.UserInfoViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

@Composable
fun DataScreen(
    userId: String,
    userInfoViewModel: UserInfoViewModel,
    viewModel: DataViewModel,
    onNavigateToWeight: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPersonal: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val dietFoodsList by viewModel.dietFoods.collectAsState(initial = emptyList())
    val scannedFoodsList by viewModel.scannedFoods.collectAsState(initial = emptyList())
    val emotionData by viewModel.emotionData.collectAsState(initial = emptyMap())
    // 添加按日期分组的食物数据
    val foodsByDate by viewModel.foodsByDate.collectAsState(initial = emptyMap())
    // 添加每日糖分摄入量数据
    val dailySugarsIntake by viewModel.dailySugarsIntake.collectAsState(initial = emptyMap())

    val totalEnergyKcal by viewModel.totalEnergyKcal.collectAsState(initial = 0.0)
    val totalSugars by viewModel.totalSugars.collectAsState(initial = 0.0)
    val profileImageUrl by userInfoViewModel.profileImageUrl.collectAsState()

    LaunchedEffect(userId) {
        viewModel.fetchDietFoods(userId)
        viewModel.fetchScannedFoods(userId)
        viewModel.fetchEmotionData(userId)
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "data",
                onNavigate = { route ->
                    when (route) {
                        "weight" -> onNavigateToWeight()
                        "home" -> onNavigateToHome()
                        "personal" -> onNavigateToPersonal()
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = if (profileImageUrl != null && profileImageUrl!!.isNotEmpty()) {
                            rememberImagePainter(
                                data = profileImageUrl,
                                builder = { crossfade(true) }
                            )
                        } else {
                            painterResource(id = R.drawable.profile_image)
                        },
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { onNavigateToPersonal() },
                        contentScale = ContentScale.Crop
                    )
                }
            }

            item {
                Text(
                    text = "Data Analysis",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TabButton(text = "Sugar Analysis", isSelected = selectedTab == 0) { selectedTab = 0 }
                    Spacer(modifier = Modifier.width(8.dp))
                    TabButton(text = "Emotion Analysis", isSelected = selectedTab == 1) { selectedTab = 1 }
                }
            }

            if (selectedTab == 0) {
                // 使用按日期分组的糖分数据显示折线图
                item { DailySugarsLineChart(dailySugarsIntake) }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            NutritionStat("Total Energy", "${totalEnergyKcal.toInt()} kcal", totalEnergyKcal / 2000, Color(0xFFE57373))
                            NutritionStat("Total Sugars", "${totalSugars.toInt()} g", totalSugars / 100, Color(0xFFFFB74D))
                        }
                    }
                }

                // 添加每日糖分摄入量标题
                item {
                    Text(
                        text = "Daily Sugar Intake",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 显示每日糖分摄入量
                items(dailySugarsIntake.entries.toList().sortedBy { it.key }) { (date, sugars) ->
                    DailySugarItem(date = date, sugars = sugars)
                }
            } else if (selectedTab == 1) {
                item { EmotionChartView(emotionData) }
            }

            item {
                Text(
                    text = "History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (selectedTab == 0) {
                // 只使用按日期分组的方式显示历史数据
                if (foodsByDate.isNotEmpty()) {
                    // 跟踪已显示的条形码
                    val displayedBarcodes = mutableSetOf<String>()

                    foodsByDate.entries.toList().sortedByDescending { (date, _) ->
                        try {
                            val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            format.parse(date)?.time ?: 0L
                        } catch (e: Exception) {
                            0L
                        }
                    }.forEach { (date, foods) ->
                        // 过滤掉已经显示过的条形码
                        val uniqueFoods = foods.filter { food -> displayedBarcodes.add(food.barcode) }

                        if (uniqueFoods.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Scanned on $date",
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            items(uniqueFoods) { food ->
                                HistoryItem(name = food.product_name, energy = food.energy_kcal, sugars = food.sugars)
                            }

                            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "No food history available",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            } else if (selectedTab == 1) {
                items(emotionData.entries.toList()) { (date, mood) ->
                    EmotionHistoryItemData(date = date, mood = mood)
                }
            }
        }
    }
}

// 日期糖分图表
@Composable
fun DailySugarsLineChart(dailySugars: Map<String, Double>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Sugar Intake by Date",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 检查数据是否为空
            if (dailySugars.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No sugar data available")
                }
            } else {
                // 获取当前日期作为参考点
                val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

                // 将日期-糖分数据对转换为可排序的列表
                val sortedDatePairs = dailySugars.entries.toList().sortedBy { (dateStr, _) ->
                    try {
                        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        format.parse(dateStr)?.time ?: Long.MAX_VALUE
                    } catch (e: Exception) {
                        Long.MAX_VALUE
                    }
                }

                // 找出当前日期在排序列表中的索引
                val todayIndex = sortedDatePairs.indexOfFirst { (dateStr, _) ->
                    dateStr == today
                }.let { index -> if (index >= 0) index else sortedDatePairs.size / 2 }

                // 重新安排日期顺序，使当前日期居中
                val reorderedPairs = if (todayIndex >= 0) {
                    val before = sortedDatePairs.take(todayIndex)
                    val after = sortedDatePairs.drop(todayIndex + 1)
                    val middle = sortedDatePairs.getOrNull(todayIndex)?.let { listOf(it) } ?: emptyList()

                    // 计算需要的日期数量
                    val totalDates = sortedDatePairs.size
                    val leftDatesNeeded = totalDates / 2
                    val rightDatesNeeded = totalDates - leftDatesNeeded - 1 // -1 是因为中间有一个日期

                    // 从历史日期中取出所需数量
                    val leftSide = before.takeLast(leftDatesNeeded)
                    // 从未来日期中取出所需数量
                    val rightSide = after.take(rightDatesNeeded)

                    leftSide + middle + rightSide
                } else {
                    sortedDatePairs
                }

                // 为图表创建数据点
                val entries = reorderedPairs.mapIndexed { index, (_, sugars) ->
                    Entry(index.toFloat(), sugars.toFloat())
                }

                val dataSet = LineDataSet(entries, "Daily Sugar Intake (g)").apply {
                    color = Color(0xFF1E88E5).hashCode()
                    valueTextColor = Color.Black.hashCode()
                    valueTextSize = 9f
                    lineWidth = 2f
                    // 强调当前日期点
                    if (todayIndex >= 0) {
                        val adjustedTodayIndex = reorderedPairs.indexOfFirst { (dateStr, _) -> dateStr == today }
                        if (adjustedTodayIndex >= 0) {
                            setCircleColor(Color(0xFFFF5722).hashCode()) // 设置当天日期点的颜色
                            circleHoleColor = Color(0xFFFF5722).hashCode()
                            circleRadius = 6f // 设置当天日期点的大小
                        }
                    }
                }
                val lineData = LineData(dataSet)

                // 获取重新排序后的日期标签
                val formattedDates = reorderedPairs.map { (dateStr, _) -> dateStr }

                AndroidView(
                    factory = { context: Context ->
                        LineChart(context).apply {
                            this.data = lineData
                            xAxis.valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    val index = value.toInt()
                                    return if (index >= 0 && index < formattedDates.size) {
                                        formattedDates[index]
                                    } else {
                                        ""
                                    }
                                }
                            }
                            xAxis.labelRotationAngle = 45f
                            description.isEnabled = false
                            legend.isEnabled = true
                            setTouchEnabled(true)
                            setPinchZoom(true)

                            // 强调当前日期位置
                            if (todayIndex >= 0) {
                                val adjustedTodayIndex = reorderedPairs.indexOfFirst { (dateStr, _) -> dateStr == today }
                                if (adjustedTodayIndex >= 0) {
                                    highlightValue(adjustedTodayIndex.toFloat(), 0, false)
                                }
                            }

                            this.invalidate()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                )
            }
        }
    }
}

// 日期糖分项目
@Composable
fun DailySugarItem(date: String, sugars: Double) {
    // 格式化日期显示（如果需要从其他格式转换）
    val formattedDate = try {
        // 检查日期是否已经是 dd-MM-yyyy 格式
        if (!date.matches(Regex("\\d{2}-\\d{2}-\\d{4}"))) {
            // 尝试解析原始日期（假设可能是 yyyy-MM-dd 格式）
            val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val targetFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val parsedDate = originalFormat.parse(date)
            parsedDate?.let { targetFormat.format(it) } ?: date
        } else {
            date
        }
    } catch (e: Exception) {
        // 如果转换失败，则使用原始日期
        date
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formattedDate,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${sugars.toInt()} g",
                color = Color(0xFFFFB74D),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// **Sugar Analysis 折线图** - 保留原有函数但不再使用
@Composable
fun LineChartView(dietFoods: List<FoodResponse>) {
    val entries = dietFoods.mapIndexed { index, food ->
        Entry(index.toFloat(), food.sugars.toFloat())
    }
    val dataSet = LineDataSet(entries, "Sugars (g)").apply {
        color = Color(0xFF1E88E5).hashCode()
        valueTextColor = Color.Black.hashCode()
    }
    val lineData = LineData(dataSet)

    AndroidView(
        factory = { context: Context ->
            LineChart(context).apply {
                this.data = lineData
                this.invalidate()
            }
        },
        modifier = Modifier.fillMaxWidth().height(200.dp)
    )
}

// **Emotion Analysis 柱状图**
@Composable
fun EmotionChartView(emotionData: Map<String, String>) {
    val moodCounts = emotionData.values.groupingBy { it }.eachCount()

    val entries = listOf(
        BarEntry(0f, moodCounts["Good"]?.toFloat() ?: 0f),
        BarEntry(1f, moodCounts["Regular"]?.toFloat() ?: 0f),
        BarEntry(2f, moodCounts["Bad"]?.toFloat() ?: 0f)
    )

    val dataSet = BarDataSet(entries, "Emotion Count").apply {
        colors = listOf(
            Color(0xFF4CAF50).hashCode(),
            Color(0xFFFFC107).hashCode(),
            Color(0xFFE57373).hashCode()
        )
        valueTextColor = Color.Black.hashCode()
    }

    val barData = BarData(dataSet)

    AndroidView(
        factory = { context: Context ->
            BarChart(context).apply {
                data = barData
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return when (value.toInt()) {
                            0 -> "Good"
                            1 -> "Regular"
                            2 -> "Bad"
                            else -> ""
                        }
                    }
                }
                invalidate()
            }
        },
        modifier = Modifier.fillMaxWidth().height(250.dp)
    )
}

// **History 组件**
@Composable
fun HistoryItem(name: String, energy: Double, sugars: Double) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = name, color = Color.Gray, modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "${energy.toInt()} kcal", color = Color(0xFFE57373))
            Text(text = "${sugars.toInt()} g", color = Color(0xFFFFB74D))
        }
    }
}

@Composable
fun EmotionHistoryItemData(date: String, mood: String) {
    val moodIcon = when (mood) {
        "Good" -> R.drawable.ic_mood_good
        "Regular" -> R.drawable.ic_mood_neutral
        "Bad" -> R.drawable.ic_mood_bad
        else -> R.drawable.ic_mood_neutral
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = moodIcon),
                contentDescription = "Mood",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = date, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = mood, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF1B3434) else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.height(36.dp)
    ) {
        Text(text = text)
    }
}

@Composable
fun NutritionStat(label: String, value: String, progress: Double, color: Color) {
    Column {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color.Gray)
        LinearProgressIndicator(
            progress = { progress.toFloat() }, // ✅ 解决过时 API
            modifier = Modifier.width(100.dp).padding(vertical = 4.dp),
            color = color
        )
        Text(text = "${(progress * 100).toInt()}%", fontSize = 12.sp, color = Color.Gray)
    }
}