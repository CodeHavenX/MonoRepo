package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.RentConfigDatastore
import com.cramsan.edifikana.server.service.models.RentConfig
import com.cramsan.framework.logging.logD

/**
 * Service for managing rent configurations. Delegates persistence to [RentConfigDatastore].
 */
class RentConfigService(
    private val rentConfigDatastore: RentConfigDatastore,
) {

    /**
     * Retrieves the active rent configuration for [unitId]. Returns null if none exists.
     */
    suspend fun getRentConfig(unitId: UnitId): RentConfig? {
        logD(TAG, "getRentConfig")
        return rentConfigDatastore.getRentConfig(unitId).getOrNull()
    }

    /**
     * Creates or updates the rent configuration for [unitId]. Returns the saved [RentConfig].
     */
    suspend fun upsertRentConfig(
        unitId: UnitId,
        orgId: OrganizationId,
        monthlyAmount: Long,
        dueDay: Int,
        currency: String,
        updatedBy: UserId?,
    ): RentConfig {
        logD(TAG, "upsertRentConfig")
        return rentConfigDatastore.upsertRentConfig(
            unitId = unitId,
            orgId = orgId,
            monthlyAmount = monthlyAmount,
            dueDay = dueDay,
            currency = currency,
            updatedBy = updatedBy,
        ).getOrThrow()
    }

    companion object {
        private const val TAG = "RentConfigService"
    }
}
