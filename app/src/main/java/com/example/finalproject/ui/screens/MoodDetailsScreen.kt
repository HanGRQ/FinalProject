package com.example.finalproject.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.R
import com.example.finalproject.viewmodel.EmotionViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodDetailsScreen(
    userId: String,  // ✅ 传递 userId
    viewModel: EmotionViewModel,
    onNavigateTo: (String) -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val emotionStatus by viewModel.moodStatus.collectAsState()
    val allEmotions by viewModel.allEmotions.collectAsState()

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.fetchAllEmotions(userId) // ✅ 获取用户情绪历史
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mood Details") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateTo("back") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Log.d("MoodDetailsScreen", "Add button clicked")
                        showDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color(0xFF4CAF50)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.emotion_illustration),
                contentDescription = "Emotion Illustration",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Emotion Data", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = selectedDate ?: "No Date", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            val moodIcon = when (emotionStatus) {
                "Good" -> R.drawable.ic_mood_good
                "Regular" -> R.drawable.ic_mood_neutral
                "Bad" -> R.drawable.ic_mood_bad
                else -> R.drawable.ic_mood_neutral
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = moodIcon),
                        contentDescription = "Mood",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = emotionStatus ?: "No Data", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Emotion History", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            if (allEmotions.isEmpty()) {
                Text(text = "No emotion records available", fontSize = 14.sp, color = Color.Gray)
            } else {
                LazyColumn {
                    items(allEmotions) { emotion ->
                        EmotionHistoryItem(date = emotion.date, mood = emotion.mood)
                    }
                }
            }
        }
    }

    if (showDialog) {
        EmotionDialog(
            userId = userId,  // ✅ 传递 userId
            context = context,
            selectedDate = selectedDate,
            onDateSelected = { viewModel.updateDate(userId, it) },
            onMoodSelected = { mood -> viewModel.saveEmotionStatus(userId, selectedDate, mood) },
            onDismiss = { showDialog = false }
        )
    }
}




// **更新历史记录 Card，使 Icon 更小**
@Composable
fun EmotionHistoryItem(date: String, mood: String) {
    val moodIcon = when (mood) {
        "Good" -> R.drawable.ic_mood_good
        "Regular" -> R.drawable.ic_mood_neutral
        "Bad" -> R.drawable.ic_mood_bad
        else -> R.drawable.ic_mood_neutral
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = moodIcon),
                contentDescription = "Mood",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = date, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = mood, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun EmotionDialog(
    userId: String,
    context: Context,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onMoodSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMood by remember { mutableStateOf("Good") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Emotion Status") },
        text = {
            Column {
                TextButton(onClick = { showDatePicker(context, onDateSelected) }) {
                    Text("Select Date: $selectedDate")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Select Mood:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Good", "Regular", "Bad").forEach { mood ->
                        Button(
                            onClick = { selectedMood = mood },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedMood == mood) Color(0xFF4CAF50) else Color.LightGray
                            )
                        ) {
                            Text(text = mood)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onMoodSelected(selectedMood); onDismiss() }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = "$year-${month + 1}-$dayOfMonth" // 确保格式正确
            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.show()
}

