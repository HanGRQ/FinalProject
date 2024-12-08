package com.example.finalproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.R
import com.example.finalproject.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToFoodDetails: () -> Unit,
    onNavigateToMoodDetails: () -> Unit,
    onNavigateToWeight: () -> Unit,
    onNavigateToData: () -> Unit,
    onNavigateToPersonal: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "home",
                onNavigate = { route ->
                    when (route) {
                        "weight" -> onNavigateToWeight()
                        "data" -> onNavigateToData()
                        "personal" -> onNavigateToPersonal()
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_image),
                    contentDescription = "Profile Image",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "您好, Hello World", fontSize = 20.sp)
                    Text(text = "你昨天增加了2kg，继续保持！", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Calendar Icon",
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigate to Food Details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToFoodDetails() }
                    .padding(vertical = 8.dp)  // 增加点击区域
            ) {
                Text(text = "饮食数据", fontSize = 18.sp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_forward),
                    contentDescription = "Navigate to Food Details",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigate to Mood Details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToMoodDetails() }
                    .padding(vertical = 8.dp)  // 增加点击区域
            ) {
                Text(text = "今日情绪", fontSize = 18.sp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_forward),
                    contentDescription = "Navigate to Mood Details",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}