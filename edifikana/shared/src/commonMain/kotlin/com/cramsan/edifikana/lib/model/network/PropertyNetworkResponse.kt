package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a property.
 */
@NetworkModel
@Serializable
data class PropertyNetworkResponse(
    @SerialName("id")
    val id: PropertyId,
    @SerialName("name")
    val name: String,
    @SerialName("address")
    val address: String? = null,
    @SerialName("organization_id")
    val organizationId: OrganizationId,
) : ResponseBody
