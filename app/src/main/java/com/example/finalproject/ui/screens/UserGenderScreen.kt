package com.example.finalproject.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.R
import com.example.finalproject.viewmodel.UserInfoViewModel

@Composable
fun UserGenderScreen(
    viewModel: UserInfoViewModel,
    onNext: () -> Unit
) {
    var selectedGender by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { /* Handle back */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back"
                )
            }
            Text("Skip", color = Color.Gray)
        }

        // Progress indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("3 / 6", color = Color.Gray)
        }

        // Main content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = buildAnnotatedString {
                    append("Choose Your ")
                    withStyle(SpanStyle(color = Color(0xFF00BFA5))) {
                        append("Gender")
                    }
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "We will use this data\nto provide a better diet plan for you",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Gender selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GenderButton(
                    text = "Male",
                    icon = R.drawable.ic_male,
                    isSelected = selectedGender == "Male",
                    onClick = {
                        selectedGender = "Male"
                        viewModel.updateUserGender("Male")
                    }
                )

                GenderButton(
                    text = "Female",
                    icon = R.drawable.ic_female,
                    isSelected = selectedGender == "Female",
                    onClick = {
                        selectedGender = "Female"
                        viewModel.updateUserGender("Female")
                    }
                )
            }
        }

        // Bottom next button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onNext,
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B3434)
                ),
                enabled = selectedGender.isNotEmpty()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_forward),
                    contentDescription = "Next",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun GenderButton(
    text: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
            contentColor = Color.Black
        ),
        elevation = null
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = text)
        }
    }
}