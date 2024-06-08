package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.PropertyConfigModel
import com.cramsan.edifikana.client.lib.service.PropertyConfigService
import com.cramsan.edifikana.client.lib.utils.getOrCatch
import com.cramsan.framework.logging.logI

class PropertyConfigManager(
    private val propertyConfigService: PropertyConfigService,
    private val workContext: WorkContext,
) {
    suspend fun getPropertyConfig(): Result<PropertyConfigModel> = workContext.getOrCatch(TAG) {
        logI(TAG, "getPropertyConfig")
        propertyConfigService.getPropertyConfig().getOrThrow()
    }

    companion object {
        private const val TAG = "PropertyConfigManager"
    }
}
