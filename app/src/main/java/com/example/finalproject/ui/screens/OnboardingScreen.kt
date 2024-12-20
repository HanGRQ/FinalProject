package com.example.finalproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.R

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }

    val pages = listOf(
        OnboardingPage(R.drawable.intro_1, "Know What to Eat", "Understand your nutrition habits\nDetailed statistics"),
        OnboardingPage(R.drawable.intro_2, "Track Your Diet Data", "We'll help you lose weight\nMaintain shape or build muscle"),
        OnboardingPage(R.drawable.intro_3, "Get Healthy Living Rewards", "Let's start this journey\nLive healthy together")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                if (currentPage < pages.size - 1) {
                    currentPage++
                } else {
                    onFinish()
                }
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = pages[currentPage].imageRes),
                contentDescription = null,
                modifier = Modifier.size(240.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = pages[currentPage].title,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = pages[currentPage].description,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String
)