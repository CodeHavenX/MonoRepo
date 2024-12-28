package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.flow.StateFlow

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
    suspend fun getPropertyList(
        showAll: Boolean = false,
    ): Result<List<PropertyModel>> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getPropertyList")
        propertyService.getPropertyList(showAll).getOrThrow()
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

    suspend fun getProperty(propertyId: PropertyId): Result<PropertyModel> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getProperty")
        propertyService.getProperty(propertyId).getOrThrow()
    }

    companion object {
        private const val TAG = "PropertyManager"
    }
}
