package com.cramsan.edifikana.lib.model.network.rent

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create or update the rent configuration for a unit.
 */
@NetworkModel
@Serializable
data class RentConfigNetworkRequest(
    @SerialName("monthly_amount") val monthlyAmount: Long,
    @SerialName("due_day") val dueDay: Int,
    val currency: String,
) : RequestBody
