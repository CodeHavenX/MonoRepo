@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.model.PropertyId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Dummy implementation of [PropertyService] for testing purposes.
 */
class DummyPropertyService : PropertyService {

    private val _activeProperty = MutableStateFlow<PropertyId?>(null)

    override suspend fun getPropertyList(): Result<List<PropertyModel>> {
        return Result.success(
            listOf(
                PropertyModel(
                    id = PropertyId("1"),
                )
            )
        )
    }

    override fun activeProperty(): StateFlow<PropertyId?> {
        return _activeProperty
    }

    override fun setActiveProperty(propertyId: PropertyId?): Result<Unit> {
        _activeProperty.value = propertyId
        return Result.success(Unit)
    }
}
