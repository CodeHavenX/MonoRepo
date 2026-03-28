package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for listing payment records.
 */
@NetworkModel
@Serializable
data class GetPaymentRecordsQueryParams(
    @SerialName("unit_id") val unitId: UnitId,
    @SerialName("org_id") val orgId: OrganizationId,
    @SerialName("period_month") val periodMonth: LocalDate? = null,
) : QueryParam
