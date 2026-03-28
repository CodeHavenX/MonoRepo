package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PaymentRecordId
import com.cramsan.edifikana.lib.model.PaymentStatus
import com.cramsan.edifikana.lib.model.PaymentType
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network response for a single payment record.
 */
@NetworkModel
@Serializable
data class PaymentRecordNetworkResponse(
    @SerialName("payment_record_id") val paymentRecordId: PaymentRecordId,
    @SerialName("unit_id") val unitId: UnitId,
    @SerialName("org_id") val orgId: OrganizationId,
    @SerialName("payment_type") val paymentType: PaymentType,
    @SerialName("period_month") val periodMonth: LocalDate,
    @SerialName("amount_due") val amountDue: Long?,
    @SerialName("amount_paid") val amountPaid: Long?,
    val status: PaymentStatus,
    @SerialName("due_date") val dueDate: LocalDate?,
    @SerialName("paid_date") val paidDate: LocalDate?,
    @SerialName("recorded_by") val recordedBy: UserId?,
    @SerialName("recorded_at") val recordedAt: Long,
    val notes: String?,
) : ResponseBody
