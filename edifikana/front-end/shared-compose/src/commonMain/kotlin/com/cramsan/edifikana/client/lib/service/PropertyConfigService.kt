package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.PropertyConfigModel

/**
 * Service for managing property configurations.
 */
interface PropertyConfigService {
    /**
     * Get the property configuration.
     */
    suspend fun getPropertyConfig(): Result<PropertyConfigModel>
}
