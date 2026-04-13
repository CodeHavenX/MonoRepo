package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.SupabaseModel
import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Entity representing a property unit stored in the database.
 */
@Serializable
@SupabaseModel
data class UnitEntity(
    @SerialName("unit_id")
    val unitId: UnitId,
    @SerialName("property_id")
    val propertyId: PropertyId,
    @SerialName("unit_number")
    val unitNumber: String,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    @SerialName("sq_ft")
    val sqFt: Int? = null,
    val floor: Int? = null,
    val notes: String? = null,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("deleted_at")
    val deletedAt: Instant? = null,
) {
    companion object {
        const val COLLECTION = "units"
    }

    /**
     * Entity representing a new unit to be inserted.
     */
    @Serializable
    @SupabaseModel
    data class CreateUnitEntity(
        @SerialName("property_id")
        val propertyId: PropertyId,
        @SerialName("unit_number")
        val unitNumber: String,
        val bedrooms: Int? = null,
        val bathrooms: Int? = null,
        @SerialName("sq_ft")
        val sqFt: Int? = null,
        val floor: Int? = null,
        val notes: String? = null,
    )
}
