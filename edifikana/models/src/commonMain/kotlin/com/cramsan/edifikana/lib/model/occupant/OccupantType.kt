package com.cramsan.edifikana.lib.model.occupant

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Domain model representing occupancy type of a unit occupant record.
 */
@Serializable
@JsonSchema.Description("Occupancy type of a unit occupant record.")
enum class OccupantType {
    TENANT,
    RESIDENT,
}
