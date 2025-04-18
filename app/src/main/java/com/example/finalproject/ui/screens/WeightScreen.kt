package com.example.finalproject.ui.screens

import android.app.DatePickerDialog
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.example.finalproject.R
import com.example.finalproject.ui.components.BottomNavigationBar
import com.example.finalproject.viewmodel.UserInfoViewModel
import com.example.finalproject.viewmodel.WeightViewModel
import com.example.finalproject.viewmodel.WeightEntry
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(
    userId: String,
    viewModel: WeightViewModel,
    userInfoViewModel: UserInfoViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToData: () -> Unit,
    onNavigateToPersonal: () -> Unit
) {
    val weightState by viewModel.weightState.collectAsState()
    val bmiResult by viewModel.bmiResult.collectAsState()
    var showWeightDialog by remember { mutableStateOf(false) }
    var showBMIDialog by remember { mutableStateOf(false) }
    var showTargetWeightDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDateString = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                selectedDate = selectedDateString
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
    }

    val profileImageUrl by userInfoViewModel.profileImageUrl.collectAsState()

    // ✅ 加载当前用户的体重数据
    LaunchedEffect(userId) {
        viewModel.fetchWeightEntries(userId)
        viewModel.fetchBMI(userId)
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "weight",
                onNavigate = { route ->
                    when (route) {
                        "home" -> onNavigateToHome()
                        "data" -> onNavigateToData()
                        "personal" -> onNavigateToPersonal()
                    }
                }
            )
        }
    ) { innerPadding ->
        // 使用可滚动的列来包含内容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
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
                IconButton(
                    onClick = { showWeightDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF00BFA5), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Weight",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Weight Data",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            WeightLineChart(weightState.weightEntries)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${weightState.targetWeight} kg",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            showTargetWeightDialog = true
                        }
                    )
                    Text(
                        text = "Target Weight",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    LinearProgressIndicator(
                        progress = 0.60f,
                        modifier = Modifier.width(100.dp),
                        color = Color(0xFFFF9800)
                    )
                }
                Column {
                    Text(
                        text = "${weightState.currentWeight} kg",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Current Weight",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    LinearProgressIndicator(
                        progress = 0.70f,
                        modifier = Modifier.width(100.dp),
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showBMIDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFAFE1AF)
                )
            ) {
                Text("Calculate BMI")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_scale),
                                contentDescription = "BMI",
                                tint = Color(0xFF00BFA5)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BMI Score",
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = String.format("%.1f", bmiResult),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = 0.4f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF00BFA5)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("15", color = Color.Gray, fontSize = 12.sp)
                        Text("25", color = Color.Gray, fontSize = 12.sp)
                        Text("35", color = Color.Gray, fontSize = 12.sp)
                        Text("40", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BMI解释卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                var showBMIInfoDialog by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "BMI Interpretation",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // 根据BMI值显示不同的解释
                        val (interpretationText, interpretationColor) = when {
                            bmiResult < 18.5 -> Pair("Underweight", Color(0xFF64B5F6))
                            bmiResult < 25 -> Pair("Normal weight", Color(0xFF4CAF50))
                            bmiResult < 30 -> Pair("Overweight", Color(0xFFFFA726))
                            else -> Pair("Obesity", Color(0xFFE57373))
                        }

                        Text(
                            text = interpretationText,
                            color = interpretationColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    IconButton(
                        onClick = { showBMIInfoDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "BMI Info",
                            tint = Color(0xFF00BFA5)
                        )
                    }
                }

                // BMI信息对话框
                if (showBMIInfoDialog) {
                    Dialog(onDismissRequest = { showBMIInfoDialog = false }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "BMI Categories",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // BMI分类表
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    BMICategoryRow(
                                        category = "Underweight",
                                        range = "Below 18.5",
                                        color = Color(0xFF64B5F6)
                                    )

                                    BMICategoryRow(
                                        category = "Normal weight",
                                        range = "18.5–24.9",
                                        color = Color(0xFF4CAF50)
                                    )

                                    BMICategoryRow(
                                        category = "Overweight",
                                        range = "25.0–29.9",
                                        color = Color(0xFFFFA726)
                                    )

                                    BMICategoryRow(
                                        category = "Obesity (Class I)",
                                        range = "30.0–34.9",
                                        color = Color(0xFFE57373)
                                    )

                                    BMICategoryRow(
                                        category = "Obesity (Class II)",
                                        range = "35.0–39.9",
                                        color = Color(0xFFEF5350)
                                    )

                                    BMICategoryRow(
                                        category = "Extreme Obesity (Class III)",
                                        range = "40.0 and above",
                                        color = Color(0xFFD32F2F)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Note: BMI is a screening tool, but it does not diagnose body fatness or health. A healthcare provider can help you interpret your BMI results.",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                TextButton(
                                    onClick = { showBMIInfoDialog = false },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Close")
                                }
                            }
                        }
                    }
                }
            }

            // 添加底部间距，确保内容不会被底部导航栏挡住
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showWeightDialog) {
            Dialog(onDismissRequest = { showWeightDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Add Weight Entry", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = { datePickerDialog.show() }) {
                            Text(selectedDate.ifEmpty { "Select Date" })
                        }

                        TextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            label = { Text("Weight (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val weight = weightInput.toDoubleOrNull()
                                if (selectedDate.isNotEmpty() && weight != null) {
                                    viewModel.addWeightEntry(userId, selectedDate, weight) // ✅ 传递 userId
                                    showWeightDialog = false
                                    weightInput = ""
                                    selectedDate = ""
                                } else {
                                    Toast.makeText(context, "Please enter a valid weight", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }

        if (showBMIDialog) {
            Dialog(onDismissRequest = { showBMIDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Calculate BMI", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = heightInput,
                            onValueChange = { heightInput = it },
                            label = { Text("Height (m)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val height = heightInput.toDoubleOrNull()
                                if (height != null) {
                                    viewModel.setHeight(userId, height) // ✅ 传递 userId
                                    viewModel.calculateBMI(userId)
                                    showBMIDialog = false
                                    heightInput = ""
                                } else {
                                    Toast.makeText(context, "Please enter a valid height", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("Calculate")
                        }
                    }
                }
            }
        }

        if (showTargetWeightDialog) {
            Dialog(onDismissRequest = { showTargetWeightDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Set Target Weight", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            label = { Text("Target Weight (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val targetWeight = weightInput.toDoubleOrNull()
                                if (targetWeight != null) {
                                    viewModel.setTargetWeight(userId, targetWeight) // ✅ 传递 userId
                                    showTargetWeightDialog = false
                                    weightInput = ""
                                } else {
                                    Toast.makeText(context, "Please enter a valid target weight", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("Save")
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun BMICategoryRow(category: String, range: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = category,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )

            Text(
                text = "BMI: $range",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun WeightLineChart(weightEntries: List<WeightEntry>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        factory = { context ->
            LineChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                setDrawGridBackground(false)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    labelRotationAngle = 30f // 旋转标签以便更好地显示日期
                    granularity = 1f // 确保每个标签之间的最小间隔
                }

                axisLeft.setDrawGridLines(true)
                axisRight.isEnabled = false

                // 设置动画
                animateX(1000)
            }
        },
        update = { chart ->
            // 将日期解析为实际的日期对象，以便正确排序
            val parsedEntries = weightEntries.map { entry ->
                val parsedDate = try {
                    // 尝试解析不同格式的日期
                    if (entry.date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                        // yyyy-MM-dd格式
                        val parts = entry.date.split("-")
                        val year = parts[0].toInt()
                        val month = parts[1].toInt() - 1  // 月份从0开始
                        val day = parts[2].toInt()
                        val calendar = Calendar.getInstance()
                        calendar.set(year, month, day)
                        calendar.timeInMillis
                    } else if (entry.date.matches(Regex("\\d{2}-\\d{2}-\\d{4}"))) {
                        // dd-MM-yyyy格式
                        val parts = entry.date.split("-")
                        val day = parts[0].toInt()
                        val month = parts[1].toInt() - 1  // 月份从0开始
                        val year = parts[2].toInt()
                        val calendar = Calendar.getInstance()
                        calendar.set(year, month, day)
                        calendar.timeInMillis
                    } else {
                        // 使用时间戳作为备选排序方式
                        entry.timestamp
                    }
                } catch (e: Exception) {
                    // 解析失败时使用时间戳
                    entry.timestamp
                }
                Triple(entry.date, entry.weight, parsedDate)
            }

            // 按照日期从过去到现在的顺序排序
            val sortedEntries = parsedEntries.sortedBy { it.third }

            // 提取排序后的格式化日期和体重
            val dates = sortedEntries.map { it.first }
            val weights = sortedEntries.map { it.second }

            // 为图表创建数据点
            val chartEntries = weights.mapIndexed { index, weight ->
                Entry(index.toFloat(), weight.toFloat())
            }

            // 将日期格式化为显示格式
            val formattedDates = dates.map { dateStr ->
                try {
                    // 统一转换为DD-MM-YYYY格式
                    if (dateStr.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                        // 从yyyy-MM-dd转换为dd-MM-yyyy
                        val parts = dateStr.split("-")
                        "${parts[2]}-${parts[1]}-${parts[0]}"
                    } else {
                        // 已经是dd-MM-yyyy或其他格式
                        dateStr
                    }
                } catch (e: Exception) {
                    dateStr
                }
            }

            val dataSet = LineDataSet(chartEntries, "Weight").apply {
                color = android.graphics.Color.rgb(0, 191, 165)  // 主题色 #00BFA5
                setCircleColor(android.graphics.Color.rgb(0, 191, 165))
                lineWidth = 2f
                circleRadius = 4f
                setDrawValues(true)
                valueTextSize = 9f
            }

            // 设置X轴日期标签
            chart.xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(formattedDates)

            val lineData = LineData(dataSet)
            chart.data = lineData
            chart.invalidate()
        }
    )
}