package com.cramsan.edifikana.lib.model.network.payment

import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for listing payment records.
 */
@NetworkModel
@Serializable
data class GetPaymentRecordsQueryParams(
    @SerialName("unit_id") val unitId: UnitId,
    @SerialName("period_month") val periodMonth: String? = null,
) : QueryParam
