@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.lib.model.network.rent

import com.cramsan.edifikana.lib.model.rent.RentConfigId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Network response for a rent configuration.
 */
@NetworkModel
@Serializable
data class RentConfigNetworkResponse(
    @SerialName("rent_config_id") val rentConfigId: RentConfigId,
    @SerialName("unit_id") val unitId: UnitId,
    @SerialName("monthly_amount") val monthlyAmount: Long,
    @SerialName("due_day") val dueDay: Int,
    val currency: String,
    @SerialName("updated_at") val updatedAt: Instant,
    @SerialName("updated_by") val updatedBy: UserId?,
    @SerialName("created_at") val createdAt: Instant,
) : ResponseBody
