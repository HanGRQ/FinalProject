package com.example.finalproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.viewmodel.UserInfoViewModel

@Composable
fun TargetWeightScreen(onNext: () -> Unit, viewModel: UserInfoViewModel) {
    val targetWeight = remember { mutableStateOf(viewModel.targetWeight.toFloat()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "选择您的目标体重 (kg)", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = targetWeight.value,
            onValueChange = {
                targetWeight.value = it
            },
            valueRange = 30f..150f,
            steps = 120,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Text(text = "${targetWeight.value.toInt()} kg", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.targetWeight = targetWeight.value.toInt()
            onNext()
        }) {
            Text("完成")
        }
    }
}
