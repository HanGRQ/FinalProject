package com.example.finalproject.ui.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.finalproject.R
import com.example.finalproject.ui.components.BottomNavigationBar
import com.example.finalproject.viewmodel.UserInfoViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalScreen(
    userId: String,
    viewModel: UserInfoViewModel,
    onLogout: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToData: () -> Unit,
    onNavigateToWeight: () -> Unit,
    onNavigateToFoodDetails: () -> Unit,
    onNavigateToPersonalDetails: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val userEmail by viewModel.userEmail.collectAsState()
    val userHeight by viewModel.userHeight.collectAsState()
    val userWeight by viewModel.userWeight.collectAsState()
    val profileImageUrl by viewModel.profileImageUrl.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadProfileImage(userId, it) { success ->
                if (!success) {
                    Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "personal",
                onNavigate = { route ->
                    when (route) {
                        "weight" -> onNavigateToWeight()
                        "home" -> onNavigateToHome()
                        "data" -> onNavigateToData()
                    }
                }
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
            Image(
                painter = rememberImagePainter(
                    data = profileImageUrl ?: R.drawable.profile_image,
                    builder = { crossfade(true) }
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = userEmail.ifEmpty { "Unknown Email" },
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
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
                modifier = Modifier.clickable { onNavigateToPersonalDetails() }
            )

            ListItem(
                headlineContent = { Text("Settings") },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateToSettings() }
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

fun uploadProfileImage(userId: String, uri: Uri, callback: (Boolean) -> Unit) {
    val storageRef = Firebase.storage.reference.child("profile_images/$userId.jpg")
    val firestoreRef = Firebase.firestore.collection("users").document(userId)

    storageRef.putFile(uri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                // 🔹 Firestore 写入头像 URL
                firestoreRef.set(mapOf("profileImageUrl" to downloadUri.toString()), SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("Upload", "Profile image successfully uploaded!")
                        callback(true)
                    }
                    .addOnFailureListener { e ->
                        Log.e("Upload", "Firestore update failed: ${e.message}")
                        callback(false)
                    }
            }.addOnFailureListener { e ->
                Log.e("Upload", "Failed to get download URL: ${e.message}")
                callback(false)
            }
        }
        .addOnFailureListener { e ->
            Log.e("Upload", "File upload failed: ${e.message}")
            callback(false)
        }
}
