package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.server.datastore.UnitDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.UnitEntity
import com.cramsan.edifikana.server.service.models.Unit
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.time.Clock

/**
 * Datastore for managing unit records using Supabase.
 */
class SupabaseUnitDatastore(
    private val postgrest: Postgrest,
    private val clock: Clock,
) : UnitDatastore {

    /**
     * Inserts a new unit row and returns the created [Unit].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createUnit(
        propertyId: PropertyId,
        orgId: OrganizationId,
        unitNumber: String,
        bedrooms: Int?,
        bathrooms: Int?,
        sqFt: Int?,
        floor: Int?,
        notes: String?,
    ): Result<Unit> = runSuspendCatching(TAG) {
        logD(TAG, "Creating unit: %s", unitNumber)
        val requestEntity = CreateUnitEntity(
            propertyId = propertyId,
            orgId = orgId,
            unitNumber = unitNumber,
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            sqFt = sqFt,
            floor = floor,
            notes = notes,
        )
        val created = postgrest.from(UnitEntity.COLLECTION).insert(requestEntity) {
            select()
        }.decodeSingle<UnitEntity>()
        logD(TAG, "Unit created: %s", created.unitId)
        created.toUnit()
    }

    /**
     * Retrieves a single unit by [unitId]. Returns null if not found or soft-deleted.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getUnit(unitId: UnitId): Result<Unit?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting unit: %s", unitId)
        postgrest.from(UnitEntity.COLLECTION).select {
            filter {
                UnitEntity::unitId eq unitId.unitId
                UnitEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<UnitEntity>()?.toUnit()
    }

    /**
     * Lists all non-deleted units for [orgId], with an optional [propertyId] filter.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getUnits(
        orgId: OrganizationId,
        propertyId: PropertyId?,
    ): Result<List<Unit>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting units for org: %s", orgId)
        postgrest.from(UnitEntity.COLLECTION).select {
            filter {
                UnitEntity::orgId eq orgId.id
                UnitEntity::deletedAt isExact null
                propertyId?.let { UnitEntity::propertyId eq it.propertyId }
            }
        }.decodeList<UnitEntity>().map { it.toUnit() }
    }

    /**
     * Updates the fields of an existing unit. Returns the updated [Unit].
     *
     * Null parameters are treated as "no change" — passing null for a field leaves the existing
     * database value intact. This means it is not possible to explicitly clear a nullable field
     * (e.g. set [notes] back to null) through this method. Callers that need to clear a field
     * must use a dedicated operation.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updateUnit(
        unitId: UnitId,
        unitNumber: String?,
        bedrooms: Int?,
        bathrooms: Int?,
        sqFt: Int?,
        floor: Int?,
        notes: String?,
    ): Result<Unit> = runSuspendCatching(TAG) {
        logD(TAG, "Updating unit: %s", unitId)
        postgrest.from(UnitEntity.COLLECTION).update({
            unitNumber?.let { value -> UnitEntity::unitNumber setTo value }
            bedrooms?.let { value -> UnitEntity::bedrooms setTo value }
            bathrooms?.let { value -> UnitEntity::bathrooms setTo value }
            sqFt?.let { value -> UnitEntity::sqFt setTo value }
            floor?.let { value -> UnitEntity::floor setTo value }
            notes?.let { value -> UnitEntity::notes setTo value }
        }) {
            select()
            filter {
                UnitEntity::unitId eq unitId.unitId
                UnitEntity::deletedAt isExact null
            }
        }.decodeSingle<UnitEntity>().toUnit()
    }

    /**
     * Soft-deletes a unit by setting [UnitEntity.deletedAt]. Returns true if the record was found and updated.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteUnit(unitId: UnitId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Soft deleting unit: %s", unitId)
        postgrest.from(UnitEntity.COLLECTION).update({
            UnitEntity::deletedAt setTo clock.now()
        }) {
            select()
            filter {
                UnitEntity::unitId eq unitId.unitId
                UnitEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<UnitEntity>() != null
    }

    /**
     * Permanently deletes a soft-deleted unit by [unitId]. Returns true if the record was purged.
     * Only purges records that are already soft-deleted (deletedAt is not null).
     */
    @OptIn(SupabaseModel::class)
    override suspend fun purgeUnit(unitId: UnitId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Purging soft-deleted unit: %s", unitId)

        val entity = postgrest.from(UnitEntity.COLLECTION).select {
            filter {
                UnitEntity::unitId eq unitId.unitId
            }
        }.decodeSingleOrNull<UnitEntity>()

        if (entity?.deletedAt == null) {
            return@runSuspendCatching false
        }

        postgrest.from(UnitEntity.COLLECTION).delete {
            filter {
                UnitEntity::unitId eq unitId.unitId
            }
        }
        true
    }

    companion object {
        const val TAG = "SupabaseUnitDatastore"
    }
}
