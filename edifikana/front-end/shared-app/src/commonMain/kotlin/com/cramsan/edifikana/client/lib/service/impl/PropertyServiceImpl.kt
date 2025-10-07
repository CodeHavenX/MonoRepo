package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.api.PropertyApi
import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.network.CreatePropertyNetworkRequest
import com.cramsan.edifikana.lib.model.network.PropertyNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpdatePropertyNetworkRequest
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.networkapi.buildRequest
import com.cramsan.framework.preferences.Preferences
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Default implementation for the [PropertyService].
 */
class PropertyServiceImpl(
    private val http: HttpClient,
    private val preferences: Preferences,
) : PropertyService {

    private val _activeProperty = MutableStateFlow<PropertyId?>(null)

    @OptIn(NetworkModel::class)
    override suspend fun getPropertyList(): Result<List<PropertyModel>> = runSuspendCatching(TAG) {
        val activePropertyId = preferences.loadString(PREF_ACTIVE_PROPERTY)
        val response = PropertyApi
            .getAssignedProperties
            .buildRequest()
            .execute(http)
        val propertyList = response.properties.map {
            it.toPropertyModel()
        }
        // Find the first property that matches the active property id.
        // If the active property id is not found, select the first property.
        // If there are no properties, set the active property to null.
        (
            propertyList.firstOrNull {
                it.id.propertyId == activePropertyId
            } ?: propertyList.firstOrNull()
            ).let {
            setActiveProperty(it?.id)
        }
        propertyList
    }

    override fun activeProperty(): StateFlow<PropertyId?> {
        return _activeProperty
    }

    override fun setActiveProperty(propertyId: PropertyId?): Result<Unit> = runSuspendCatching(TAG) {
        preferences.saveString(PREF_ACTIVE_PROPERTY, propertyId?.propertyId)
        _activeProperty.value = propertyId
    }

    @OptIn(NetworkModel::class)
    override suspend fun getProperty(propertyId: PropertyId): Result<PropertyModel> = runSuspendCatching(TAG) {
        val response = PropertyApi
            .getProperty
            .buildRequest(propertyId)
            .execute(http)
        response.toPropertyModel()
    }

    @OptIn(NetworkModel::class)
    override suspend fun addProperty(
        propertyName: String,
        address: String,
        organizationId: OrganizationId,
    ): Result<PropertyModel> = runSuspendCatching(TAG) {
        val response = PropertyApi
            .createProperty
            .buildRequest(
                CreatePropertyNetworkRequest(
                    name = propertyName,
                    address = address,
                    organizationId = organizationId,
                )
            )
            .execute(http)

        response.toPropertyModel()
    }

    @OptIn(NetworkModel::class)
    override suspend fun updateProperty(
        propertyId: PropertyId,
        name: String,
        address: String,
    ): Result<PropertyModel> = runSuspendCatching(TAG) {
        val response = PropertyApi
            .updateProperty.buildRequest(
                propertyId,
                UpdatePropertyNetworkRequest(
                    name = name,
                    address = address,
                )
            ).execute(http)

        response.toPropertyModel()
    }

    override suspend fun removeProperty(propertyId: PropertyId): Result<Unit> = runSuspendCatching(TAG) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "PropertyServiceImpl"
        const val PREF_ACTIVE_PROPERTY = "activeProperty"
    }
}

@NetworkModel
private fun PropertyNetworkResponse.toPropertyModel(): PropertyModel {
    return PropertyModel(
        id = id,
        name = name,
        address = address.orEmpty(),
        organizationId = organizationId,
    )
}
