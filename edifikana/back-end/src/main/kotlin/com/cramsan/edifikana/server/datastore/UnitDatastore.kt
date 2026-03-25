package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.server.service.models.Unit

/**
 * Interface for the unit datastore.
 */
interface UnitDatastore {

    /**
     * Creates a new unit record. Returns the [Result] of the operation with the created [Unit].
     */
    suspend fun createUnit(
        propertyId: PropertyId,
        orgId: OrganizationId,
        unitNumber: String,
        bedrooms: Int?,
        bathrooms: Int?,
        sqFt: Int?,
        floor: Int?,
        notes: String?,
    ): Result<Unit>

    /**
     * Retrieves a unit by [unitId]. Returns [Result] with the [Unit] if found.
     */
    suspend fun getUnit(unitId: UnitId): Result<Unit?>

    /**
     * Retrieves all non-deleted units for [orgId], optionally filtered by [propertyId].
     */
    suspend fun getUnits(
        orgId: OrganizationId,
        propertyId: PropertyId?,
    ): Result<List<Unit>>

    /**
     * Updates the fields of an existing unit. Returns the updated [Unit].
     */
    suspend fun updateUnit(
        unitId: UnitId,
        unitNumber: String?,
        bedrooms: Int?,
        bathrooms: Int?,
        sqFt: Int?,
        floor: Int?,
        notes: String?,
    ): Result<Unit>

    /**
     * Soft-deletes the unit with the given [unitId]. Returns true if the record was deleted.
     */
    suspend fun deleteUnit(unitId: UnitId): Result<Boolean>
}
