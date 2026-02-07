package com.cramsan.edifikana.client.lib.features.home.addproperty

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.FileManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.service.StorageService
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
    private val storageService: StorageService,
    private val fileManager: FileManager,
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
     * If selectedImageUri is provided, creates property first, uploads image with propertyID, then updates property.
     *
     * @param propertyName Name of the property
     * @param address Address of the property
     * @param imageUrl Pre-defined image URL (e.g., "drawable:CASA" for default icons)
     * @param selectedImageUri Local file URI for custom image upload
     */
    fun addProperty(
        propertyName: String,
        address: String,
        imageUrl: String? = null,
        selectedImageUri: CoreUri? = null
    ) {
        viewModelScope.launch {
            val organizationId = requireNotNull(uiState.value.orgId)

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
            .onFailure { error ->
                logE(TAG, "Failed to create property", error)
                updateUiState { it.copy(isLoading = false, isUploading = false) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Failed to create property"))
            }
            .getOrThrow()

        val propertyId = newProperty.id
        logI(TAG, "Property created with ID: $propertyId")

        // Step 2: Get filename and prepare for upload
        val originalFilename = try {
            fileManager.getFilename(selectedImageUri)
        } catch (e: Exception) {
            logE(TAG, "Failed to get filename", e)
            val message = "Unable to read file. Property created but image not uploaded."
            updateUiState { it.copy(isLoading = false, isUploading = false, uploadError = message) }
            emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
            return
        }

        // Step 3: Read and process image bytes
        val processedBytes = prepareImageForUpload(selectedImageUri).getOrElse { error ->
            logE(TAG, "Failed to prepare image: ${error.message}", error)
            val message = "Unable to process image. Property created but image not uploaded."
            updateUiState { it.copy(isLoading = false, isUploading = false, uploadError = message) }
            emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
            return
        }

        // Step 4: Upload image and link to property
        uploadAndLinkImage(propertyId, propertyName, address, processedBytes, originalFilename, newProperty)
    }

    /**
     * Read and process image bytes for upload.
     * Handles reading bytes from URI and processing (EXIF rotation + compression on Android).
     *
     * @param uri The image URI to read
     * @return Result containing processed bytes, or error if read/process failed
     */
    private suspend fun prepareImageForUpload(uri: CoreUri): Result<ByteArray> {
        // Read bytes from URI
        val bytes = fileManager.readFileBytes(uri).getOrElse { error ->
            return Result.failure(Exception("Failed to read file bytes", error))
        }

        // Process image data (EXIF rotation + compression on Android, raw data on JVM)
        val processedBytes = fileManager.processImage(bytes).getOrElse { error ->
            return Result.failure(Exception("Failed to process image", error))
        }

        return Result.success(processedBytes)
    }

    /**
     * Upload image to storage and link to property.
     * Handles the complete upload → update property flow with all error cases.
     *
     * @param propertyId The property to link the image to
     * @param propertyName The property name (for success message)
     * @param address The property address
     * @param processedBytes The processed image bytes to upload
     * @param originalFilename The original filename (for storage reference)
     * @param newProperty The newly created property model
     */
    private suspend fun uploadAndLinkImage(
        propertyId: com.cramsan.edifikana.lib.model.PropertyId,
        propertyName: String,
        address: String,
        processedBytes: ByteArray,
        originalFilename: String,
        newProperty: com.cramsan.edifikana.client.lib.models.PropertyModel
    ) {
        val targetRef = "private/properties/${propertyId}_${originalFilename}"
        logI(TAG, "Uploading image to properties folder, size: ${processedBytes.size} bytes")

        storageService.uploadFile(processedBytes, targetRef).onSuccess { storageRef ->
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
                val message = "File size must be less than 10MB"
                updateUiState { it.copy(uploadError = message, selectedIcon = null) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
                return@launch
            }

            // Validate file type
            FileValidationUtils.validateFileType(uri, ioDependencies, imagesOnly = true).onFailure { error ->
                val message = "Please select a valid image file (JPG, PNG, GIF, or WebP)"
                updateUiState { it.copy(uploadError = message, selectedIcon = null) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(message))
                return@launch
            }

            // Get filename for preview
            val filename = try {
                fileManager.getFilename(uri)
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
