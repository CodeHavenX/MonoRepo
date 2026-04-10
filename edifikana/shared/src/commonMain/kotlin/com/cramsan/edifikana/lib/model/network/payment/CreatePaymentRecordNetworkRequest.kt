package com.cramsan.edifikana.lib.model.network.payment

import com.cramsan.edifikana.lib.model.payment.PaymentType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new payment record.
 */
@NetworkModel
@Serializable
data class CreatePaymentRecordNetworkRequest(
    @SerialName("unit_id") val unitId: UnitId,
    @SerialName("payment_type") val paymentType: PaymentType,
    @SerialName("period_month") val periodMonth: LocalDate,
    @SerialName("amount_due") val amountDue: Long?,
    @SerialName("due_date") val dueDate: LocalDate?,
    val notes: String?,
) : RequestBody
