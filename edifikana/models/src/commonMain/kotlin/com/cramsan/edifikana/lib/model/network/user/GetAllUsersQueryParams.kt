package com.cramsan.edifikana.lib.model.network.user

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Data class representing the query parameters for fetching all users.
 *
 * @property orgId Optional organization ID to filter users by organization.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Query parameters for listing users, requiring an organization id.")
data class GetAllUsersQueryParams(
    @JsonSchema.Description("Identifier of the organization to list users for.")
    val orgId: OrganizationId,
) : QueryParam
