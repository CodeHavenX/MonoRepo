package com.cramsan.edifikana.lib.model.network.organization

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network model representing an organization.
 */
@NetworkModel
@Serializable
data class OrganizationNetworkResponse(
    @SerialName("id")
    val id: OrganizationId,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
) : ResponseBody
