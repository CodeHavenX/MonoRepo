package com.cramsan.edifikana.client.android.features.camera

import android.net.Uri

sealed class CameraUiState {

    data class PhotoConfirmation(
        val photoUri: Uri
    ) : CameraUiState()

    data class PreviewCamera(
        val lensFacing: Int,
        val captureWidth: Int,
        val captureHeight: Int,
    ) : CameraUiState()

    data object PermissionDenied : CameraUiState()

    data class Error(
        val message: String
    ) : CameraUiState()
}
