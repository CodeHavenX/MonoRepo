package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.CommonAreaType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing a common area within a property (e.g. Lobby, Pool, Gym).
 */
@OptIn(ExperimentalTime::class)
data class CommonArea(
    val id: CommonAreaId,
    val propertyId: PropertyId,
    val orgId: OrganizationId,
    val name: String,
    val type: CommonAreaType,
    val description: String?,
    val createdAt: Instant,
)
