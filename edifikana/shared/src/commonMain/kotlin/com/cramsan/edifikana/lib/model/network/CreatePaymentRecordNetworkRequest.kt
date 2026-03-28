package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PaymentStatus
import com.cramsan.edifikana.lib.model.PaymentType
import com.cramsan.edifikana.lib.model.UnitId
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
    @SerialName("org_id") val orgId: OrganizationId,
    @SerialName("payment_type") val paymentType: PaymentType,
    @SerialName("period_month") val periodMonth: LocalDate,
    @SerialName("amount_due") val amountDue: Long? = null,
    @SerialName("amount_paid") val amountPaid: Long? = null,
    val status: PaymentStatus = PaymentStatus.PENDING,
    @SerialName("due_date") val dueDate: LocalDate? = null,
    @SerialName("paid_date") val paidDate: LocalDate? = null,
    val notes: String? = null,
) : RequestBody
