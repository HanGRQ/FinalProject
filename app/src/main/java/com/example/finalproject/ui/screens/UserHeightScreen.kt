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
fun UserHeightScreen(onNext: () -> Unit, viewModel: UserInfoViewModel) {
    val height = remember { mutableStateOf(viewModel.userHeight.toFloat()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "选择您的身高 (cm)", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = height.value,
            onValueChange = {
                height.value = it
            },
            valueRange = 140f..220f,
            steps = 80,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Text(text = "${height.value.toInt()} cm", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.userHeight = height.value.toInt()
            onNext()
        }) {
            Text("下一步")
        }
    }
}
