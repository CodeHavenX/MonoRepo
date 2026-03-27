package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.CommonAreaType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new common area.
 */
@NetworkModel
@Serializable
data class CreateCommonAreaNetworkRequest(
    @SerialName("property_id")
    val propertyId: PropertyId,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: CommonAreaType,
    @SerialName("description")
    val description: String? = null,
) : RequestBody
