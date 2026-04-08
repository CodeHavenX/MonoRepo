package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.commonArea.CommonAreaId
import com.cramsan.edifikana.lib.model.commonArea.CommonAreaType
import com.cramsan.edifikana.lib.model.property.PropertyId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing a common area within a property (e.g. Lobby, Pool, Gym).
 */
@OptIn(ExperimentalTime::class)
data class CommonArea(
    val id: CommonAreaId,
    val propertyId: PropertyId,
    val name: String,
    val type: CommonAreaType,
    val description: String?,
    val createdAt: Instant,
)
