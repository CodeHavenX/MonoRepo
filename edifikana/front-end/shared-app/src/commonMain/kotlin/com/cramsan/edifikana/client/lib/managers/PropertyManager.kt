package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI

/**
 * Manager for property configuration.
 */
class PropertyManager(
    private val propertyService: PropertyService,
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
     */
    suspend fun addProperty(
        propertyName: String,
        address: String,
        organizationId: OrganizationId,
    ) = dependencies.getOrCatch(TAG) {
        logI(TAG, "addProperty")

        propertyService.addProperty(propertyName, address, organizationId).getOrThrow()
    }

    /**
     * Update the property with the given [propertyId].
     */
    suspend fun updateProperty(propertyId: PropertyId, name: String, address: String) = dependencies.getOrCatch(TAG) {
        logI(TAG, "updateProperty")
        propertyService.updateProperty(propertyId, name, address).getOrThrow()
    }

    /**
     * Remove the property with the given [propertyId].
     */
    suspend fun removeProperty(propertyId: PropertyId): Result<Unit> = dependencies.getOrCatch(TAG) {
        logI(TAG, "removeProperty")
        propertyService.removeProperty(propertyId).requireSuccess()
        propertyService.getPropertyList().requireSuccess()
    }

    companion object {
        private const val TAG = "PropertyManager"
    }
}
