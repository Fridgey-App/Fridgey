package com.fridgey.app.screen

import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.sharp.CropFree
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.fridgey.app.ui.theme.FridgeyText
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.*
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ScanScreen(onBackPressed: () -> Unit) {
    var scannedBarcodes = remember { mutableStateListOf<String>() }

    CheckCameraPermission {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            Card(modifier = Modifier.fillMaxWidth(), elevation = 5.dp, shape = RectangleShape) {
                Row(modifier = Modifier.padding(5.dp)) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.Outlined.ArrowBackIosNew,
                            contentDescription = "go back",
                            modifier = Modifier.size(25.dp),
                            tint = Color.Black
                        )
                    }
                    Column {
                        FridgeyText(t = "scanning for barcodes", isBold = true)
                        AnimatedContent(
                            targetState = scannedBarcodes.size,
                            transitionSpec = {
                                scaleIn() with scaleOut()
                            }
                        ) { targetCount ->
                            FridgeyText("$targetCount barcodes total")
                        }

                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                CameraPreviewView(onBarcodesScanned = { data ->
                    for (b in data)
                        if (!scannedBarcodes.contains(b)) scannedBarcodes.add(b)
                })
                Icon(
                    Icons.Sharp.CropFree,
                    contentDescription = "focus center",
                    modifier = Modifier.size(100.dp).alpha(0.5f).align(Alignment.Center),
                    tint = Color.White
                )
                Button(onClick = {  }, Modifier.align(Alignment.BottomEnd).padding(30.dp)) {
                    FridgeyText("continue", isBold = true)
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun CheckCameraPermission(content: @Composable () -> Unit) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted) {
        content()
    } else {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(50.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            FridgeyText(   "the camera is required to scan your items")
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                FridgeyText("grant permission")
            }
        }
    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({ continuation.resume(cameraProvider.get()) }, ContextCompat.getMainExecutor(this))
    }
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
private fun CameraPreviewView(onBarcodesScanned: (List<String>) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val barcodeScannerOptions = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_EAN_13).build()
    val barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions)
    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    val imageAnalysis = ImageAnalysis.Builder()
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
        try {
            val inputImage = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    Log.d("[SCAN]", "Scanned ${barcodes.size} barcodes")
                    var scannedBarcodes = arrayListOf<String>()
                    for (barcode in barcodes) {
                        if (barcode.valueType == Barcode.TYPE_PRODUCT && barcode.displayValue != null)
                            scannedBarcodes.add(barcode.displayValue!!)
                    }
                    onBarcodesScanned(scannedBarcodes)
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    Log.e("[SCAN]","An error occurred while scanning: ", e)
                    imageProxy.close()
                }
        }
        catch (e:Exception){
            Log.e("[SCAN]","An error occurred while scanning: ", e)
            imageProxy.close()
        }
    }

    LaunchedEffect(Unit) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis, preview)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
}