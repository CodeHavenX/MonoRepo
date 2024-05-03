package com.cramsan.edifikana.client.android.camera

import android.net.Uri
import java.util.concurrent.Executor

sealed class CameraUiState {

    data object Loading : CameraUiState()

    data class PhotoConfirmation(
        val photoUri: Uri
    ) : CameraUiState()

    data class PreviewCamera(
        val outputDirectory: String,
    ) : CameraUiState()

    data object PermissionDenied : CameraUiState()

    data object Error : CameraUiState()
}