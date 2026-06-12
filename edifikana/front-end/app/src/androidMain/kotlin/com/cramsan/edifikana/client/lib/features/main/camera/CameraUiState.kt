package com.cramsan.edifikana.client.lib.features.main.camera

import android.net.Uri

/**
 * Camera UI state.
 */
sealed class CameraUiState {

    /**
     * No operation.
     */
    data class PhotoConfirmation(
        val photoUri: Uri
    ) : CameraUiState()

    /**
     * Preview camera.
     */
    data class PreviewCamera(
        val lensFacing: Int,
        val captureWidth: Int,
        val captureHeight: Int,
    ) : CameraUiState()

    /**
     * Permission denied.
     */
    data object PermissionDenied : CameraUiState()

    /**
     * Error.
     */
    data class Error(
        val message: String
    ) : CameraUiState()
}
