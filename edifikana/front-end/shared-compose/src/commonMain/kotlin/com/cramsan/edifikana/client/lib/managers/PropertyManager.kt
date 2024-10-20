package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.client.lib.utils.getOrCatch
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.flow.StateFlow

/**
 * Manager for property configuration.
 */
class PropertyManager(
    private val propertyService: PropertyService,
    private val workContext: WorkContext,
) {
    /**
     * Get the list of properties.
     */
    suspend fun getPropertyList(): Result<List<PropertyModel>> = workContext.getOrCatch(TAG) {
        logI(TAG, "getPropertyList")
        propertyService.getPropertyList().getOrThrow()
    }

    /**
     * Set the active property.
     */
    suspend fun setActiveProperty(propertyId: PropertyId?) = workContext.getOrCatch(TAG) {
        logI(TAG, "setActiveProperty")
        propertyService.setActiveProperty(propertyId).getOrThrow()
    }

    /**
     * Get the active property. You can use this function to either fetch the value or observe it.
     */
    fun activeProperty(): StateFlow<PropertyId?> = propertyService.activeProperty()

    companion object {
        private const val TAG = "PropertyConfigManager"
    }
}
