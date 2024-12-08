package com.example.finalproject.ui.screens

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.viewmodel.UserInfoViewModel

@Composable
fun UserNameScreen(onNext: () -> Unit, viewModel: UserInfoViewModel) {
    val userName = remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "输入您的用户名", fontSize = 24.sp)
        OutlinedTextField(
            value = userName.value,
            onValueChange = {
                userName.value = it
                viewModel.userName = it
            },
            label = { Text("用户名") }
        )
        Button(onClick = onNext) {
            Text("下一步")
        }
    }
}
