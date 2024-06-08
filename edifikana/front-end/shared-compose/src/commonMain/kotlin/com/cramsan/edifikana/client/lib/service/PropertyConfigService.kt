package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.PropertyConfigModel

interface PropertyConfigService {
    suspend fun getPropertyConfig(): Result<PropertyConfigModel>
}
