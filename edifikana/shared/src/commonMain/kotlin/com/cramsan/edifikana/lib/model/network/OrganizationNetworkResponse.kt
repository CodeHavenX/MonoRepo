package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network model representing an organization.
 */
@NetworkModel
@Serializable
data class OrganizationNetworkResponse(
    @SerialName("id")
    val id: String,
)
