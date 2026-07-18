package com.cramsan.edifikana.lib.model.network.organization

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Network response for a list of organizations.
 *
 * @property organizations The list of organizations.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A list of organizations.")
data class OrganizationNetworkListNetworkResponse(
    @JsonSchema.Description("The organizations matching the request.")
    val organizations: List<OrganizationNetworkResponse>,
) : ResponseBody
