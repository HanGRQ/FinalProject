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
fun UserHeightScreen(
    viewModel: UserInfoViewModel,
    onNext: () -> Unit
) {
    var currentHeight by remember { mutableStateOf(175f) }
    var isDragging by remember { mutableStateOf(false) }

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
            Text("4 / 6", color = Color.Gray)
        }

        // Main content
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
                        append("Height")
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
            Button(
                onClick = { /* Toggle unit */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B3434)
                ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("cm")
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Height slider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = { isDragging = false },
                            onDragCancel = { isDragging = false },
                            onHorizontalDrag = { change, dragAmount ->
                                val newHeight = currentHeight + (dragAmount / 10f)
                                when {
                                    newHeight < 50f -> currentHeight = 50f
                                    newHeight > 250f -> currentHeight = 250f
                                    else -> currentHeight = newHeight
                                }
                            }
                        )
                    }
            ) {
                // Display height values
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (offset in -1..1) {
                        val height = (currentHeight + offset).roundToInt()
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .background(
                                    if (offset == 0) Color(0xFFE8F5E9)
                                    else Color(0xFFF5F5F5),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = height.toString(),
                                fontSize = if (offset == 0) 24.sp else 20.sp,
                                fontWeight = if (offset == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Ruler marks
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    // Add ruler marks here
                }
            }
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
                    viewModel.updateUserHeight(currentHeight)
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