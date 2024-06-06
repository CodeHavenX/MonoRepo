package com.cramsan.edifikana.client.android.features.camera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.android.features.camera.compose.CameraPreview
import com.cramsan.edifikana.client.android.features.camera.compose.PermissionDeniedScreen
import com.cramsan.edifikana.client.android.features.camera.compose.PhotoConfirmation
import com.cramsan.edifikana.client.android.features.camera.compose.PhotoErrorScreen
import com.cramsan.edifikana.client.lib.managers.remoteconfig.ImageConfig
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CameraActivity : ComponentActivity() {

    @Inject
    lateinit var imageConfig: ImageConfig

    private lateinit var cameraDelegate: CameraDelegate

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraDelegate = CameraDelegate(this, imageConfig)

        onBackPressedDispatcher.addCallback {
            cameraDelegate.handleBackNavigation()
        }

        setContent {
            val uiState by cameraDelegate.uiState.collectAsState()
            val event by cameraDelegate.event.collectAsState(CameraEvent.Noop)

            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                cameraDelegate.requestCameraPermission()
            }

            LaunchedEffect(event) {
                when (val viewModelEvent = event) {
                    CameraEvent.Noop -> Unit
                    is CameraEvent.CompleteFlow -> {
                        setResult(
                            RESULT_OK,
                            Intent().apply {
                                putExtra(RESULT_URI, viewModelEvent.uri.toString())
                            }
                        )
                        finish()
                    }
                    is CameraEvent.CancelFlow -> finish()
                    is CameraEvent.OpenSettings -> {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", this@CameraActivity.packageName, null)
                        )
                        startActivity(intent)
                    }
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
                    PhotoErrorScreen(
                        message = state.message,
                        onCancelClick = { cameraDelegate.displayFrontCameraPreview() },
                    )
                }
                is CameraUiState.PermissionDenied -> {
                    PermissionDeniedScreen(
                        onOpenSettingsClick = { cameraDelegate.openAppSettings() },
                        onCancelClick = { cameraDelegate.handleBackNavigation() },
                    )
                }
            }
        }
    }
}

const val RESULT_URI = "result_uri"
