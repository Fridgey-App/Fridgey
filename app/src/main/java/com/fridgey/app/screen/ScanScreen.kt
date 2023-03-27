package com.fridgey.app.screen

import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.lifecycle.ViewModel
import com.fridgey.app.service.BarcodeInfo
import com.fridgey.app.service.BarcodeInfoService
import com.fridgey.app.ui.theme.FridgeyText
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class ScanViewModel @Inject constructor(val barcodeInfoService: BarcodeInfoService) : ViewModel()

@Composable
fun ScanScreen(onBackPressed: () -> Unit, viewModel: ScanViewModel) {
    var scannedBarcodes = remember { mutableStateListOf<String>() }
    var isCurrentlyScanning = remember { mutableStateOf(false) }

    fun onBarcodesScanned(data: List<String>) {
        for (b in data)
            if (!scannedBarcodes.contains(b)) scannedBarcodes.add(b)
    }

    CheckCameraPermission {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            ScanTopBar(onBackPressed = onBackPressed, barcodeCount = scannedBarcodes.size)
            if (isCurrentlyScanning.value)
                AddItemsList(barcodeList = scannedBarcodes, barcodeInfoService = viewModel.barcodeInfoService, scanMorePressed = { isCurrentlyScanning.value = false })
            else
                ScanCameraView(onBarcodesScanned = { data -> onBarcodesScanned(data) }, onFinishedScanning = { isCurrentlyScanning.value = true })
        }
    }
}

@Composable
fun FridgeItem(barcode: String, barcodeInfoService: BarcodeInfoService) {
    var barcodeInfo = remember { mutableStateOf(emptyList<BarcodeInfo>()) }
    var shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    LaunchedEffect(Unit) {
        try {
            val info = barcodeInfoService.getBarcodeInfo(barcode)
            barcodeInfo.value = listOf(info)
        }
        catch (ex: Throwable) {

        }
    }

    Card(modifier = Modifier
        .height(100.dp)
        .fillMaxWidth(), elevation = 5.dp) {
        if (barcodeInfo.value.isEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier
                .padding(15.dp)
                .fillMaxSize()) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .shimmer(shimmer)
                    .background(color = Color.LightGray))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .shimmer(shimmer)
                    .background(color = Color.LightGray))
            }
        }
        else if (barcodeInfo.value[0].status != "1")
            Text("Failed to get product info :(")
        else
            Text(barcodeInfo.value[0].product.product_name)
    }
}

@Composable
fun AddItemsList(barcodeList: List<String>, barcodeInfoService: BarcodeInfoService, scanMorePressed: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(20.dp)) {
            for (barcode in barcodeList) {
                item {
                    FridgeItem(barcode = barcode, barcodeInfoService = barcodeInfoService)
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End, modifier = Modifier.height(100.dp).padding(30.dp)) {
            Button(onClick = scanMorePressed) {
                FridgeyText("scan more", isBold = true)
            }
            Spacer(modifier = Modifier.width(15.dp))
            Button(onClick = { /*TODO*/ }) {
                FridgeyText("add", isBold = true)
            }
        }
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ScanTopBar(onBackPressed: () -> Unit, barcodeCount : Int) {
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
                    targetState = barcodeCount,
                    transitionSpec = {
                        scaleIn() with scaleOut()
                    }
                ) { targetCount ->
                    FridgeyText("$targetCount barcodes total")
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

@Composable
fun ScanCameraView(onBarcodesScanned: (List<String>) -> Unit, onFinishedScanning: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreviewView(onBarcodesScanned = onBarcodesScanned)
        Icon(
            Icons.Sharp.CropFree,
            contentDescription = "focus center",
            modifier = Modifier
                .size(100.dp)
                .alpha(0.5f)
                .align(Alignment.Center),
            tint = Color.White
        )
        Button(
            onClick = onFinishedScanning,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(30.dp)
        ) {
            FridgeyText("continue", isBold = true)
        }
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