package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.PaymentStatus
import com.cramsan.edifikana.lib.model.PaymentType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to update an existing payment record.
 *
 * Null parameters are treated as "no change" — passing null for a field leaves the existing
 * database value intact.
 */
@NetworkModel
@Serializable
data class UpdatePaymentRecordNetworkRequest(
    @SerialName("payment_type") val paymentType: PaymentType? = null,
    @SerialName("amount_due") val amountDue: Long? = null,
    @SerialName("amount_paid") val amountPaid: Long? = null,
    val status: PaymentStatus? = null,
    @SerialName("due_date") val dueDate: LocalDate? = null,
    @SerialName("paid_date") val paidDate: LocalDate? = null,
    val notes: String? = null,
) : RequestBody
