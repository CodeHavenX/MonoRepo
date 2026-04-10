@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.lib.model.network.payment

import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.edifikana.lib.model.payment.PaymentStatus
import com.cramsan.edifikana.lib.model.payment.PaymentType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Network response for a single payment record.
 */
@NetworkModel
@Serializable
data class PaymentRecordNetworkResponse(
    @SerialName("payment_record_id") val paymentRecordId: PaymentRecordId,
    @SerialName("unit_id") val unitId: UnitId,
    @SerialName("payment_type") val paymentType: PaymentType,
    @SerialName("period_month") val periodMonth: LocalDate,
    @SerialName("amount_due") val amountDue: Long?,
    @SerialName("amount_paid") val amountPaid: Long?,
    val status: PaymentStatus,
    @SerialName("due_date") val dueDate: LocalDate?,
    @SerialName("paid_date") val paidDate: LocalDate?,
    @SerialName("recorded_by") val recordedBy: UserId?,
    @SerialName("recorded_at") val recordedAt: Instant,
    val notes: String?,
) : ResponseBody
