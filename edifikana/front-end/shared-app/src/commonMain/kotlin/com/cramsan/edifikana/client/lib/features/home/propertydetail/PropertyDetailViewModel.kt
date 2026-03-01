package com.cramsan.edifikana.client.lib.features.home.propertydetail

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StorageManager
import com.cramsan.edifikana.client.ui.components.ImageSource
import com.cramsan.edifikana.lib.model.PropertyId
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
 * ViewModel for the PropertyDetail screen.
 **/
class PropertyDetailViewModel(
    dependencies: ViewModelDependencies,
    private val propertyManager: PropertyManager,
    private val storageManager: StorageManager,
    private val stringProvider: StringProvider,
) : BaseViewModel<PropertyDetailEvent, PropertyDetailUIState>(
    dependencies,
    PropertyDetailUIState.Initial,
    TAG,
) {

    /**
     * Initialize the ViewModel with the property ID.
     */
    fun initialize(propertyId: PropertyId) {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, propertyId = propertyId) }
            propertyManager.getProperty(propertyId)
                .onSuccess { property ->
                    updateUiState {
                        it.copy(
                            isLoading = false,
                            name = property.name,
                            address = property.address,
                            imageUrl = property.imageUrl,
                        )
                    }
                }
                .onFailure { throwable ->
                    updateUiState { it.copy(isLoading = false) }
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar(
                            "Failed to load property: ${throwable.message ?: "Unknown error"}"
                        )
                    )
                }
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
     * Toggle edit mode.
     */
    fun toggleEditMode() {
        viewModelScope.launch {
            updateUiState { it.copy(isEditMode = !it.isEditMode) }
        }
    }

    /**
     * Cancel edit mode and revert changes.
     */
    fun cancelEdit() {
        viewModelScope.launch {
            val propertyId = uiState.value.propertyId ?: return@launch
            updateUiState { it.copy(isLoading = true, isEditMode = false) }
            propertyManager.getProperty(propertyId)
                .onSuccess { property ->
                    updateUiState {
                        it.copy(
                            isLoading = false,
                            name = property.name,
                            address = property.address,
                            imageUrl = property.imageUrl,
                        )
                    }
                }
                .onFailure {
                    updateUiState { it.copy(isLoading = false) }
                }
        }
    }

    /**
     * Update the property name.
     */
    fun onNameChanged(name: String) {
        viewModelScope.launch {
            updateUiState { it.copy(name = name) }
        }
    }

    /**
     * Update the property address.
     */
    fun onAddressChanged(address: String) {
        viewModelScope.launch {
            updateUiState { it.copy(address = address) }
        }
    }

    /**
     * Update the property image URL.
     */
    fun onImageUrlChanged(imageUrl: String?) {
        viewModelScope.launch {
            updateUiState { it.copy(imageUrl = imageUrl) }
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
     * Called by platform when user selects images.
     */
    fun handleReceivedImages(uris: List<CoreUri>) {
        viewModelScope.launch {
            logI(TAG, "handleReceivedImages called with ${uris.size} URIs")
            if (uris.isEmpty()) return@launch

            // Only handle the first image for property icon upload
            val uri = uris.first()
            logI(TAG, "Validating and previewing image")

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
     * Save the property changes.
     * Supports both default icons and custom image uploads.
     */
    fun saveProperty() {
        viewModelScope.launch {
            val state = uiState.value
            val propertyId = state.propertyId ?: return@launch

            // Extract URI if user selected a custom local file
            val imageUri = if (state.selectedIcon?.id == "custom_local" &&
                state.selectedIcon.imageSource is ImageSource.LocalFile
            ) {
                (state.selectedIcon.imageSource as ImageSource.LocalFile).uri
            } else {
                null
            }

            // Use imageUrl from state if no custom upload
            val imageUrl = if (imageUri == null) state.imageUrl else null

            // Set loading states - isUploading only if we have a custom image
            updateUiState {
                it.copy(
                    isLoading = true,
                    isUploading = imageUri != null
                )
            }

            // Call PropertyManager with either imageUrl or imageUri
            propertyManager.updateProperty(propertyId, state.name, state.address, imageUrl, imageUri)
                .onSuccess {
                    updateUiState { it.copy(isLoading = false, isUploading = false, isEditMode = false) }
                    val message = if (imageUri != null) {
                        "Property updated with custom image"
                    } else {
                        "Property updated successfully"
                    }
                    emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
                }
                .onFailure { error ->
                    logE(TAG, "Failed to update property", error)
                    val message = getUploadErrorMessage(error)
                    updateUiState { it.copy(isLoading = false, isUploading = false, uploadError = message) }
                    emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Failed to update property: $message"))
                }
        }
    }

    /**
     * Delete the property.
     */
    fun deleteProperty() {
        viewModelScope.launch {
            val propertyId = uiState.value.propertyId ?: return@launch

            updateUiState { it.copy(isLoading = true) }
            propertyManager.removeProperty(propertyId)
                .onSuccess {
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar("Property deleted successfully")
                    )
                    emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
                }
                .onFailure { throwable ->
                    updateUiState { it.copy(isLoading = false) }
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar(
                            "Failed to delete property: ${throwable.message ?: "Unknown error"}"
                        )
                    )
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
        private const val TAG = "PropertyDetailViewModel"
    }
}
