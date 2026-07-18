package com.cramsan.edifikana.lib.model.network.payment

import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for listing payment records.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Query parameters for listing payment records, requiring a unit id.")
data class GetPaymentRecordsQueryParams(
    @SerialName("unit_id")
    @JsonSchema.Description("Identifier of the unit to list payment records for.")
    val unitId: UnitId,
    @SerialName("period_month")
    @JsonSchema.Description("Optional month (e.g. \"2026-07\") to filter payment records by.")
    val periodMonth: String? = null,
) : QueryParam
