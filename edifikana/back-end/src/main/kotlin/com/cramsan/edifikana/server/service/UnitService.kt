package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.server.datastore.UnitDatastore
import com.cramsan.edifikana.server.service.models.Unit
import com.cramsan.framework.logging.logD

/**
 * Service for managing property units. Delegates persistence to [UnitDatastore].
 */
class UnitService(
    private val unitDatastore: UnitDatastore,
) {

    /**
     * Creates a new unit record.
     */
    suspend fun createUnit(
        propertyId: PropertyId,
        unitNumber: String,
        bedrooms: Int?,
        bathrooms: Int?,
        sqFt: Int?,
        floor: Int?,
        notes: String?,
    ): Unit {
        logD(TAG, "createUnit")
        return unitDatastore.createUnit(
            propertyId = propertyId,
            unitNumber = unitNumber,
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            sqFt = sqFt,
            floor = floor,
            notes = notes,
        ).getOrThrow()
    }

    /**
     * Retrieves a single unit by [unitId]. Returns null if not found.
     */
    suspend fun getUnit(unitId: UnitId): Unit? {
        logD(TAG, "getUnit")
        return unitDatastore.getUnit(unitId).getOrNull()
    }

    /**
     * List all units for [propertyId]
     */
    suspend fun getUnits(
        propertyId: PropertyId,
    ): List<Unit> {
        logD(TAG, "getUnits by propertyId")
        return unitDatastore.getUnits(
            propertyId = propertyId,
        ).getOrThrow()
    }

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
    ): Unit {
        logD(TAG, "updateUnit")
        return unitDatastore.updateUnit(
            unitId = unitId,
            unitNumber = unitNumber,
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            sqFt = sqFt,
            floor = floor,
            notes = notes,
        ).getOrThrow()
    }

    /**
     * Soft-deletes a unit record. Returns true if the record was successfully deleted.
     */
    suspend fun deleteUnit(unitId: UnitId): Boolean {
        logD(TAG, "deleteUnit")
        return unitDatastore.deleteUnit(unitId).getOrThrow()
    }

    companion object {
        private const val TAG = "UnitService"
    }
}
