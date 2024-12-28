package com.example.finalproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.R
import com.example.finalproject.viewmodel.UserInfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserGoalScreen(
    viewModel: UserInfoViewModel,
    onNext: () -> Unit
) {
    var selectedGoal by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top navigation section
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
            Text("2 / 6", color = Color.Gray)
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
                        append("Goal")
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

            // Goal options
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GoalButton(
                    text = "Gain Weight",
                    icon = R.drawable.ic_banana,
                    isSelected = selectedGoal == "Gain Weight",
                    onClick = {
                        selectedGoal = "Gain Weight"
                        viewModel.updateUserGoal("Gain Weight")
                    }
                )

                GoalButton(
                    text = "Lose Weight",
                    icon = R.drawable.ic_dumbbell,
                    isSelected = selectedGoal == "Lose Weight",
                    onClick = {
                        selectedGoal = "Lose Weight"
                        viewModel.updateUserGoal("Lose Weight")
                    }
                )

                GoalButton(
                    text = "Stay Healthy",
                    icon = R.drawable.ic_trophy,
                    isSelected = selectedGoal == "Stay Healthy",
                    onClick = {
                        selectedGoal = "Stay Healthy"
                        viewModel.updateUserGoal("Stay Healthy")
                    }
                )
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
                onClick = onNext,
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B3434)
                ),
                enabled = selectedGoal.isNotEmpty()
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
private fun GoalButton(
    text: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
            contentColor = Color.Black
        ),
        elevation = null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text)
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}