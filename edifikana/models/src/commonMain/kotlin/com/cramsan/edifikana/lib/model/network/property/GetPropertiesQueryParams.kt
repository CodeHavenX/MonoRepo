package com.cramsan.edifikana.lib.model.network.property

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for listing properties.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Query parameters for listing properties, requiring an organization id.")
data class GetPropertiesQueryParams(
    @SerialName("organization_id")
    @JsonSchema.Description("Identifier of the organization to list properties for.")
    val organizationId: OrganizationId,
) : QueryParam
