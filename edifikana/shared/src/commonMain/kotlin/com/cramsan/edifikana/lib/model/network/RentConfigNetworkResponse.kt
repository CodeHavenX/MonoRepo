package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.RentConfigId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network response for a rent configuration record.
 */
@NetworkModel
@Serializable
data class RentConfigNetworkResponse(
    @SerialName("rent_config_id") val rentConfigId: RentConfigId,
    @SerialName("unit_id") val unitId: UnitId,
    @SerialName("org_id") val orgId: OrganizationId,
    @SerialName("monthly_amount") val monthlyAmount: Long,
    @SerialName("due_day") val dueDay: Int,
    val currency: String,
    @SerialName("updated_at") val updatedAt: Long,
    @SerialName("updated_by") val updatedBy: UserId?,
    @SerialName("created_at") val createdAt: Long,
) : ResponseBody
