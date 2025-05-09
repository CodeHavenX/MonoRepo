package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.model.PropertyId
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration.Companion.milliseconds

/**
 * Dummy implementation for the [PropertyService] with hardcoded responses.
 */
class DummyPropertyService : PropertyService {

    private val properties = mutableListOf(
        PROPERTY_1,
        PROPERTY_2,
    )

    private val _activeProperty = MutableStateFlow<PropertyId?>(PROPERTY_1.id)

    override suspend fun getPropertyList(showAll: Boolean): Result<List<PropertyModel>> {
        delay(500.milliseconds)
        return Result.success(properties)
    }

    override fun activeProperty(): StateFlow<PropertyId?> {
        return _activeProperty.asStateFlow()
    }

    override fun setActiveProperty(propertyId: PropertyId?): Result<Unit> {
        _activeProperty.value = propertyId
        return Result.success(Unit)
    }

    override suspend fun getProperty(propertyId: PropertyId): Result<PropertyModel> {
        delay(500.milliseconds)
        val property = properties.firstOrNull { it.id == propertyId }
        if (property == null) {
            return Result.failure(IllegalArgumentException("Property not found"))
        }
        return Result.success(property)
    }

    override suspend fun addProperty(propertyName: String, address: String): Result<PropertyModel> {
        delay(500.milliseconds)
        val newProperty = PropertyModel(
            id = PropertyId("property-${properties.size + 1}"),
            name = propertyName,
            address = address,
        )
        properties.add(newProperty)
        return Result.success(newProperty)
    }

    override suspend fun updateProperty(propertyId: PropertyId, name: String, address: String): Result<PropertyModel> {
        delay(500.milliseconds)
        val property = properties.firstOrNull { it.id == propertyId }
        if (property == null) {
            return Result.failure(IllegalArgumentException("Property not found"))
        }
        val updatedProperty = property.copy(name = name, address = address)
        properties[properties.indexOf(property)] = updatedProperty
        return Result.success(updatedProperty)
    }

    override suspend fun removeProperty(propertyId: PropertyId): Result<Unit> {
        delay(500.milliseconds)
        val property = properties.firstOrNull { it.id == propertyId }
        if (property == null) {
            return Result.failure(IllegalArgumentException("Property not found"))
        }
        properties.remove(property)
        return Result.success(Unit)
    }
}
