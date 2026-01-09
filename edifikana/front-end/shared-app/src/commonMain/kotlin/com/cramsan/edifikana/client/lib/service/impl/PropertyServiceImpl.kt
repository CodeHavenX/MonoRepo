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
import io.ktor.client.HttpClient

/**
 * Default implementation for the [PropertyService].
 */
class PropertyServiceImpl(
    private val http: HttpClient,
) : PropertyService {

    @OptIn(NetworkModel::class)
    override suspend fun getPropertyList(): Result<List<PropertyModel>> = runSuspendCatching(TAG) {
        val response = PropertyApi
            .getAssignedProperties
            .buildRequest()
            .execute(http)
        val propertyList = response.properties.map {
            it.toPropertyModel()
        }
        propertyList
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
        imageUrl: String?,
    ): Result<PropertyModel> = runSuspendCatching(TAG) {
        val response = PropertyApi
            .createProperty
            .buildRequest(
                CreatePropertyNetworkRequest(
                    name = propertyName,
                    address = address,
                    organizationId = organizationId,
                    imageUrl = imageUrl,
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
        imageUrl: String?,
    ): Result<PropertyModel> = runSuspendCatching(TAG) {
        val response = PropertyApi
            .updateProperty.buildRequest(
                propertyId,
                UpdatePropertyNetworkRequest(
                    name = name,
                    address = address,
                    imageUrl = imageUrl,
                )
            ).execute(http)

        response.toPropertyModel()
    }

    override suspend fun removeProperty(propertyId: PropertyId): Result<Unit> = runSuspendCatching(TAG) {
        PropertyApi.deleteProperty.buildRequest(
            propertyId,
        ).execute(http)
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
        imageUrl = imageUrl,
    )
}
