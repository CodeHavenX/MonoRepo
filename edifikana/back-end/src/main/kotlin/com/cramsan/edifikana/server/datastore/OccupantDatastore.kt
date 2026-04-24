package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.occupant.OccupancyStatus
import com.cramsan.edifikana.lib.model.occupant.OccupantId
import com.cramsan.edifikana.lib.model.occupant.OccupantType
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.service.models.Occupant
import kotlinx.datetime.LocalDate

/**
 * Interface for the occupant datastore.
 */
interface OccupantDatastore {

    /**
     * Creates a new occupant record. Returns the [Result] with the created [Occupant].
     */
    suspend fun createOccupant(
        unitId: UnitId,
        orgId: OrganizationId,
        userId: UserId?,
        addedBy: UserId?,
        name: String,
        email: String?,
        occupantType: OccupantType,
        isPrimary: Boolean,
        startDate: LocalDate,
        endDate: LocalDate?,
    ): Result<Occupant>

    /**
     * Retrieves an occupant by [occupantId]. Returns null if not found or soft-deleted.
     */
    suspend fun getOccupant(occupantId: OccupantId): Result<Occupant?>

    /**
     * Lists occupants for [unitId]. Returns active occupants only unless [includeInactive] is true.
     */
    suspend fun listOccupantsForUnit(
        unitId: UnitId,
        includeInactive: Boolean,
    ): Result<List<Occupant>>

    /**
     * Sets isPrimary to false on all active occupants for [unitId].
     * Called before inserting or updating an occupant with [isPrimary]=true.
     */
    suspend fun clearPrimaryForUnit(unitId: UnitId): Result<kotlin.Unit>

    /**
     * Updates an existing occupant. Only non-null fields are applied. Returns the updated [Occupant].
     */
    suspend fun updateOccupant(
        occupantId: OccupantId,
        occupantType: OccupantType?,
        isPrimary: Boolean?,
        endDate: LocalDate?,
        status: OccupancyStatus?,
    ): Result<Occupant>

    /**
     * Soft-removes an occupant by setting [status]=INACTIVE and [endDate] to [today].
     * The row is not deleted. Returns the updated [Occupant].
     */
    suspend fun softRemoveOccupant(
        occupantId: OccupantId,
        today: LocalDate,
    ): Result<Occupant>

    /**
     * Hard-deletes an occupant row. For integration test cleanup only.
     */
    suspend fun purgeOccupant(occupantId: OccupantId): Result<Boolean>
}
