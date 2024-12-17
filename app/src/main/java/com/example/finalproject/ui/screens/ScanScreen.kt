package com.example.finalproject.ui.screens

import android.Manifest
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.finalproject.utils.DatabaseHelper
import com.example.finalproject.viewmodel.ScanViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import android.content.pm.PackageManager


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
        Text("需要相机权限来扫描条形码", Modifier.fillMaxSize())
        return
    }

    // 添加测试代码
    LaunchedEffect(Unit) {
        try {
            val testBarcode = "6903252710175"
            Log.d("ScanScreen", "测试查询条形码: $testBarcode")
            val result = databaseHelper.getFoodDetailsByBarcode(testBarcode)
            Log.d("ScanScreen", "查询结果: ${result?.name ?: "未找到"}")
        } catch (e: Exception) {
            Log.e("ScanScreen", "测试查询失败", e)
        }
    }

    LaunchedEffect(scanResult) {
        scanResult?.let { barcode ->
            if (!isProcessingBarcode) {
                isProcessingBarcode = true
                try {
                    Log.d(TAG, "处理扫描结果: $barcode")
                    val foodDetails = databaseHelper.getFoodDetailsByBarcode(barcode)
                    if (foodDetails != null) {
                        Log.d(TAG, "找到商品信息，准备导航")
                        delay(100)
                        navController.navigate("food_details/$barcode") {
                            launchSingleTop = true
                        }
                    } else {
                        Toast.makeText(
                            context, "未找到商品信息: $barcode", Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "处理扫描结果时出错", e)
                    Toast.makeText(
                        context, "错误: ${e.message}", Toast.LENGTH_LONG
                    ).show()
                } finally {
                    viewModel.resetScan()
                    isProcessingBarcode = false
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview { detectedBarcode ->
            if (!isProcessingBarcode) {
                Log.d(TAG, "扫描到条形码: $detectedBarcode")
                viewModel.onBarcodeDetected(detectedBarcode)
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "将条形码对准框内",
                color = androidx.compose.ui.graphics.Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

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
                            Log.e(TAG, "条形码识别失败", e)
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            } catch (e: Exception) {
                Log.e(TAG, "处理图像时出错", e)
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
            Log.e(TAG, "绑定相机时出错", e)
        }
    }
}
