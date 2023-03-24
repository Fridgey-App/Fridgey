package com.fridgey.app.screen

import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.fridgey.app.ui.theme.FridgeyBlue
import com.fridgey.app.ui.theme.FridgeyText
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.util.*
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun ScanScreen() {
    fun onImageCaptured(image: ImageProxy) {
        Log.d("[SCAN]", "Image captured! ${image.width}x${image.height}")
        image.close()
    }
    val uiController = rememberSystemUiController()
    uiController.setSystemBarsColor(Color.Transparent)
    CheckCameraPermission {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            CameraPreviewView(onImageCaptured = { i -> onImageCaptured(i) }, onError = { })
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
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

@Composable
private fun CameraPreviewView(onImageCaptured: (ImageProxy) -> Unit, onError: (ImageCaptureException) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder().build()
    }


    LaunchedEffect(CameraSelector.LENS_FACING_BACK) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
        Column(modifier = Modifier.align(Alignment.BottomCenter), verticalArrangement = Arrangement.Bottom) {
            FloatingActionButton(
                onClick = { imageCapture.takePicture(onImageCaptured, onError) },
                modifier = Modifier.size(95.dp).shadow(2.dp, shape = CircleShape),
                backgroundColor = FridgeyBlue
            ) {
                Icon(
                    Icons.Outlined.PhotoCamera,
                    contentDescription = "take picture",
                    modifier = Modifier.size(50.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

fun ImageCapture.takePicture(onImageCaptured: (ImageProxy) -> Unit, onError: (ImageCaptureException) -> Unit) {
    this.takePicture(
        Executors.newSingleThreadExecutor(),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                onImageCaptured(image)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                onError(exception)
            }
        })
}
