package com.example.finalproject.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
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
                // 显示每日糖分摄入量
                items(dailySugarsIntake.entries.toList().sortedBy { (dateStr, _) ->
                    try {
                        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        format.parse(dateStr)?.time ?: Long.MAX_VALUE
                    } catch (e: Exception) {
                        Long.MAX_VALUE
                    }
                }) { (date, sugars) ->
                    DailySugarItem(
                        date = date,
                        sugars = sugars,
                        viewModel = viewModel
                    )
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

                // 将日期-糖分数据对转换为可排序的列表并排序
                val sortedDatePairs = dailySugars.entries.toList().sortedBy { (dateStr, _) ->
                    try {
                        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        format.parse(dateStr)?.time ?: Long.MAX_VALUE
                    } catch (e: Exception) {
                        Long.MAX_VALUE
                    }
                }

                // 为图表创建数据点
                val entries = sortedDatePairs.mapIndexed { index, (_, sugars) ->
                    Entry(index.toFloat(), sugars.toFloat())
                }

                val dataSet = LineDataSet(entries, "Daily Sugar Intake (g)").apply {
                    color = Color(0xFF1E88E5).hashCode()
                    valueTextColor = Color.Black.hashCode()
                    valueTextSize = 9f
                    lineWidth = 2f
                    // 设置圆点颜色和大小
                    setCircleColor(Color(0xFF1E88E5).hashCode())
                    circleHoleColor = Color.White.hashCode()
                    circleRadius = 4f

                    // 如果需要强调最新数据点，使用不同的方法
                    if (entries.isNotEmpty()) {
                        // 使用其他方式突出显示最后一个点，例如增加数据点大小
                        circleRadius = 5f

                        // 设置单独的颜色数组，每个点一个颜色
                        val colors = ArrayList<Int>()
                        for (i in entries.indices) {
                            if (i == entries.size - 1) {
                                // 最后一个点使用突出颜色
                                colors.add(Color(0xFFFF5722).hashCode())
                            } else {
                                // 其他点使用普通颜色
                                colors.add(Color(0xFF1E88E5).hashCode())
                            }
                        }
                        circleColors = colors
                    }
                }
                val lineData = LineData(dataSet)

                // 获取排序后的日期标签（缩短日期格式，只显示日和月）
                val formattedDates = sortedDatePairs.map { (dateStr, _) ->
                    try {
                        val originalFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        val date = originalFormat.parse(dateStr)
                        val newFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                        date?.let { newFormat.format(it) } ?: dateStr
                    } catch (e: Exception) {
                        dateStr
                    }
                }

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

                            // 确保显示所有数据
                            setVisibleXRangeMaximum(entries.size.toFloat())

                            // 高亮显示最新的数据点
                            if (entries.isNotEmpty()) {
                                highlightValue(entries.size - 1f, 0, false)
                            }

                            invalidate()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                )
            }
        }
    }
}

// 日期糖分项目
// 日期糖分项目
@Composable
fun DailySugarItem(
    date: String,
    sugars: Double,
    viewModel: DataViewModel // 添加 ViewModel 参数
) {
    // 获取提示信息
    val mood = viewModel.getMoodForDate(date)
    val isExceeding = viewModel.isSugarExceedingLimit(date)
    val tipMessage = viewModel.getTipMessageForMoodAndSugar(date)

    // 提示按钮颜色
    val tipButtonColor = if (isExceeding) Color(0xFFE57373) else Color(0xFF4CAF50)

    // 显示提示的状态
    var showTip by remember { mutableStateOf(false) }

    // 格式化日期显示
    val formattedDate = try {
        if (!date.matches(Regex("\\d{2}-\\d{2}-\\d{4}"))) {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val targetFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val parsedDate = originalFormat.parse(date)
            parsedDate?.let { targetFormat.format(it) } ?: date
        } else {
            date
        }
    } catch (e: Exception) {
        date
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Box {
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${sugars.toInt()} g",
                        color = Color(0xFFFFB74D),
                        fontWeight = FontWeight.Bold
                    )

                    // 提示按钮
                    IconButton(
                        onClick = { showTip = !showTip },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Tip",
                            tint = tipButtonColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 提示浮窗
            if (showTip) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, end = 12.dp)
                        .width(200.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF333333)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // 显示情绪状态
                        if (mood != "No Data") {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                val moodIcon = when (mood) {
                                    "Good" -> Icons.Filled.SentimentSatisfied
                                    "Regular" -> Icons.Filled.SentimentNeutral
                                    "Bad" -> Icons.Filled.SentimentDissatisfied
                                    else -> Icons.Filled.SentimentNeutral
                                }

                                Icon(
                                    imageVector = moodIcon,
                                    contentDescription = "Mood",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = "Mood: $mood",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // 显示提示信息
                        Text(
                            text = tipMessage,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
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

    // 格式化日期显示（确保是 dd-MM-yyyy 格式）
    val formattedDate = try {
        // 尝试解析日期格式
        if (date.matches(Regex("\\d{2}-\\d{2}-\\d{4}"))) {
            // 已经是正确的格式，直接使用
            date
        } else if (date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            // 是 yyyy-MM-dd 格式，需要转换
            val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val targetFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val parsedDate = originalFormat.parse(date)
            parsedDate?.let { targetFormat.format(it) } ?: date
        } else {
            // 其他不识别的格式，直接使用
            date
        }
    } catch (e: Exception) {
        // 转换失败时使用原始日期
        date
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
                Text(text = formattedDate, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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