package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.OrganizationId

/**
 * Request to delete an organization by ID.
 */
data class DeleteOrganizationRequest(
    val id: OrganizationId,
)
