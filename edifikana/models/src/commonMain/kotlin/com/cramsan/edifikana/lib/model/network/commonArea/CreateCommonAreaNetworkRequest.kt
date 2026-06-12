package com.cramsan.edifikana.lib.model.network.commonArea

import com.cramsan.edifikana.lib.model.commonArea.CommonAreaType
import com.cramsan.edifikana.lib.model.property.PropertyId
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
    val description: String?,
) : RequestBody
