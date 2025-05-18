package com.cramsan.edifikana.client.lib.features.main.camera

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.main.camera.compose.CameraPreview
import com.cramsan.edifikana.client.lib.features.main.camera.compose.PermissionDeniedScreen
import com.cramsan.edifikana.client.lib.features.main.camera.compose.PhotoConfirmation
import com.cramsan.edifikana.client.lib.features.main.camera.compose.PhotoErrorScreen
import com.cramsan.edifikana.client.lib.managers.remoteconfig.ImageConfig
import com.cramsan.framework.core.compose.resources.StringProvider
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Activity that handles camera operations.
 */
class CameraActivity : ComponentActivity() {

    private val imageConfig: ImageConfig by inject()

    private val stringProvider: StringProvider by inject()

    private lateinit var cameraDelegate: CameraDelegate

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraDelegate = CameraDelegate(this, imageConfig, stringProvider)

        onBackPressedDispatcher.addCallback {
            cameraDelegate.handleBackNavigation()
        }

        setContent {
            val uiState by cameraDelegate.uiState.collectAsState()
            val scope = rememberCoroutineScope()

            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                cameraDelegate.requestCameraPermission()
            }

            LaunchedEffect(scope) {
                scope.launch {
                    cameraDelegate.event.collect { event ->
                        when (event) {
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
                            is CameraEvent.OpenSettings -> {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", this@CameraActivity.packageName, null)
                                )
                                startActivity(intent)
                            }
                        }
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
