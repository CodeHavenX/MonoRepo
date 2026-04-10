package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
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
     * Retrieves all non-deleted units for [orgId].
     */
    suspend fun getUnits(
        orgId: OrganizationId,
    ): Result<List<Unit>>

    /**
     * Retrieves all non-deleted units for [propertyId].
     */
    suspend fun getUnits(
        propertyId: PropertyId,
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

    /**
     * Permanently deletes a soft-deleted unit record by [unitId].
     * Only purges if the record is already soft-deleted.
     * This is intended for testing and maintenance purposes only.
     * Returns the [Result] of the operation with a [Boolean] indicating if the record was purged.
     */
    suspend fun purgeUnit(unitId: UnitId): Result<Boolean>
}
