package com.example.finalproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.R
import com.example.finalproject.viewmodel.UserInfoViewModel
import kotlin.math.roundToInt


@Composable
fun TargetWeightScreen(
    viewModel: UserInfoViewModel,
    onNext: () -> Unit
) {
    var targetWeight by remember { mutableStateOf(72f) }
    var unit by remember { mutableStateOf("kg") }
    val currentWeight = 60f // Get this from viewModel

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { /* Handle back */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back"
                )
            }
            Text("Skip", color = Color.Gray)
        }

        // Progress indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("6 / 6", color = Color.Gray)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = buildAnnotatedString {
                    append("Choose Your ")
                    withStyle(SpanStyle(color = Color(0xFF00BFA5))) {
                        append("Target Weight")
                    }
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "We will use this data\nto provide a better diet plan for you",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Unit selector
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1B3434))
                    .padding(4.dp)
            ) {
                listOf("kg", "lb").forEach { unitOption ->
                    Button(
                        onClick = { /* unit = unitOption */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (unit == unitOption) Color(0xFF1B3434) else Color.Transparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(unitOption)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Weight displays
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WeightDisplay(
                    weight = currentWeight,
                    isHighlighted = false
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_forward),
                    contentDescription = "To",
                    tint = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                WeightDisplay(
                    weight = targetWeight,
                    isHighlighted = true
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Ruler
            RulerSelector(
                value = targetWeight,
                onValueChange = { targetWeight = it },
                range = currentWeight..currentWeight + 20f
            )
        }

        // Bottom next button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    viewModel.updateTargetWeight(targetWeight)
                    onNext()
                },
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B3434)
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_forward),
                    contentDescription = "Next",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun WeightScreenBase(
    title: String,
    progress: String,
    currentValue: Float,
    onValueChange: (Float) -> Unit,
    unit: String,
    onUnitChange: (String) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { /* Handle back */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back"
                )
            }
            Text("Skip", color = Color.Gray)
        }

        // Progress indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(progress, color = Color.Gray)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = buildAnnotatedString {
                    append("Choose Your ")
                    withStyle(SpanStyle(color = Color(0xFF00BFA5))) {
                        append(title)
                    }
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "We will use this data\nto provide a better diet plan for you",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Unit selector
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1B3434))
                    .padding(4.dp)
            ) {
                listOf("kg", "lb").forEach { unitOption ->
                    Button(
                        onClick = { onUnitChange(unitOption) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (unit == unitOption) Color(0xFF1B3434) else Color.Transparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(unitOption)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Weight display
            WeightDisplay(
                weight = currentValue,
                isHighlighted = true
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Ruler
            RulerSelector(
                value = currentValue,
                onValueChange = onValueChange,
                range = valueRange
            )
        }

        // Bottom next button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onNext,
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B3434)
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_forward),
                    contentDescription = "Next",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun WeightDisplay(
    weight: Float,
    isHighlighted: Boolean
) {
    Box(
        modifier = Modifier
            .background(
                if (isHighlighted) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 32.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = weight.roundToInt().toString(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RulerSelector(
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>
) {
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onHorizontalDrag = { change, dragAmount ->
                        val newValue = value + (dragAmount / 10f)
                        when {
                            newValue < range.start -> onValueChange(range.start)
                            newValue > range.endInclusive -> onValueChange(range.endInclusive)
                            else -> onValueChange(newValue)
                        }
                    }
                )
            }
    ) {
        // Ruler marks implementation
    }
}