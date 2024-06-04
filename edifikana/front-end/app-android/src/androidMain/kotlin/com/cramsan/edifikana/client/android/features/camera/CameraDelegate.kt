package com.cramsan.edifikana.client.android.features.camera

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.managers.remoteconfig.ImageConfig
import com.cramsan.framework.logging.logE
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CameraDelegate(
    private val activity: ComponentActivity,
    private val imageConfig: ImageConfig
) {

    private val _uiState = MutableStateFlow<CameraUiState>(
        CameraUiState.PreviewCamera(
            lensFacing = CameraSelector.LENS_FACING_BACK,
            captureWidth = imageConfig.captureWidth,
            captureHeight = imageConfig.captureHeight,
        )
    )
    val uiState: StateFlow<CameraUiState> = _uiState

    private val _event = MutableSharedFlow<CameraEvent>()
    val event: SharedFlow<CameraEvent> = _event

    private val scope = activity.lifecycleScope

    private val requestPermissionLauncher = activity.registerPermissionCallbacks(
        onPermissionGranted = { displayFrontCameraPreview() },
        onPermissionDenied = { displayPermissionDeniedMessage() },
    )

    fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                displayFrontCameraPreview()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) -> {
                displayPermissionDeniedMessage()
            }
            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun displayPermissionDeniedMessage() {
        _uiState.value = CameraUiState.PermissionDenied
    }

    fun toggleCamera() {
        val previewState = _uiState.value as? CameraUiState.PreviewCamera ?: return

        val newCameraSelection = when (previewState.lensFacing) {
            CameraSelector.LENS_FACING_BACK -> CameraSelector.LENS_FACING_FRONT
            CameraSelector.LENS_FACING_FRONT -> CameraSelector.LENS_FACING_BACK
            else -> return
        }

        _uiState.value = previewState.copy(
            lensFacing = newCameraSelection,
        )
    }

    private fun displayErrorMessage(message: String, throwable: Throwable) {
        logE(TAG, message, throwable)
        _uiState.value = CameraUiState.Error(message)
    }

    private fun openImageConfirmation(uri: Uri?) {
        if (uri == null) {
            displayErrorMessage(
                activity.resources.getString(R.string.text_error_take_photo),
                RuntimeException("Failed to save image")
            )
            return
        } else {
            _uiState.value = CameraUiState.PhotoConfirmation(uri)
        }
    }

    fun displayFrontCameraPreview() {
        _uiState.value = CameraUiState.PreviewCamera(
            lensFacing = CameraSelector.LENS_FACING_BACK,
            captureWidth = imageConfig.captureWidth,
            captureHeight = imageConfig.captureHeight,
        )
    }

    fun capturePhoto(
        imageCapture: ImageCapture,
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val fileName = SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss-SSS",
            Locale.US,
        ).format(System.currentTimeMillis()) + ".jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            activity.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues,
        ).build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    displayErrorMessage(
                        activity.resources.getString(R.string.text_error_take_photo),
                        exception,
                    )
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    openImageConfirmation(outputFileResults.savedUri)
                }
            }
        )
    }

    fun handleBackNavigation() {
        scope.launch {
            when (_uiState.value) {
                is CameraUiState.PermissionDenied -> _event.emit(CameraEvent.CancelFlow())
                is CameraUiState.Error -> _event.emit(CameraEvent.CancelFlow())
                is CameraUiState.PhotoConfirmation -> displayFrontCameraPreview()
                is CameraUiState.PreviewCamera -> _event.emit(CameraEvent.CancelFlow())
            }
        }
    }

    fun handleResult(uri: Uri) {
        scope.launch {
            _event.emit(CameraEvent.CompleteFlow(uri = uri))
        }
    }

    fun openAppSettings() {
        scope.launch {
            _event.emit(CameraEvent.OpenSettings())
        }
    }

    companion object {
        private const val TAG = "CameraDelegate"
    }
}

private fun ComponentActivity.registerPermissionCallbacks(
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
