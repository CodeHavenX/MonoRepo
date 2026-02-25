package com.cramsan.edifikana.client.lib.features.home.addproperty

import com.cramsan.edifikana.client.lib.features.home.shared.PropertyIconOptions
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.service.FileService
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StorageManager
import com.cramsan.edifikana.client.lib.utils.FileValidationUtils
import com.cramsan.edifikana.client.lib.utils.IODependencies
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
    private val fileService: FileService,
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
     * If selectedIcon is a custom local file, creates property first, uploads image with propertyID, then updates property.
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
            val selectedImageUri = if (selectedIcon?.id == "custom_local" &&
                selectedIcon.imageSource is ImageSource.LocalFile
            ) {
                (selectedIcon.imageSource as ImageSource.LocalFile).uri
            } else {
                null
            }

            // For non-custom images, use the converted imageUrl
            val imageUrl = if (selectedImageUri == null) {
                PropertyIconOptions.toImageUrl(selectedIcon)
            } else {
                null
            }

            // Set loading states - isUploading only if we have a custom image
            updateUiState {
                it.copy(
                    isLoading = true,
                    isUploading = selectedImageUri != null
                )
            }

            // Check if user selected a custom image that needs upload
            if (selectedImageUri != null) {
                // Flow: Create property → Upload image with propertyID → Update property
                handleAddPropertyWithCustomImage(propertyName, address, organizationId, selectedImageUri)
            } else {
                // Flow: Create property with imageUrl (default icon or no icon)
                handleAddPropertyWithDefaultIcon(propertyName, address, organizationId, imageUrl)
            }
        }
    }

    /**
     * Add property with default icon (normal flow).
     */
    private suspend fun handleAddPropertyWithDefaultIcon(
        propertyName: String,
        address: String,
        organizationId: OrganizationId,
        imageUrl: String?
    ) {
        val newProperty = propertyManager.addProperty(propertyName, address, organizationId, imageUrl)
            .onFailure {
                updateUiState { it.copy(isLoading = false) }
            }
            .getOrThrow()

        emitWindowEvent(
            EdifikanaWindowsEvent.ShowSnackbar(
                "Property ${newProperty.name} added successfully"
            )
        )
        emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
    }

    /**
     * Add property with custom image upload.
     * Flow: Create property → Upload image with propertyID → Update property with storage ref.
     * TODO: Refactor further down stack with @Cramsan
     */
    private suspend fun handleAddPropertyWithCustomImage(
        propertyName: String,
        address: String,
        organizationId: OrganizationId,
        selectedImageUri: CoreUri
    ) {
        // Step 1: Create property without imageUrl first to get propertyID
        logI(TAG, "Creating property without image...")
        val newProperty = propertyManager.addProperty(propertyName, address, organizationId, imageUrl = null)
            .getOrElse { error ->
                logE(TAG, "Failed to create property", error)
                updateUiState { it.copy(isLoading = false, isUploading = false) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Failed to create property"))
                return
            }

        val propertyId = newProperty.id
        logI(TAG, "Property created with ID: $propertyId")

        // Step 2: Get filename and prepare for upload
        val originalFilename = try {
            fileService.getFilename(selectedImageUri)
        } catch (e: Exception) {
            logE(TAG, "Failed to get filename", e)
            val message = "Unable to read file. Property created but image not uploaded."
            updateUiState { it.copy(isLoading = false, isUploading = false, uploadError = message) }
            emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
            return
        }

        // Step 3: Upload image using StorageManager
        val targetRef = "private/properties/${propertyId}_${originalFilename}"
        logI(TAG, "Uploading image to properties folder")

        storageManager.uploadImage(selectedImageUri, targetRef).onSuccess { storageRef ->
            logI(TAG, "Upload successful!")

            // Update property with storage reference
            val storageUrl = "storage:$storageRef"
            propertyManager.updateProperty(propertyId, propertyName, address, storageUrl)
                .onSuccess {
                    logI(TAG, "Property updated with image successfully")
                    updateUiState { it.copy(isLoading = false, isUploading = false) }
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar(
                            "Property ${newProperty.name} added with custom image"
                        )
                    )
                    emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
                }
                .onFailure { error ->
                    logE(TAG, "Failed to update property with image", error)
                    val message = "Property created but failed to attach image"
                    updateUiState { it.copy(isLoading = false, isUploading = false, uploadError = message) }
                    emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
                    emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
                }
        }.onFailure { error ->
            logE(TAG, "Upload failed", error)
            val message = getUploadErrorMessage(error)
            updateUiState { it.copy(isLoading = false, isUploading = false, uploadError = message) }
            emitWindowEvent(
                EdifikanaWindowsEvent.ShowSnackbar(
                    "Property created but image upload failed: $message"
                )
            )
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
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
            // Validate file size first
            FileValidationUtils.validateFileSize(uri, ioDependencies).onFailure { error ->
                logE(TAG, "Failed to validate image size: ${error.message}", error)
                val message = "File size must be less than 10MB"
                updateUiState { it.copy(uploadError = message, selectedIcon = null) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
                return@launch
            }

            // Validate file type
            FileValidationUtils.validateFileType(uri, ioDependencies, imagesOnly = true).onFailure { error ->
                logE(TAG, "File type validation failed: $error")
                val message = "Please select a valid image file (JPG, PNG, GIF, or WebP)"
                updateUiState { it.copy(uploadError = message, selectedIcon = null) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
                return@launch
            }

            // Get filename for preview
            val filename = try {
                fileService.getFilename(uri)
            } catch (e: Exception) {
                logE(TAG, "Failed to get filename", e)
                val message = "Unable to read file. Please try again."
                updateUiState { it.copy(uploadError = message, selectedIcon = null) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
                return@launch
            }

            // Show local preview - NO upload yet
            val previewIcon = ImageOptionUIModel(
                id = "custom_local",
                displayName = "Custom Image",
                imageSource = ImageSource.LocalFile(uri, filename),
            )

            logI(TAG, "Validation successful, showing local preview")
            updateUiState {
                it.copy(
                    selectedIcon = previewIcon,
                    uploadError = null
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
        private const val TAG = "AddPropertyViewModel"
    }
}
