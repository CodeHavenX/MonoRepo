package com.cramsan.edifikana.client.android.features.camera

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.cramsan.edifikana.client.android.config.ImageConfig
import com.cramsan.edifikana.client.android.features.camera.compose.CameraPreview
import com.cramsan.edifikana.client.android.features.camera.compose.PhotoConfirmation
import com.cramsan.edifikana.client.android.features.camera.compose.PhotoError
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CameraActivity : ComponentActivity() {

    @Inject
    lateinit var imageConfig: ImageConfig

    private lateinit var cameraDelegate: CameraDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraDelegate = CameraDelegate(this, imageConfig)

        onBackPressedDispatcher.addCallback {
            cameraDelegate.handleBackNavigation()
        }

        setContent {
            val uiState by cameraDelegate.uiState.collectAsState()
            val event by cameraDelegate.event.collectAsState(CameraEvent.Noop)

            LaunchedEffect(event) {
                when (val event = event) {
                    CameraEvent.Noop -> Unit
                    is CameraEvent.CompleteFlow -> {
                        setResult(
                            RESULT_OK,
                            Intent().apply {
                                putExtra(RESULT_URI, event.uri.toString())
                            }
                        )
                        finish()
                    }
                    is CameraEvent.CancelFlow -> finish()
                }
            }

            when (val state = uiState) {
                is CameraUiState.PhotoConfirmation -> {
                    PhotoConfirmation(
                        photoUri = state.photoUri,
                        onCancelClick = { cameraDelegate.displayFrontCameraPreview() },
                    ) { uri ->
                        cameraDelegate.handleResult(uri)
                    }
                }
                is CameraUiState.PreviewCamera -> {
                    CameraPreview(
                        lensFacing = state.lensFacing,
                        captureWidth = state.captureWidth,
                        captureHeight = state.captureHeight,
                        onShutterButtonClick = { imageCapture -> cameraDelegate.capturePhoto(imageCapture) },
                        onToggleCameraClick = { cameraDelegate.toggleCamera() },
                    )
                }
                is CameraUiState.Error -> {
                    PhotoError(
                        message = state.message,
                        onCancelClick = { cameraDelegate.displayFrontCameraPreview() },
                    )
                }
                CameraUiState.PermissionDenied -> { }
            }
        }
    }
}

const val RESULT_URI = "result_uri"
