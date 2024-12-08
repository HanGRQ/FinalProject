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
fun UserHeightScreen(
    viewModel: UserInfoViewModel,
    onNext: () -> Unit
) {
    var heightInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "输入你的身高",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "请输入身高（厘米）",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = heightInput,
            onValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d{0,3}$"))) {
                    heightInput = it
                    showError = false
                }
            },
            label = { Text("身高") },
            suffix = { Text("cm") },
            singleLine = true,
            isError = showError,
            supportingText = if (showError) {
                { Text("请输入有效的身高（50-250厘米）") }
            } else null
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val height = heightInput.toFloatOrNull()
                if (height != null && height in 50f..250f) {
                    viewModel.updateUserHeight(height)
                    onNext()
                } else {
                    showError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = heightInput.isNotEmpty()
        ) {
            Text("下一步")
        }
    }
}