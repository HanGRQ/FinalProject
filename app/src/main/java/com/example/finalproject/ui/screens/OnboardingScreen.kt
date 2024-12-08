package com.example.finalproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.R

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }

    val pages = listOf(
        OnboardingPage(R.drawable.intro_1, "知道你应该吃什么", "了解您的营养习惯\n详细统计数据"),
        OnboardingPage(R.drawable.intro_2, "查看你的饮食数据", "我们将帮助您减肥\n保持身材或锻炼肌肉"),
        OnboardingPage(R.drawable.intro_3, "健康生活获得荣誉", "让我们开始这段旅程\n一起健康生活")
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = pages[currentPage].imageRes),
            contentDescription = null,
            modifier = Modifier.size(240.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = pages[currentPage].title, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = pages[currentPage].description)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (currentPage < pages.size - 1) {
                    currentPage++
                } else {
                    onFinish() // 进入主页面
                }
            }
        ) {
            Text(text = if (currentPage == pages.size - 1) "开始" else "下一页")
        }
    }
}

data class OnboardingPage(val imageRes: Int, val title: String, val description: String)
