package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.network.CreatePropertyNetworkRequest
import com.cramsan.edifikana.lib.model.network.PropertyNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpdatePropertyNetworkRequest
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.preferences.Preferences
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
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
        val response = http.get(Routes.Property.PATH) {}.body<List<PropertyNetworkResponse>>()
        val propertyList = response.map {
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
        val response = http.get("${Routes.Property.PATH}/$propertyId").body<PropertyNetworkResponse>()
        response.toPropertyModel()
    }

    @OptIn(NetworkModel::class)
    override suspend fun addProperty(
        propertyName: String,
        address: String,
        organizationId: OrganizationId,
    ): Result<PropertyModel> = runSuspendCatching(
        TAG
    ) {
        val response = http.post(Routes.Property.PATH) {
            setBody(
                CreatePropertyNetworkRequest(
                    name = propertyName,
                    address = address,
                    organizationId = organizationId.id,
                )
            )
            contentType(ContentType.Application.Json)
        }.body<PropertyNetworkResponse>()

        response.toPropertyModel()
    }

    @OptIn(NetworkModel::class)
    override suspend fun updateProperty(
        propertyId: PropertyId,
        name: String,
        address: String,
    ): Result<PropertyModel> = runSuspendCatching(TAG) {
        val response = http.put("${Routes.Property.PATH}/$propertyId") {
            setBody(
                UpdatePropertyNetworkRequest(
                    name = name,
                    address = address,
                )
            )
            contentType(ContentType.Application.Json)
        }.body<PropertyNetworkResponse>()

        response.toPropertyModel()
    }

    override suspend fun removeProperty(propertyId: PropertyId): Result<Unit> = runSuspendCatching(TAG) {
        TODO("Not yet implemented")
    }

    companion object {
        const val TAG = "PropertyServiceImpl"
        const val PREF_ACTIVE_PROPERTY = "activeProperty"
    }
}

@NetworkModel
private fun PropertyNetworkResponse.toPropertyModel(): PropertyModel {
    return PropertyModel(
        id = PropertyId(id),
        name = name,
        address = address.orEmpty(),
        organizationId = OrganizationId(organizationId),
    )
}
