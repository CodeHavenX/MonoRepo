package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.OrganizationId

/**
 * Request to get a list of users for the organization ID.
 */
data class GetOrganizationUsersRequest(
    val id: OrganizationId,
)
