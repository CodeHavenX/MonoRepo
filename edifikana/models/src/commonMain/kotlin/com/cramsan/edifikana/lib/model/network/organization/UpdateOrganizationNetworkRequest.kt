package com.cramsan.edifikana.lib.model.network.organization

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for updating an organization.
 */
@NetworkModel
@Serializable
@JsonSchema.Description(
    "Request payload to update an existing organization. Only provided (non-null) fields are updated.",
)
data class UpdateOrganizationNetworkRequest(
    @SerialName("name")
    @JsonSchema.Description("New display name for the organization, or null to leave unchanged.")
    val name: String?,
    @SerialName("description")
    @JsonSchema.Description("New description for the organization, or null to leave unchanged.")
    val description: String?,
) : RequestBody
