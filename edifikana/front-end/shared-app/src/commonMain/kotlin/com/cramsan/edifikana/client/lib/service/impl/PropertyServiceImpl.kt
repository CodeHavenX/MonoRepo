package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.SHOW_ALL
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.network.PropertyNetworkResponse
import com.cramsan.framework.core.runSuspendCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Default implementation for the [PropertyService].
 */
class PropertyServiceImpl(
    private val http: HttpClient,
) : PropertyService {

    private val _activeProperty = MutableStateFlow<PropertyId?>(null)

    @OptIn(NetworkModel::class)
    override suspend fun getPropertyList(
        showAll: Boolean,
    ): Result<List<PropertyModel>> = runSuspendCatching(TAG) {
        val response = http.get(Routes.Property.PATH) {
            url {
                parameters.append(SHOW_ALL, showAll.toString())
            }
        }.body<List<PropertyNetworkResponse>>()
        val propertyList = response.map {
            it.toPropertyModel()
        }
        propertyList
    }

    override fun activeProperty(): StateFlow<PropertyId?> {
        return _activeProperty
    }

    override fun setActiveProperty(propertyId: PropertyId?): Result<Unit> = runSuspendCatching(TAG) {
        _activeProperty.value = propertyId
    }

    @OptIn(NetworkModel::class)
    override suspend fun getAdminPropertyList(): Result<List<PropertyModel>> = runSuspendCatching(TAG) {
        val response = http.get(Routes.Property.PATH).body<List<PropertyNetworkResponse>>()
        val propertyList = response.map {
            it.toPropertyModel()
        }
        propertyList
    }

    @OptIn(NetworkModel::class)
    override suspend fun getProperty(propertyId: PropertyId): Result<PropertyModel> = runSuspendCatching(TAG) {
        val response = http.get("${Routes.Property.PATH}/$propertyId").body<PropertyNetworkResponse>()
        response.toPropertyModel()
    }

    companion object {
        const val TAG = "PropertyServiceImpl"
    }
}

@NetworkModel
private fun PropertyNetworkResponse.toPropertyModel(): PropertyModel {
    return PropertyModel(
        id = PropertyId(id),
        name = name,
        address = "", // TODO: Address is not available in the network response.
    )
}
