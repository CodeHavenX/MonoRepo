package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
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
     * Creates or updates the rent configuration for [unitId]. Returns the created or updated [RentConfig].
     */
    suspend fun setRentConfig(
        unitId: UnitId,
        monthlyAmount: Long,
        dueDay: Int,
        currency: String,
        updatedBy: UserId?,
    ): RentConfig {
        logD(TAG, "setRentConfig")
        return rentConfigDatastore.setRentConfig(
            unitId = unitId,
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
