package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId

/**
 * Request to update an organization.
 */
data class UpdateOrganizationRequest(
    val id: OrganizationId,
    val owner: UserId,
)
