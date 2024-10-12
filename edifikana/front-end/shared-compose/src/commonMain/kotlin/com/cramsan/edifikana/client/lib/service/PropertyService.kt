package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.PropertyModel

interface PropertyService {

    suspend fun getPropertyList(): Result<List<PropertyModel>>

}