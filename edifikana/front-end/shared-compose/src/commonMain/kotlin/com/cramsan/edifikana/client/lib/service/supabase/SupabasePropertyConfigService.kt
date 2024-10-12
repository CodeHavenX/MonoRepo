package com.cramsan.edifikana.client.lib.service.supabase

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.network.PropertyNetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class SupabasePropertyConfigService(
    private val http: HttpClient,
) : PropertyService {

    @OptIn(NetworkModel::class)
    override suspend fun getPropertyList(): Result<List<PropertyModel>> {
        val response = http.get(Routes.Property.PATH).body<List<PropertyNetworkResponse>>()
        val propertyList = response.map {
            it.toPropertyModel()
        }
        return Result.success(propertyList)
    }
}

@NetworkModel
fun PropertyNetworkResponse.toPropertyModel(): PropertyModel {
    return PropertyModel(
        id = id,
    )
}
