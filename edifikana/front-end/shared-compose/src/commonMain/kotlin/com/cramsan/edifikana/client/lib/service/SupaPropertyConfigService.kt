package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.PropertyConfigModel
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching

class SupaPropertyConfigService : PropertyConfigService {

    override suspend fun getPropertyConfig(): Result<PropertyConfigModel> = runSuspendCatching(TAG) {
        TODO()
    }

    companion object {
        private const val TAG = "SupaPropertyConfigService"
    }
}
