package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.Serializable

/**
 * Data class representing the query parameters for fetching all users.
 *
 * @property orgId Optional organization ID to filter users by organization.
 */
@NetworkModel
@Serializable
data class GetAllUsersQueryParams(
    val orgId: OrganizationId,
) : QueryParam
