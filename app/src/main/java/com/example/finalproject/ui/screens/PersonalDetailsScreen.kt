package com.example.finalproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.finalproject.R
import com.example.finalproject.viewmodel.UserInfoViewModel
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDetailsScreen(
    userId: String,
    viewModel: UserInfoViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // 使用viewModel获取用户信息
    val userEmail by viewModel.userEmail.collectAsState()

    // 从ViewModel加载用户设置，如果为空则使用默认值
    val userAge by viewModel.userAge.collectAsState()
    val userGender by viewModel.userGender.collectAsState()
    val userPlan by viewModel.userPlan.collectAsState()

    // 本地状态用于管理对话框的显示
    var showAgeDialog by remember { mutableStateOf(false) }
    var showGenderDialog by remember { mutableStateOf(false) }
    var showPlanDialog by remember { mutableStateOf(false) }

    // 临时存储编辑值
    var tempAge by remember { mutableStateOf(userAge.ifEmpty { "" }) }
    var tempGender by remember { mutableStateOf(userGender.ifEmpty { "" }) }
    var tempPlan by remember { mutableStateOf(userPlan.ifEmpty { "" }) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Profile Section
            ListItem(
                headlineContent = { Text(userEmail.ifEmpty { "Unknown Email" }) },
                supportingContent = { Text(userEmail.ifEmpty { "No email" }) },
                leadingContent = {
                    Image(
                        painter = painterResource(id = R.drawable.profile_image),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                },
                modifier = Modifier
                    .background(Color(0xFFF0F9F0))
                    .clickable { /* Handle edit profile click */ }
            )

            // Personal Information
            ListItem(
                headlineContent = { Text("Age") },
                supportingContent = { Text(userAge.ifEmpty { "Not specified" }) },
                trailingContent = {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Age",
                        modifier = Modifier.clickable { showAgeDialog = true }
                    )
                }
            )

            ListItem(
                headlineContent = { Text("Gender") },
                supportingContent = { Text(userGender.ifEmpty { "Not specified" }) },
                trailingContent = {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Gender",
                        modifier = Modifier.clickable { showGenderDialog = true }
                    )
                }
            )

            ListItem(
                headlineContent = { Text("My Plan") },
                supportingContent = { Text(userPlan.ifEmpty { "Not specified" }) },
                trailingContent = {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Plan",
                        modifier = Modifier.clickable { showPlanDialog = true }
                    )
                }
            )
        }
    }

    // Age Dialog
    if (showAgeDialog) {
        Dialog(onDismissRequest = { showAgeDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Enter Your Age", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = tempAge,
                        onValueChange = {
                            // 只允许输入数字
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                tempAge = it
                            }
                        },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAgeDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (tempAge.isNotEmpty()) {
                                    saveUserSettingToFirestore(userId, "age", tempAge) { success ->
                                        if (success) {
                                            viewModel.setUserAge(tempAge)
                                            Toast.makeText(context, "Age updated successfully", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Failed to update age", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                showAgeDialog = false
                            }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }

    // Gender Dialog
    if (showGenderDialog) {
        val genderOptions = listOf("Male", "Female", "Other")

        Dialog(onDismissRequest = { showGenderDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Select Your Gender", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))

                    genderOptions.forEach { gender ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    tempGender = gender
                                    saveUserSettingToFirestore(userId, "gender", gender) { success ->
                                        if (success) {
                                            viewModel.setUserGender(gender)
                                            Toast.makeText(context, "Gender updated successfully", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Failed to update gender", Toast.LENGTH_SHORT).show()
                                        }
                                        showGenderDialog = false
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = gender == userGender,
                                onClick = {
                                    tempGender = gender
                                    saveUserSettingToFirestore(userId, "gender", gender) { success ->
                                        if (success) {
                                            viewModel.setUserGender(gender)
                                            Toast.makeText(context, "Gender updated successfully", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Failed to update gender", Toast.LENGTH_SHORT).show()
                                        }
                                        showGenderDialog = false
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(gender)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { showGenderDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        }
    }

    // Plan Dialog - 只显示英文，移除了中文翻译
    if (showPlanDialog) {
        val planOptions = listOf("Weight Gain", "Weight Loss", "Healthy")

        Dialog(onDismissRequest = { showPlanDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Select Your Plan", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))

                    planOptions.forEach { plan ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    tempPlan = plan
                                    saveUserSettingToFirestore(userId, "plan", plan) { success ->
                                        if (success) {
                                            viewModel.setUserPlan(plan)
                                            Toast.makeText(context, "Plan updated successfully", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Failed to update plan", Toast.LENGTH_SHORT).show()
                                        }
                                        showPlanDialog = false
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = plan == userPlan,
                                onClick = {
                                    tempPlan = plan
                                    saveUserSettingToFirestore(userId, "plan", plan) { success ->
                                        if (success) {
                                            viewModel.setUserPlan(plan)
                                            Toast.makeText(context, "Plan updated successfully", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Failed to update plan", Toast.LENGTH_SHORT).show()
                                        }
                                        showPlanDialog = false
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(plan)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { showPlanDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

// 保存用户设置到Firestore
fun saveUserSettingToFirestore(userId: String, field: String, value: String, callback: (Boolean) -> Unit) {
    val db = com.google.firebase.Firebase.firestore
    val userSettingsRef = db.collection("users").document(userId).collection("user_settings").document("preferences")

    // 使用merge option确保不会覆盖其他字段
    userSettingsRef.set(mapOf(field to value), SetOptions.merge())
        .addOnSuccessListener {
            callback(true)
        }
        .addOnFailureListener {
            callback(false)
        }
}