package com.example.finalproject.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.AuthCredential

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var isLoading by remember { mutableStateOf(false) }

    // Google Sign-In
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("140845605756-uhcqtqse2u4vspci6dsd2srop8dnbl8r.apps.googleusercontent.com") // 替换为你的 Web 客户端 ID
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val startForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(task, auth, onLoginSuccess, context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Welcome Back",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Login now and let's live healthy together",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Enter Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Enter Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                isLoading = true
                auth.signInWithEmailAndPassword(email.value, password.value)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid ?: ""
                            Toast.makeText(context, "Login Success!", Toast.LENGTH_SHORT).show()
                            onLoginSuccess(userId)
                        } else {
                            Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40))
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "Login Now", fontSize = 18.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Forgot Password?",
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Google Sign-In Button
        Button(
            onClick = {
                val signInIntent = googleSignInClient.signInIntent
                startForResult.launch(signInIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB4437)) // Google Red
        ) {
            Text(text = "Sign in with Google", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Register Button
        Text(
            text = "Don't have an account? Register Now",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onNavigateToRegister() }
                .align(Alignment.CenterHorizontally)
        )
    }
}

private fun handleSignInResult(
    completedTask: Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount>,
    auth: FirebaseAuth,
    onLoginSuccess: (String) -> Unit,
    context: android.content.Context
) {
    try {
        val account = completedTask.getResult(ApiException::class.java)
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        signInWithGoogleCredential(auth, credential, onLoginSuccess, context)
    } catch (e: ApiException) {
        Toast.makeText(context, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun signInWithGoogleCredential(
    auth: FirebaseAuth,
    credential: AuthCredential,
    onLoginSuccess: (String) -> Unit,
    context: android.content.Context
) {
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: ""
                Toast.makeText(context, "Google sign in success!", Toast.LENGTH_SHORT).show()
                onLoginSuccess(userId)
            } else {
                Toast.makeText(context, "Google sign in failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}