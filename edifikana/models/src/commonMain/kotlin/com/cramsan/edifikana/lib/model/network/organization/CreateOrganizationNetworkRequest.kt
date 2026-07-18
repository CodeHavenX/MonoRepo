package com.cramsan.edifikana.lib.model.network.organization

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for creating an organization.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to create a new organization.")
data class CreateOrganizationNetworkRequest(
    @SerialName("name")
    @JsonSchema.Description("Human-readable display name of the organization.")
    @JsonSchema.Example("\"Sunset Property Group\"")
    val name: String,
    @SerialName("description")
    @JsonSchema.Description("Description of the organization.")
    val description: String,
) : RequestBody
