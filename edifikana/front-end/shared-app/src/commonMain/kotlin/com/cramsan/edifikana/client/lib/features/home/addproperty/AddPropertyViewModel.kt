package com.cramsan.edifikana.client.lib.features.home.addproperty

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.utils.FileValidationUtils
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.edifikana.client.lib.utils.getFilename
import com.cramsan.edifikana.client.lib.utils.processImageData
import com.cramsan.edifikana.client.lib.utils.readBytes
import com.cramsan.edifikana.client.ui.components.ImageOptionUIModel
import com.cramsan.edifikana.client.ui.components.ImageSource
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import kotlinx.coroutines.launch

/**
 * ViewModel for the AddProperty screen.
 **/
class AddPropertyViewModel(
    dependencies: ViewModelDependencies,
    private val propertyManager: PropertyManager,
    private val storageService: StorageService,
    private val ioDependencies: IODependencies,
    private val stringProvider: StringProvider,
) : BaseViewModel<AddPropertyEvent, AddPropertyUIState>(
    dependencies,
    AddPropertyUIState.Initial,
    TAG,
) {

    /**
     * Initialize the ViewModel with the organization ID.
     */
    fun initialize(orgId: OrganizationId) {
        viewModelScope.launch {
            updateUiState { it.copy(orgId = orgId) }
        }
    }

    /**
     * Navigate back to the previous screen.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Add a new property.
     */
    fun addProperty(propertyName: String, address: String, imageUrl: String? = null) {
        viewModelScope.launch {
            val organizationId = requireNotNull(uiState.value.orgId)
            updateUiState { it.copy(isLoading = true) }
            val newProperty = propertyManager.addProperty(propertyName, address, organizationId, imageUrl).onFailure {
                updateUiState { it.copy(isLoading = false) }
            }.getOrThrow()

            emitWindowEvent(
                EdifikanaWindowsEvent.ShowSnackbar(
                    "Property ${newProperty.name} added successfully"
                )
            )
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateBack
            )
        }
    }

    /**
     * Trigger the photo picker to select a custom image.
     */
    fun triggerPhotoPicker() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.OpenPhotoPicker)
        }
    }

    /**
     * Handle images received from the photo picker.
     * Called by MainActivity when user selects images.
     */
    fun handleReceivedImages(uris: List<CoreUri>) {
        viewModelScope.launch {
            logI(TAG, "handleReceivedImages called with ${uris.size} URIs")
            if (uris.isEmpty()) return@launch

            // Only handle the first image for property icon upload
            val uri = uris.first()
            logI(TAG, "Starting upload for URI: $uri")
            uploadImageAndSelect(uri)
        }
    }

    /**
     * Upload the selected image and update the selected icon.
     */
    private fun uploadImageAndSelect(uri: CoreUri) {
        viewModelScope.launch {
            // Show the local file preview immediately
            val localFileName = try {
                uri.getFilename(ioDependencies)
            } catch (e: Exception) {
                logE(TAG, "Failed to get filename for preview", e)
                "custom_image"
            }

            val previewIcon = ImageOptionUIModel(
                id = "custom_local",
                displayName = "Custom Image",
                imageSource = ImageSource.LocalFile(uri, localFileName),
            )

            updateUiState {
                it.copy(
                    selectedIcon = previewIcon,
                    isUploading = true,
                    uploadError = null
                )
            }

            // Validate file size
            FileValidationUtils.validateFileSize(uri, ioDependencies).onFailure { error ->
                val message = "File size must be less than 10MB"
                updateUiState { it.copy(isUploading = false, uploadError = message) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
                return@launch
            }

            // Validate file type
            FileValidationUtils.validateFileType(uri, ioDependencies, imagesOnly = true).onFailure { error ->
                val message = "Please select a valid image file (JPG, PNG, GIF, or WebP)"
                updateUiState { it.copy(isUploading = false, uploadError = message) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
                return@launch
            }

            // Get filename
            val filename = try {
                uri.getFilename(ioDependencies)
            } catch (e: Exception) {
                logE(TAG, "Failed to get filename", e)
                val message = "Unable to read file. Please try again."
                updateUiState { it.copy(isUploading = false, uploadError = message) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
                return@launch
            }

            // Read bytes
            val bytes = readBytes(uri, ioDependencies).getOrElse { error ->
                logE(TAG, "Failed to read file bytes", error)
                val message = "Unable to read file. Please try again."
                updateUiState { it.copy(isUploading = false, uploadError = message) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
                return@launch
            }

            // Process image data (rotation, compression)
            val processedBytes = processImageData(bytes).getOrElse { error ->
                logE(TAG, "Failed to process image", error)
                val message = "Unable to process image. Please try again."
                updateUiState { it.copy(isUploading = false, uploadError = message) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
                return@launch
            }

            // Upload to storage (must be in private/ folder per RLS policies)
            val targetRef = "private/properties/${filename}"
            logI(TAG, "Uploading to storage with targetRef: $targetRef, size: ${processedBytes.size} bytes")
            storageService.uploadFile(processedBytes, targetRef).onSuccess { storageRef ->
                logI(TAG, "Upload successful! storageRef: $storageRef")
                // Keep showing the local file preview, mark as uploaded
                // Note: storageRef is just "properties/filename", not a full URL
                // We keep the LocalFile source for preview and store the ref for saving
                val uploadedIcon = ImageOptionUIModel(
                    id = "custom_uploaded:$storageRef",
                    displayName = "Custom Image",
                    // Keep showing local file for preview since we can't load storage ref as URL
                    imageSource = ImageSource.LocalFile(uri, filename),
                )
                logI(TAG, "Upload complete, keeping local preview")
                updateUiState {
                    it.copy(
                        selectedIcon = uploadedIcon,
                        isUploading = false,
                        uploadError = null
                    )
                }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Image uploaded successfully"))
            }.onFailure { error ->
                logE(TAG, "Upload failed", error)
                val message = getUploadErrorMessage(error)
                // Keep the local preview but show error
                updateUiState { it.copy(isUploading = false, uploadError = message) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
            }
        }
    }

    /**
     * Map exceptions to user-friendly error messages.
     */
    private suspend fun getUploadErrorMessage(exception: Throwable): String {
        return when (exception) {
            is ClientRequestExceptions.UnauthorizedException ->
                "Authentication failed. Please sign in again."

            is ClientRequestExceptions.ForbiddenException ->
                "You don't have permission to upload files."

            is ClientRequestExceptions.InvalidRequestException ->
                "Invalid file. ${exception.message}"

            is ClientRequestExceptions.ConflictException ->
                "A file with this name already exists."

            else -> stringProvider.getString(Res.string.error_message_unexpected_error)
        }
    }

    companion object {
        private const val TAG = "AddPropertyViewModel"
    }
}
