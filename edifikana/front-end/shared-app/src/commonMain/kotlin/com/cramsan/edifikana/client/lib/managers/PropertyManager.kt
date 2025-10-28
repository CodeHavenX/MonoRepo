package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.OrganizationService
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.flow.StateFlow

/**
 * Manager for property configuration.
 */
class PropertyManager(
    private val propertyService: PropertyService,
    private val organizationService: OrganizationService,
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
     * Set the active property.
     */
    suspend fun setActiveProperty(propertyId: PropertyId?) = dependencies.getOrCatch(TAG) {
        logI(TAG, "setActiveProperty")
        propertyService.setActiveProperty(propertyId).getOrThrow()
    }

    /**
     * Get the active property. You can use this function to either fetch the value or observe it.
     */
    fun activeProperty(): StateFlow<PropertyId?> = propertyService.activeProperty()

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
    ) = dependencies.getOrCatch(TAG) {
        logI(TAG, "addProperty")
        val organizationId = organizationService.observableActiveOrganization.value?.id
            ?: error("No active organization set")

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
