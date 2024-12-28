package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.lib.model.PropertyId
import kotlinx.coroutines.flow.StateFlow

/**
 * Property service interface.
 */
interface PropertyService {

    /**
     * Get a list of properties associated with current user.
     */
    suspend fun getPropertyList(showAll: Boolean): Result<List<PropertyModel>>

    /**
     * Get the observable reference to the active property. You can use this function to fetch the current
     * active property or to observe changes to the active property.
     */
    fun activeProperty(): StateFlow<PropertyId?>

    /**
     * Set the active property.
     */
    fun setActiveProperty(propertyId: PropertyId?): Result<Unit>

    suspend fun getAdminPropertyList(): Result<List<PropertyModel>>

    /**
     * Get the property with the given id.
     */
    suspend fun getProperty(propertyId: PropertyId): Result<PropertyModel>
}
