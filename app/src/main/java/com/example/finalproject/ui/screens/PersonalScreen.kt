package com.example.finalproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.R
import com.example.finalproject.ui.components.BottomNavigationBar
import com.example.finalproject.viewmodel.UserInfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalScreen(
    userId: String,
    viewModel: UserInfoViewModel, // ✅ 添加 UserInfoViewModel 以获取用户数据
    onNavigateTo: (String) -> Unit,
    onLogout: () -> Unit
) {
    val userEmail by viewModel.userEmail.collectAsState() // ✅ 获取用户邮箱
    val userHeight by viewModel.userHeight.collectAsState() // ✅ 获取身高
    val userWeight by viewModel.userWeight.collectAsState() // ✅ 获取当前体重

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "personal",
                onNavigate = onNavigateTo
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Image(
                painter = painterResource(id = R.drawable.profile_image),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ✅ 显示用户邮箱（登录的邮箱）
            Text(
                text = userEmail.ifEmpty { "Unknown Email" }, // ✅ 为空时显示默认值
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ✅ 显示用户的 Height 和 Weight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("${userHeight.ifEmpty { "--" }} cm", color = Color.Gray)
                Text(" · ", color = Color.Gray)
                Text("${userWeight.ifEmpty { "--" }} kg", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security Notice Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5F0)
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Security",
                        tint = Color(0xFF00A884)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Security Notice",
                        color = Color(0xFF00A884)
                    )
                }
            }

            // Navigation Items
            ListItem(
                headlineContent = { Text("Personal Page") },
                leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateTo("personal_details") }
            )

            ListItem(
                headlineContent = { Text("Settings") },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateTo("settings") }
            )

            // ✅ Logout 按钮
            ListItem(
                headlineContent = { Text("Logout", color = Color.Red) },
                leadingContent = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Red) },
                modifier = Modifier.clickable(onClick = onLogout)
            )
        }
    }
}
