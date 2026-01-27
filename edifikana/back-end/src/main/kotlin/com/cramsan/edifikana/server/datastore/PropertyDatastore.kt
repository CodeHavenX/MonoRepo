package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.service.models.Property

/**
 * Interface for interacting with the property database.
 */
interface PropertyDatastore {

    /**
     * Creates a new property. Returns the [Result] of the operation with the created [Property].
     */
    suspend fun createProperty(
        name: String,
        address: String,
        creatorUserId: UserId,
        organizationId: OrganizationId,
        imageUrl: String? = null,
    ): Result<Property>

    /**
     * Retrieves a property by its ID. Returns the [Result] of the operation with the fetched [Property] if found.
     */
    suspend fun getProperty(propertyId: PropertyId): Result<Property?>

    /**
     * Retrieves all properties for a user. Returns the [Result] of the operation with a list of [Property].
     */
    suspend fun getProperties(userId: UserId): Result<List<Property>>

    /**
     * Updates a property with the given ID. Returns the [Result] of the operation with the updated [Property].
     */
    suspend fun updateProperty(
        propertyId: PropertyId,
        name: String?,
        address: String?,
        imageUrl: String? = null,
    ): Result<Property>

    /**
     * Deletes a property with the given ID. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    suspend fun deleteProperty(propertyId: PropertyId): Result<Boolean>

    /**
     * Permanently deletes a soft-deleted property record by ID.
     * Only purges if the record is already soft-deleted.
     * This is intended for testing and maintenance purposes only.
     * Returns the [Result] of the operation with a [Boolean] indicating if the record was purged.
     */
    suspend fun purgeProperty(propertyId: PropertyId): Result<Boolean>
}
