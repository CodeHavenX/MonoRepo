package com.cramsan.edifikana.client.lib.features.main.camera.compose

import android.content.Context
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.FlipCameraAndroid
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import edifikana_lib.Res
import edifikana_lib.text_flip_camera
import edifikana_lib.text_take_photo
import org.jetbrains.compose.resources.stringResource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Composable that displays the camera preview.
 *
 * @param lensFacing The lens facing of the camera.
 * @param captureWidth The width of the image to capture.
 * @param captureHeight The height of the image to capture.
 * @param onShutterButtonClick Callback when the shutter button is clicked.
 * @param onToggleCameraClick Callback when the toggle camera button is clicked.
 */
@Composable
fun CameraPreview(
    lensFacing: Int,
    captureWidth: Int,
    captureHeight: Int,
    onShutterButtonClick: (ImageCapture) -> Unit,
    onToggleCameraClick: () -> Unit,
) {
    // 1
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val preview = androidx.camera.core.Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder()
            .setTargetResolution(Size(captureWidth, captureHeight))
            .build()
    }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    // 2
    LaunchedEffect(lensFacing) {
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

    // 3
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        BottomActionBar(
            mainButton = {
                IconButton(onClick = { onShutterButtonClick(imageCapture) }) {
                    Icon(
                        imageVector = Icons.Sharp.Lens,
                        contentDescription = stringResource(Res.string.text_take_photo),
                        tint = Color.White,
                        modifier = Modifier
                            .border(1.dp, Color.White, CircleShape)
                            .fillMaxSize()
                    )
                }
            },
            secondaryButton = {
                IconButton(onClick = onToggleCameraClick) {
                    Icon(
                        imageVector = Icons.Sharp.FlipCameraAndroid,
                        contentDescription = stringResource(Res.string.text_flip_camera),
                        tint = Color.White,
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxSize()
                    )
                }
            }
        )
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    // TODO: Can we refactor this to use some sort of coroutine/future interop?
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener(
            {
                continuation.resume(cameraProvider.get())
            },
            ContextCompat.getMainExecutor(this),
        )
    }
}

@Preview
@Composable
private fun PreviewCameraView() {
    CameraPreview(
        CameraSelector.LENS_FACING_BACK,
        0,
        0,
        onShutterButtonClick = { _ -> },
        onToggleCameraClick = {},
    )
}
