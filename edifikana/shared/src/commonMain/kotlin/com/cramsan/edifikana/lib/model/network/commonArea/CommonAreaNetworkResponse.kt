package com.cramsan.edifikana.lib.model.network.commonArea

import com.cramsan.edifikana.lib.model.commonArea.CommonAreaId
import com.cramsan.edifikana.lib.model.commonArea.CommonAreaType
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a common area.
 */
@NetworkModel
@Serializable
data class CommonAreaNetworkResponse(
    @SerialName("common_area_id")
    val commonAreaId: CommonAreaId,
    @SerialName("property_id")
    val propertyId: PropertyId,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: CommonAreaType,
    @SerialName("description")
    val description: String?,
) : ResponseBody
