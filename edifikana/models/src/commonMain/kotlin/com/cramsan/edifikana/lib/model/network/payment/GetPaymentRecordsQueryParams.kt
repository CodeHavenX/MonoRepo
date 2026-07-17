package com.cramsan.edifikana.lib.model.network.payment

import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
import kotlinx.datetime.LocalDate
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
    @JsonSchema.Description("Optional month (first day of month, e.g. \"2026-07-01\") to filter payment records by.")
    @JsonSchema.Format("date")
    val periodMonth: LocalDate? = null,
) : QueryParam
