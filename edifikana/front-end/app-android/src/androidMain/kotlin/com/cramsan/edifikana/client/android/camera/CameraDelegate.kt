package com.cramsan.edifikana.client.android.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCaptureException
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.cramsan.edifikana.client.android.R
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CameraDelegate(
    private val activity: ComponentActivity,
) {

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Loading)
    val uiState: StateFlow<CameraUiState> = _uiState

    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                requestCameraPermission()
            }
        })
    }

    private val requestPermissionLauncher = activity.register(
        { setAsPermissionGranted() },
        { setAsPermissionDenied() },
    )

    fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                setAsPermissionGranted()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA
            ) -> {
                setAsPermissionDenied()
            }

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun handleImageCaptureError(exception: ImageCaptureException) {
        _uiState.value = CameraUiState.Error
    }

    fun handleImageCapture(uri: Uri) {
        _uiState.value = CameraUiState.PhotoConfirmation(uri)
    }

    private fun setAsPermissionGranted() {
        _uiState.value = CameraUiState.PreviewCamera(
            activity.getOutputDirectory().absolutePath,
        )
    }

    private fun setAsPermissionDenied() {
        _uiState.value = CameraUiState.PermissionDenied
    }
}

private fun ComponentActivity.register(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
) = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        onPermissionGranted()
    } else {
        onPermissionDenied()
    }
}

private fun ComponentActivity.getOutputDirectory(): File {
    val mediaDir = externalMediaDirs.firstOrNull()?.let {
        File(it, FOLDER_IMAGES).apply { mkdirs() }
    }

    return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
}

private const val FOLDER_IMAGES = "images"