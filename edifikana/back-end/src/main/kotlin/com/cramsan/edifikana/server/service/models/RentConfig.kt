package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.RentConfigId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId

/**
 * Domain model representing a rent configuration for a unit.
 */
data class RentConfig(
    val id: RentConfigId,
    val unitId: UnitId,
    val orgId: OrganizationId,
    val monthlyAmount: Long,
    val dueDay: Int,
    val currency: String,
    val updatedAt: Long,
    val updatedBy: UserId?,
    val createdAt: Long,
)
