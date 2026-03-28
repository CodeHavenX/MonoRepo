package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.RentConfigId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
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
     * Creates or updates the rent configuration for [unitId].
     * If an active configuration already exists for the unit it is updated; otherwise a new row is inserted.
     */
    suspend fun upsertRentConfig(
        unitId: UnitId,
        orgId: OrganizationId,
        monthlyAmount: Long,
        dueDay: Int,
        currency: String,
        updatedBy: UserId?,
    ): Result<RentConfig>

    /**
     * Hard-deletes a rent configuration row by [rentConfigId]. Used only for test teardown.
     */
    suspend fun purgeRentConfig(rentConfigId: RentConfigId): Result<Boolean>
}
