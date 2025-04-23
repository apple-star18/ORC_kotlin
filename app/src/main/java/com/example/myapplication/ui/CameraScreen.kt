package com.example.ocrscanner.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ocrscanner.util.OcrUtils
import java.util.concurrent.Executors
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import com.example.myapplication.R
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.unit.sp

@Composable
fun CameraScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var detectedText by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                OcrUtils.processImage(imageProxy) { result ->
                    detectedText = result
                    val corrected = OcrUtils.getCorrectedText(result, listOf("tzaqol", "tzagol", "tzaqal"))
                    if (corrected.equals("tzaqol", ignoreCase = true)) {
                        // just call onClose and open browser
                        openUrl(context, "https://tzaqol.com") {
                            onClose() //call onClose to return to introScreen
                        }
                    }
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("CameraScreen", "Binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraExecutor.shutdown()
        }
    }

    val customFont = FontFamily(
        Font(
            resId = R.font.ibm_plex_sans_semi_bold_italic,
            weight = FontWeight.SemiBold,
            style = FontStyle.Italic
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6300),
                    contentColor = Color(0xFF851600)
                ),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Scanner Close",
                    fontSize = 18.sp,
                    fontFamily = customFont,
                    )
            }

            AndroidView(factory = { previewView }, modifier = Modifier.weight(1f))
        }
    }
}

private fun openUrl(context: Context, url: String, onClose: () -> Unit) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)

    //After Browser open, call onClose to return to introScreen
    onClose()
}
