package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
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
    @SerialName("org_id")
    val orgId: OrganizationId,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String,
    @SerialName("description")
    val description: String? = null,
) : ResponseBody
