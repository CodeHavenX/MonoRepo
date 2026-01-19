package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.OrganizationId

/**
 * Domain model representing an organization.
 */
data class Organization(
    val id: OrganizationId,
    val name: String,
    val description: String,
)
