package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.UserId

/**
 * Request to create an organization.
 */
data class CreateOrganizationRequest(
    val owner: UserId,
)
