package com.cramsan.edifikana.lib.model.network.organization

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network model representing an organization.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("An organization that owns properties and has members.")
data class OrganizationNetworkResponse(
    @SerialName("id")
    @JsonSchema.Description("Unique identifier of the organization.")
    val id: OrganizationId,
    @SerialName("name")
    @JsonSchema.Description("Human-readable display name of the organization.")
    @JsonSchema.Example("\"Sunset Property Group\"")
    val name: String,
    @SerialName("description")
    @JsonSchema.Description("Description of the organization.")
    val description: String,
) : ResponseBody
