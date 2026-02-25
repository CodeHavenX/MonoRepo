package com.cramsan.edifikana.client.lib.features.home.addproperty

import com.cramsan.edifikana.client.lib.features.home.shared.PropertyIconOptions
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StorageManager
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
    private val storageManager: StorageManager,
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
     * Supports both default icons and custom image uploads.
     *
     * @param propertyName Name of the property
     * @param address Address of the property
     * @param selectedIcon The selected icon (default icon or custom upload)
     */
    fun addProperty(
        propertyName: String,
        address: String,
        selectedIcon: ImageOptionUIModel?
    ) {
        viewModelScope.launch {
            val organizationId = requireNotNull(uiState.value.orgId)

            // Extract URI if user selected a custom local file
            val imageUri = if (selectedIcon?.id == "custom_local" &&
                selectedIcon.imageSource is ImageSource.LocalFile
            ) {
                (selectedIcon.imageSource as ImageSource.LocalFile).uri
            } else {
                null
            }

            // For non-custom images, use the converted imageUrl
            val imageUrl = if (imageUri == null) {
                PropertyIconOptions.toImageUrl(selectedIcon)
            } else {
                null
            }

            // Set loading states - isUploading only if we have a custom image
            updateUiState {
                it.copy(
                    isLoading = true,
                    isUploading = imageUri != null
                )
            }

            // Call PropertyManager with either imageUrl or imageUri
            propertyManager.addProperty(propertyName, address, organizationId, imageUrl, imageUri)
                .onSuccess { newProperty ->
                    updateUiState { it.copy(isLoading = false, isUploading = false) }
                    val message = if (imageUri != null) {
                        "Property ${newProperty.name} added with custom image"
                    } else {
                        "Property ${newProperty.name} added successfully"
                    }
                    emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
                    emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
                }
                .onFailure { error ->
                    logE(TAG, "Failed to add property", error)
                    val message = getUploadErrorMessage(error)
                    updateUiState { it.copy(isLoading = false, isUploading = false, uploadError = message) }
                    emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Failed to add property: $message"))
                }
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
            logI(TAG, "Validating and previewing image")
            validateAndPreviewImage(uri)
        }
    }

    /**
     * Validate the selected image and show local preview (without uploading).
     * Upload will happen later when user clicks "Add" button.
     */
    private fun validateAndPreviewImage(uri: CoreUri) {
        viewModelScope.launch {
            storageManager.validateAndPrepareImagePreview(uri)
                .onSuccess { previewIcon ->
                    logI(TAG, "Validation successful, showing local preview")
                    updateUiState {
                        it.copy(
                            selectedIcon = previewIcon,
                            uploadError = null
                        )
                    }
                }
                .onFailure { error ->
                    logE(TAG, "Validation failed: ${error.message}", error)
                    val message = error.message ?: "Failed to validate image"
                    updateUiState { it.copy(uploadError = message, selectedIcon = null) }
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
