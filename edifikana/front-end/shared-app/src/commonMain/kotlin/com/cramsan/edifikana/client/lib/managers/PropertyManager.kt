package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.FileService
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI

/**
 * Manager for property configuration.
 */
class PropertyManager(
    private val propertyService: PropertyService,
    private val storageManager: StorageManager,
    private val fileService: FileService,
    private val dependencies: ManagerDependencies,
) {
    /**
     * Get the list of properties.
     */
    suspend fun getPropertyList(): Result<List<PropertyModel>> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getPropertyList")
        propertyService.getPropertyList().getOrThrow()
    }

    /**
     * Get the list of properties that the current user has admin access to.
     */
    suspend fun getProperty(propertyId: PropertyId): Result<PropertyModel> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getProperty")
        propertyService.getProperty(propertyId).getOrThrow()
    }

    /**
     * Add a new property.
     * Supports both default icons (via imageUrl) and custom image uploads (via imageUri).
     * If imageUri is provided, uploads the custom image and uses storage reference.
     * Otherwise, uses the provided imageUrl (e.g., "drawable:CASA") or null.
     *
     * @param propertyName Name of the property
     * @param address Address of the property
     * @param organizationId Organization ID
     * @param imageUrl URL for default icon (e.g., "drawable:CASA"), ignored if imageUri is provided
     * @param imageUri Local URI of custom image to upload, takes precedence over imageUrl
     * @return Result containing the created PropertyModel
     */
    suspend fun addProperty(
        propertyName: String,
        address: String,
        organizationId: OrganizationId,
        imageUrl: String? = null,
        imageUri: CoreUri? = null,
    ) = dependencies.getOrCatch(TAG) {
        logI(TAG, "addProperty: propertyName=$propertyName, imageUri=$imageUri")

        // If custom image upload is requested
        if (imageUri != null) {
            // Step 1: Create property without image to get propertyId
            logI(TAG, "Creating property without image for custom upload...")
            val newProperty = propertyService.addProperty(propertyName, address, organizationId, imageUrl = null)
                .getOrThrow()
            val propertyId = newProperty.id
            logI(TAG, "Property created with ID: $propertyId")

            // Step 2-3: Upload image and get storage reference
            val storageUrl = uploadPropertyImage(imageUri, propertyId)
            val updatedProperty = propertyService.updateProperty(propertyId, propertyName, address, storageUrl)
                .getOrThrow()
            logI(TAG, "Property updated with custom image successfully")

            updatedProperty
        } else {
            // Standard flow with default icon or no icon
            logI(TAG, "Creating property with default icon: $imageUrl")
            propertyService.addProperty(propertyName, address, organizationId, imageUrl).getOrThrow()
        }
    }

    /**
     * Update the property with the given [propertyId].
     * Supports both default icons (via imageUrl) and custom image uploads (via imageUri).
     * If imageUri is provided, uploads the custom image and uses storage reference.
     * Otherwise, uses the provided imageUrl (e.g., "drawable:CASA") or null.
     *
     * @param propertyId ID of the property to update
     * @param name Updated name
     * @param address Updated address
     * @param imageUrl URL for default icon, ignored if imageUri is provided
     * @param imageUri Local URI of custom image to upload, takes precedence over imageUrl
     * @return Result containing the updated PropertyModel
     */
    suspend fun updateProperty(
        propertyId: PropertyId,
        name: String,
        address: String,
        imageUrl: String? = null,
        imageUri: CoreUri? = null,
    ) = dependencies.getOrCatch(TAG) {
        logI(TAG, "updateProperty: propertyId=$propertyId, imageUri=$imageUri")

        // If custom image upload is requested
        if (imageUri != null) {
            // Step 1-2: Upload image and get storage reference
            val storageUrl = uploadPropertyImage(imageUri, propertyId)
            val updatedProperty = propertyService.updateProperty(propertyId, name, address, storageUrl)
                .getOrThrow()
            logI(TAG, "Property updated with custom image successfully")

            updatedProperty
        } else {
            // Standard flow with default icon or no icon
            logI(TAG, "Updating property with default icon: $imageUrl")
            propertyService.updateProperty(propertyId, name, address, imageUrl).getOrThrow()
        }
    }

    /**
     * Remove the property with the given [propertyId].
     */
    suspend fun removeProperty(propertyId: PropertyId): Result<Unit> = dependencies.getOrCatch(TAG) {
        logI(TAG, "removeProperty")
        propertyService.removeProperty(propertyId).requireSuccess()
        propertyService.getPropertyList().requireSuccess()
    }

    /**
     * Uploads a property image and returns the storage URL ready for persistence.
     * Sanitizes the filename, builds the storage path, and uploads the image.
     *
     * @param imageUri Local URI of the image to upload
     * @param propertyId Property ID used to namespace the storage path
     * @return Storage URL string in the format "storage:<ref>"
     */
    private suspend fun uploadPropertyImage(imageUri: CoreUri, propertyId: PropertyId): String {
        val filename = sanitizeFilename(fileService.getFilename(imageUri))
        logI(TAG, "Filename: $filename")
        val targetRef = "private/properties/${propertyId}_$filename"
        logI(TAG, "Uploading image to: $targetRef")
        val storageRef = storageManager.uploadImage(imageUri, targetRef).getOrThrow()
        logI(TAG, "Upload successful: $storageRef")
        return "storage:$storageRef"
    }

    /**
     * Sanitizes a filename to prevent path traversal attacks.
     * Strips any directory components and replaces unsafe characters.
     */
    private fun sanitizeFilename(filename: String): String {
        return filename
            .substringAfterLast('/')
            .substringAfterLast('\\')
            .replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .ifEmpty { "file" }
    }

    companion object {
        private const val TAG = "PropertyManager"
    }
}
