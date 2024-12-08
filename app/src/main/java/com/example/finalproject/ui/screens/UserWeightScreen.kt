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
fun UserWeightScreen(onNext: () -> Unit, viewModel: UserInfoViewModel) {
    val weight = remember { mutableStateOf(viewModel.userWeight.toFloat()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "选择您的体重 (kg)", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = weight.value,
            onValueChange = {
                weight.value = it
            },
            valueRange = 30f..150f,
            steps = 120,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Text(text = "${weight.value.toInt()} kg", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.userWeight = weight.value.toInt()
            onNext()
        }) {
            Text("下一步")
        }
    }
}
