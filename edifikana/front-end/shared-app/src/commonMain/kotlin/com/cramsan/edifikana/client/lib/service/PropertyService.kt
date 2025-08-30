package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import kotlinx.coroutines.flow.StateFlow

/**
 * Property service interface.
 */
interface PropertyService {

    /**
     * Get a list of properties associated with current user.
     */
    suspend fun getPropertyList(): Result<List<PropertyModel>>

    /**
     * Get the observable reference to the active property. You can use this function to fetch the current
     * active property or to observe changes to the active property.
     */
    fun activeProperty(): StateFlow<PropertyId?>

    /**
     * Set the active property.
     */
    fun setActiveProperty(propertyId: PropertyId?): Result<Unit>

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
