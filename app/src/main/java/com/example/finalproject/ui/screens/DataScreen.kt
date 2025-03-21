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
                        painter = rememberImagePainter(
                            data = profileImageUrl ?: R.drawable.profile_image,
                            builder = { crossfade(true) }
                        ),
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
                item { LineChartView(dietFoodsList) }

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
                items(scannedFoodsList) { food ->
                    HistoryItem(name = food.product_name, energy = food.energy_kcal, sugars = food.sugars)
                }
            } else if (selectedTab == 1) {
                items(emotionData.entries.toList()) { (date, mood) ->
                    EmotionHistoryItemData(date = date, mood = mood)
                }
            }
        }
    }
}



// **Sugar Analysis 折线图**
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
