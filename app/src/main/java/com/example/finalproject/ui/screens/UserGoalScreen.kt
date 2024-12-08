package com.example.finalproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "选择你的目标",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 目标选项
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GoalButton(
                text = "增重",
                isSelected = selectedGoal == "增重",
                onClick = {
                    selectedGoal = "增重"
                    viewModel.updateUserGoal("增重")
                }
            )

            GoalButton(
                text = "减重",
                isSelected = selectedGoal == "减重",
                onClick = {
                    selectedGoal = "减重"
                    viewModel.updateUserGoal("减重")
                }
            )

            GoalButton(
                text = "保持体重",
                isSelected = selectedGoal == "保持体重",
                onClick = {
                    selectedGoal = "保持体重"
                    viewModel.updateUserGoal("保持体重")
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = selectedGoal.isNotEmpty()
        ) {
            Text("下一步")
        }
    }
}

@Composable
private fun GoalButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}