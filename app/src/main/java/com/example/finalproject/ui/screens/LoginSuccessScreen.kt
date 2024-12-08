// LoginSuccessScreen.kt
package com.example.finalproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginSuccessScreen(onNavigateToSetup: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "登录成功",
                fontSize = 32.sp,
                color = Color(0xFF004D40)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onNavigateToSetup,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40))
            ) {
                Text(text = "Set Up Plan", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}
