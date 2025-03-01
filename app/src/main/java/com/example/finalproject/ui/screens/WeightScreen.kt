package com.example.finalproject.ui.screens

import android.app.DatePickerDialog
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.finalproject.R
import com.example.finalproject.ui.components.BottomNavigationBar
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
    viewModel: WeightViewModel,
    onNavigateTo: (String) -> Unit
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

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "weight",
                onNavigate = onNavigateTo
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_image),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
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
                                    viewModel.addWeightEntry(selectedDate, weight)
                                    showWeightDialog = false
                                    weightInput = ""
                                    selectedDate = ""
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
                                    viewModel.setHeight(height)
                                    viewModel.calculateBMI()
                                    showBMIDialog = false
                                    heightInput = ""
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
                                    viewModel.setTargetWeight(targetWeight)
                                    showTargetWeightDialog = false
                                    weightInput = ""
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
                }

                axisLeft.setDrawGridLines(true)
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val entries = weightEntries.mapIndexed { index, entry ->
                Entry(index.toFloat(), entry.weight.toFloat())
            }

            val dataSet = LineDataSet(entries, "Weight").apply {
                color = android.graphics.Color.BLUE
                setCircleColor(android.graphics.Color.BLUE)
                lineWidth = 2f
                circleRadius = 4f
                setDrawValues(true)
            }

            val lineData = LineData(dataSet)
            chart.data = lineData
            chart.invalidate()
        }
    )
}