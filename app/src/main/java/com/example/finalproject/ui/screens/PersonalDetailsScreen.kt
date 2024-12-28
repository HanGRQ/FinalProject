package com.example.finalproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.finalproject.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDetailsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Page") },
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
                headlineContent = { Text("Hello World") },
                supportingContent = { Text("helloworldXX@gmail.com") },
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
                trailingContent = { Icon(Icons.Default.Edit, contentDescription = "Edit") },
                modifier = Modifier
                    .background(Color(0xFFF0F9F0))
                    .clickable { /* Handle edit profile click */ }
            )

            // Personal Information
            ListItem(
                headlineContent = { Text("Age") },
                supportingContent = { Text("23") },
                trailingContent = { Icon(Icons.Default.Edit, contentDescription = "Edit") }
            )

            ListItem(
                headlineContent = { Text("Weight") },
                supportingContent = { Text("65kg") },
                trailingContent = { Icon(Icons.Default.Edit, contentDescription = "Edit") }
            )

            ListItem(
                headlineContent = { Text("Height") },
                supportingContent = { Text("175cm") },
                trailingContent = { Icon(Icons.Default.Edit, contentDescription = "Edit") }
            )

            ListItem(
                headlineContent = { Text("Gender") },
                supportingContent = { Text("Female") },
                trailingContent = { Icon(Icons.Default.Edit, contentDescription = "Edit") }
            )
        }
    }
}