package com.cramsan.edifikana.client.android.camera

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraActivity : ComponentActivity() {

    private val cameraDelegate = CameraDelegate(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState by cameraDelegate.uiState.collectAsState()
            when (val state = uiState) {
                is CameraUiState.PhotoConfirmation -> {
                    PhotoConfirmation(
                        state.photoUri,
                        onCancel = { finish() },
                    ) { uri ->
                        setResult(RESULT_OK, Intent().apply {
                            putExtra(RESULT_FILEPATH, uri.toString())
                        })
                        finish()
                    }
                }
                is CameraUiState.PreviewCamera -> {
                    CameraView(
                        outputDirectory = state.outputDirectory,
                        onImageCaptured = { cameraDelegate.handleImageCapture(it) },
                        onError = { cameraDelegate.handleImageCaptureError(it) }
                    )
                }
                CameraUiState.Error -> { }
                CameraUiState.Loading -> { }
                CameraUiState.PermissionDenied -> { }
            }
        }
    }
}

const val RESULT_FILEPATH = "result_filepath"

const val OUTPUT_FILENAME = "output_filename"