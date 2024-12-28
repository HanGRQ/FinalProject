package com.example.finalproject.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.utils.DatabaseHelper
import com.example.finalproject.viewmodel.ScanViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay

private const val TAG = "ScanScreen"

@Composable
fun ScanScreen(
    navController: NavController,
    viewModel: ScanViewModel,
    databaseHelper: DatabaseHelper
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scanResult by viewModel.scanResult.collectAsState()

    var isProcessingBarcode by remember { mutableStateOf(false) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 请求权限
    if (!hasCameraPermission) {
        LaunchedEffect(Unit) {
            ActivityCompat.requestPermissions(
                context as androidx.activity.ComponentActivity,
                arrayOf(Manifest.permission.CAMERA),
                0
            )
        }
        Text("Camera permission required", Modifier.fillMaxSize())
        return
    }

    // 原有的测试代码和扫描结果处理
    LaunchedEffect(Unit) {
        try {
            val testBarcode = "6903252710175"
            Log.d("ScanScreen", "Testing barcode: $testBarcode")
            val result = databaseHelper.getFoodDetailsByBarcode(testBarcode)
            Log.d("ScanScreen", "Query result: ${result?.name ?: "Not found"}")
        } catch (e: Exception) {
            Log.e("ScanScreen", "Test query failed", e)
        }
    }

    LaunchedEffect(scanResult) {
        scanResult?.let { barcode ->
            if (!isProcessingBarcode) {
                isProcessingBarcode = true
                try {
                    Log.d(TAG, "Processing scan result: $barcode")
                    val foodDetails = databaseHelper.getFoodDetailsByBarcode(barcode)
                    if (foodDetails != null) {
                        Log.d(TAG, "Found product, preparing navigation")
                        delay(100)
                        navController.navigate("food_details/$barcode") {
                            launchSingleTop = true
                        }
                    } else {
                        Toast.makeText(
                            context, "Product not found: $barcode", Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing scan result", e)
                    Toast.makeText(
                        context, "Error: ${e.message}", Toast.LENGTH_LONG
                    ).show()
                } finally {
                    viewModel.resetScan()
                    isProcessingBarcode = false
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 原有的相机预览
        CameraPreview { detectedBarcode ->
            if (!isProcessingBarcode) {
                Log.d(TAG, "Detected barcode: $detectedBarcode")
                viewModel.onBarcodeDetected(detectedBarcode)
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }

        // UI Overlays
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = { /* Handle flash */ },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_flash),
                    contentDescription = "Flash",
                    tint = Color.White
                )
            }
        }

        // Scan Frame
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                // Scan Line Animation
                val infiniteTransition = rememberInfiniteTransition()
                val scanLineY by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 250f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color(0xFF00BFA5))
                        .graphicsLayer {
                            translationY = scanLineY
                        }
                )
            }
        }

        // Bottom Message
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_warning),
                    contentDescription = "Warning",
                    tint = Color(0xFFFFA726),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Place the barcode within the frame")
            }
        }
    }
}

// Original CameraPreview composable stays the same

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
private fun CameraPreview(onBarcodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { previewView ->
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder()
            .setTargetResolution(android.util.Size(1280, 720))
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(android.util.Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
            try {
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(
                        mediaImage, imageProxy.imageInfo.rotationDegrees
                    )

                    BarcodeScanning.getClient()
                        .process(image)
                        .addOnSuccessListener { barcodes ->
                            barcodes.firstOrNull()?.rawValue?.let { value ->
                                onBarcodeScanned(value)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Barcode recognition failed", e)
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing image", e)
                imageProxy.close()
            }
        }

        try {
            cameraProvider.unbindAll()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageAnalysis
            )
            preview.setSurfaceProvider(previewView.surfaceProvider)
        } catch (e: Exception) {
            Log.e(TAG, "Error binding camera", e)
        }
    }
}
