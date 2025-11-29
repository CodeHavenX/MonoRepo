package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId

/**
 * Property service interface.
 */
interface PropertyService {

    /**
     * Get a list of properties associated with current user.
     */
    suspend fun getPropertyList(): Result<List<PropertyModel>>

    /**
     * Get the property with the given id.
     */
    suspend fun getProperty(propertyId: PropertyId): Result<PropertyModel>

    /**
     * Add a new property.
     */
    suspend fun addProperty(
        propertyName: String,
        address: String,
        organizationId: OrganizationId
    ): Result<PropertyModel>

    /**
     * Update the property with the given [propertyId].
     */
    suspend fun updateProperty(propertyId: PropertyId, name: String, address: String): Result<PropertyModel>

    /**
     * Remove the property with the given [propertyId].
     */
    suspend fun removeProperty(propertyId: PropertyId): Result<Unit>
}
