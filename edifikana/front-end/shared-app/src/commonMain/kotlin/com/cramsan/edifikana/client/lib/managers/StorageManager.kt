package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.service.FileService
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.utils.FileValidationUtils
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.edifikana.client.ui.components.ImageOptionUIModel
import com.cramsan.edifikana.client.ui.components.ImageSource
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI

/**
 * Manager for storage operations.
 *
 * Provides a layer between ViewModels and StorageService, following the application's
 * architectural pattern where ViewModels interact with Managers, not Services directly.
 */
class StorageManager(
    private val storageService: StorageService,
    private val fileService: FileService,
    private val ioDependencies: IODependencies,
    private val dependencies: ManagerDependencies,
) {
    /**
     * Upload a file to storage.
     *
     * @param data The file data as bytes
     * @param targetRef The target storage path/reference
     * @return Result containing the storage reference on success, or error on failure
     */
    suspend fun uploadFile(data: ByteArray, targetRef: String): Result<String> = dependencies.getOrCatch(TAG) {
        logI(TAG, "uploadFile: targetRef=$targetRef, size=${data.size} bytes")
        storageService.uploadFile(data, targetRef).getOrThrow()
    }

    /**
     * Download a file from storage.
     *
     * @param targetRef The storage path/reference to download
     * @return Result containing the local file URI on success, or error on failure
     */
    suspend fun downloadFile(targetRef: String): Result<CoreUri> = dependencies.getOrCatch(TAG) {
        logI(TAG, "downloadFile: targetRef=$targetRef")
        storageService.downloadFile(targetRef).getOrThrow()
    }

    /**
     * Validate an image file and prepare it for preview.
     * Validates file size and type, then creates an ImageOptionUIModel for local preview.
     * Does NOT upload the image - upload happens later when user confirms the action.
     *
     * @param uri The local file URI to validate
     * @return Result containing ImageOptionUIModel for preview, or error on validation failure
     */
    suspend fun validateAndPrepareImagePreview(uri: CoreUri): Result<ImageOptionUIModel> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "validateAndPrepareImagePreview: uri=$uri")

            // Validate file size (max 10MB)
            FileValidationUtils.validateFileSize(uri, ioDependencies).getOrThrow()
            logI(TAG, "File size validation passed")

            // Validate file type (images only)
            FileValidationUtils.validateFileType(uri, ioDependencies, imagesOnly = true).getOrThrow()
            logI(TAG, "File type validation passed")

            // Get filename for preview
            val filename = fileService.getFilename(uri)
            logI(TAG, "Filename: $filename")

            // Create preview UI model
            val previewIcon = ImageOptionUIModel(
                id = "custom_local",
                displayName = "Custom Image",
                imageSource = ImageSource.LocalFile(uri, filename),
            )

            logI(TAG, "Image preview prepared successfully")
            previewIcon
        }

    /**
     * Upload an image file from URI to storage.
     * Handles reading, processing (EXIF rotation + compression), and uploading.
     *
     * @param uri The local file URI to upload
     * @param targetRef The target storage path/reference (e.g., "private/properties/id_filename.jpg")
     * @return Result containing the storage reference on success, or error on failure
     */
    suspend fun uploadImage(uri: CoreUri, targetRef: String): Result<String> = dependencies.getOrCatch(TAG) {
        logI(TAG, "uploadImage: uri=$uri, targetRef=$targetRef")

        // Read bytes from URI
        val bytes = fileService.readFileBytes(uri).getOrThrow()
        logI(TAG, "Read ${bytes.size} bytes from file")

        // Process image (EXIF rotation + compression on Android, raw on JVM)
        val processedBytes = fileService.processImage(bytes).getOrThrow()
        logI(TAG, "Processed image, final size: ${processedBytes.size} bytes")

        // Upload to storage
        val storageRef = storageService.uploadFile(processedBytes, targetRef).getOrThrow()
        logI(TAG, "Upload successful: $storageRef")

        storageRef
    }

    companion object {
        private const val TAG = "StorageManager"
    }
}
