package com.example.finalproject.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.R

@Composable
fun LoginSuccessScreen(onNavigateToSetup: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated success checkmark
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp)
                ) {
                    CircleWithCheckmark(
                        circleColor = Color(0xFF00BFA5),
                        checkmarkColor = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Login Success",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Click to set up your plan",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onNavigateToSetup,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF00BFA5)
                        )
                    ) {
                        Text("Go to set up plan")
                        Icon(
                            painter = painterResource(id = R.drawable.ic_forward),
                            contentDescription = "Forward",
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CircleWithCheckmark(
    circleColor: Color,
    checkmarkColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw outer circle
        drawCircle(
            color = circleColor,
            radius = size.minDimension / 2,
            center = Offset(size.width / 2, size.height / 2)
        )

        // Draw checkmark
        val checkmarkPath = Path().apply {
            moveTo(size.width * 0.3f, size.height * 0.5f)
            lineTo(size.width * 0.45f, size.height * 0.65f)
            lineTo(size.width * 0.7f, size.height * 0.35f)
        }

        drawPath(
            path = checkmarkPath,
            color = checkmarkColor,
            style = Stroke(
                width = size.minDimension * 0.08f,
                cap = StrokeCap.Round
            )
        )
    }
}