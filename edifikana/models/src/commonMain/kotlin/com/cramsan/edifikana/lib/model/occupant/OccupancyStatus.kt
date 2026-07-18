package com.cramsan.edifikana.lib.model.occupant

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Domain model representing the occupancy status of a unit occupant record.
 */
@Serializable
@JsonSchema.Description("Occupancy status of a unit occupant record.")
enum class OccupancyStatus {
    ACTIVE,
    INACTIVE,
}
