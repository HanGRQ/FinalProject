package com.example.finalproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.viewmodel.UserInfoViewModel

@Composable
fun UserGenderScreen(onNext: () -> Unit, viewModel: UserInfoViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "选择您的性别", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.LightGray)
                    .clickable {
                        viewModel.userGender = "男"
                        onNext()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "男士", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.LightGray)
                    .clickable {
                        viewModel.userGender = "女"
                        onNext()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "女士", fontSize = 20.sp)
            }
        }
    }
}
