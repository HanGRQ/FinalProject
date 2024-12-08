package com.example.finalproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.R

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        // 返回按钮
        IconButton(onClick = { /*返回逻辑*/ }) {
            Icon(painter = painterResource(R.drawable.ic_back), contentDescription = null)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 欢迎标题
        Text(
            text = "欢迎回来",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "快来登录，一起健康生活吧",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Email 输入框
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("输入Email账号") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 密码输入框
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("输入密码") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 登录按钮
        Button(
            onClick = { onLoginSuccess() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40))
        ) {
            Text(text = "立即登录", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 忘记密码
        Text(
            text = "忘记密码?",
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
