package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.rent.RentConfigId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.service.models.RentConfig

/**
 * Interface for the rent configuration datastore.
 */
interface RentConfigDatastore {

    /**
     * Retrieves the active rent configuration for [unitId]. Returns null if none exists.
     */
    suspend fun getRentConfig(unitId: UnitId): Result<RentConfig?>

    /**
     * Creates or updates the rent configuration for [unitId] (upsert semantics).
     * Returns the [Result] with the created or updated [RentConfig].
     */
    suspend fun setRentConfig(
        unitId: UnitId,
        monthlyAmount: Long,
        dueDay: Int,
        currency: String,
        updatedBy: UserId?,
    ): Result<RentConfig>

    /**
     * Soft-deletes the rent configuration for [unitId]. Returns true if a record was deleted.
     */
    suspend fun deleteRentConfigByUnitId(unitId: UnitId): Result<Boolean>

    /**
     * Hard-deletes the rent configuration with [rentConfigId]. For integration test cleanup only.
     */
    suspend fun purgeRentConfig(rentConfigId: RentConfigId): Result<Boolean>
}
