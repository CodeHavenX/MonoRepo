package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for creating an organization.
 */
@NetworkModel
@Serializable
data class CreateOrganizationNetworkRequest(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
) : RequestBody
