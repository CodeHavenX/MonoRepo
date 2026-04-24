package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.occupant.OccupancyStatus
import com.cramsan.edifikana.lib.model.occupant.OccupantId
import com.cramsan.edifikana.lib.model.occupant.OccupantType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.datastore.OccupantDatastore
import com.cramsan.edifikana.server.datastore.UnitDatastore
import com.cramsan.edifikana.server.service.models.Occupant
import com.cramsan.framework.logging.logD
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.ConflictException
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Service for managing unit occupants. Delegates persistence to [OccupantDatastore].
 */
@OptIn(ExperimentalTime::class)
class OccupantService(
    private val occupantDatastore: OccupantDatastore,
    private val unitDatastore: UnitDatastore,
    private val clock: Clock,
) {

    /**
     * Adds a new occupant to a unit.
     *
     * If [isPrimary] is true, clears the primary flag on all other active occupants for the unit first.
     * The org_id is resolved automatically from the unit record.
     */
    suspend fun addOccupant(
        unitId: UnitId,
        userId: UserId?,
        addedBy: UserId?,
        name: String,
        email: String?,
        occupantType: OccupantType,
        isPrimary: Boolean,
        startDate: LocalDate,
        endDate: LocalDate?,
    ): Occupant {
        logD(TAG, "addOccupant")
        val unit = unitDatastore.getUnit(unitId).getOrThrow()
            ?: throw NoSuchElementException("Unit not found: $unitId")
        if (isPrimary) {
            occupantDatastore.clearPrimaryForUnit(unitId).getOrThrow()
        }
        return occupantDatastore.createOccupant(
            unitId = unitId,
            orgId = unit.orgId,
            userId = userId,
            addedBy = addedBy,
            name = name,
            email = email,
            occupantType = occupantType,
            isPrimary = isPrimary,
            startDate = startDate,
            endDate = endDate,
        ).getOrThrow()
    }

    /**
     * Retrieves a single occupant by [occupantId]. Returns null if not found.
     */
    suspend fun getOccupant(occupantId: OccupantId): Occupant? {
        logD(TAG, "getOccupant")
        return occupantDatastore.getOccupant(occupantId).getOrThrow()
    }

    /**
     * Lists occupants for [unitId]. Returns active occupants only unless [includeInactive] is true.
     */
    suspend fun listOccupantsForUnit(
        unitId: UnitId,
        includeInactive: Boolean,
    ): List<Occupant> {
        logD(TAG, "listOccupantsForUnit")
        return occupantDatastore.listOccupantsForUnit(unitId, includeInactive).getOrThrow()
    }

    /**
     * Updates an existing occupant.
     *
     * If [isPrimary] is being set to true, clears the primary flag on all other active occupants first.
     * If [isPrimary] is being set to false and this is the only active occupant, rejects with [ConflictException].
     */
    suspend fun updateOccupant(
        occupantId: OccupantId,
        occupantType: OccupantType?,
        isPrimary: Boolean?,
        endDate: LocalDate?,
        status: OccupancyStatus?,
    ): Occupant {
        logD(TAG, "updateOccupant")
        val existing = occupantDatastore.getOccupant(occupantId).getOrThrow()
            ?: throw NoSuchElementException("Occupant not found: $occupantId")

        if (isPrimary == true) {
            occupantDatastore.clearPrimaryForUnit(existing.unitId).getOrThrow()
        } else if (isPrimary == false && existing.isPrimary) {
            val activeOccupants = occupantDatastore.listOccupantsForUnit(
                unitId = existing.unitId,
                includeInactive = false,
            ).getOrThrow()
            if (activeOccupants.size == 1) {
                throw ConflictException(
                    "Cannot unset primary on the only active occupant. Designate a new primary first.",
                )
            }
        }

        return occupantDatastore.updateOccupant(
            occupantId = occupantId,
            occupantType = occupantType,
            isPrimary = isPrimary,
            endDate = endDate,
            status = status,
        ).getOrThrow()
    }

    /**
     * Soft-removes an occupant: sets status to INACTIVE and end_date to today.
     *
     * Rejects with [ConflictException] if the occupant is the primary and other active occupants exist for the unit —
     * the caller must designate a new primary first.
     */
    suspend fun removeOccupant(occupantId: OccupantId): Occupant {
        logD(TAG, "removeOccupant")
        val existing = occupantDatastore.getOccupant(occupantId).getOrThrow()
            ?: throw NoSuchElementException("Occupant not found: $occupantId")

        if (existing.isPrimary) {
            val activeOccupants = occupantDatastore.listOccupantsForUnit(
                unitId = existing.unitId,
                includeInactive = false,
            ).getOrThrow()
            val otherActive = activeOccupants.filter { it.id != occupantId }
            if (otherActive.isNotEmpty()) {
                throw ConflictException(
                    "Cannot remove the primary occupant while other active occupants exist. " +
                        "Designate a new primary first.",
                )
            }
        }

        val today = clock.now().toLocalDateTime(TimeZone.UTC).date
        return occupantDatastore.softRemoveOccupant(occupantId, today).getOrThrow()
    }

    companion object {
        private const val TAG = "OccupantService"
    }
}
