package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.OrganizationId

/**
 * Request to get an organization by ID.
 */
data class GetOrganizationRequest(
    val id: OrganizationId,
)
