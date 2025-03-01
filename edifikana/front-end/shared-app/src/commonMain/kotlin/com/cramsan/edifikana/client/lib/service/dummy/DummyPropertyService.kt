package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.model.PropertyId
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Dummy implementation for the [PropertyService] with hardcoded responses.
 */
class DummyPropertyService : PropertyService {

    private val _activeProperty = MutableStateFlow<PropertyId?>(PROPERTY_1.id)

    override suspend fun getPropertyList(showAll: Boolean): Result<List<PropertyModel>> {
        return Result.success(
            listOf(
                PROPERTY_1,
                PROPERTY_2,
            )
        )
    }

    override fun activeProperty(): StateFlow<PropertyId?> {
        return _activeProperty.asStateFlow()
    }

    override fun setActiveProperty(propertyId: PropertyId?): Result<Unit> {
        _activeProperty.value = propertyId
        return Result.success(Unit)
    }

    override suspend fun getAdminPropertyList(): Result<List<PropertyModel>> {
        return Result.success(
            listOf(
                PROPERTY_1,
                PROPERTY_2,
            )
        )
    }

    override suspend fun getProperty(propertyId: PropertyId): Result<PropertyModel> {
        return Result.success(PROPERTY_1)
    }

    override suspend fun addProperty(propertyName: String, address: String): Result<PropertyModel> {
        TODO("Not yet implemented")
    }

    override suspend fun updateProperty(propertyId: PropertyId, name: String, address: String): Result<PropertyModel> {
        TODO("Not yet implemented")
    }
}
