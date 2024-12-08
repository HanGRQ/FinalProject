package com.example.finalproject.ui.screens

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finalproject.viewmodel.UserInfoViewModel

@Composable
fun UserGoalScreen(onNext: () -> Unit, viewModel: UserInfoViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "选择您的目标")
        Button(onClick = {
            viewModel.userGoal = "减肥"
            onNext()
        }) {
            Text("我要减肥")
        }
        Button(onClick = {
            viewModel.userGoal = "增重"
            onNext()
        }) {
            Text("我要增重")
        }
    }
}
