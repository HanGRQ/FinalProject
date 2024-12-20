package com.example.finalproject.ui.screens

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserNameScreen(
    viewModel: UserInfoViewModel,
    onNext: () -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top navigation section
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
            Text("1 / 6", color = Color.Gray)
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
                    append("Enter Your ")
                    withStyle(SpanStyle(color = Color(0xFF00BFA5))) {
                        append("Username")
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

            Spacer(modifier = Modifier.height(40.dp))

            // Custom text field
            OutlinedTextField(
                value = nameInput,
                onValueChange = {
                    nameInput = it
                    showError = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFFF5F5F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF00BFA5)
                ),
                placeholder = { Text("Hello World") },
                singleLine = true,
                isError = showError
            )

            if (showError) {
                Text(
                    text = "Please enter a valid name (1-20 characters)",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
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
                onClick = {
                    if (nameInput.trim().length in 1..20) {
                        viewModel.updateUserName(nameInput.trim())
                        onNext()
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B3434)
                ),
                enabled = nameInput.isNotEmpty()
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