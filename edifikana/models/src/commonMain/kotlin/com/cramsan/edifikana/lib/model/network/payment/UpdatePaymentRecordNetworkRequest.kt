package com.cramsan.edifikana.lib.model.network.payment

import com.cramsan.edifikana.lib.model.payment.PaymentStatus
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to update an existing payment record. Only provided (non-null) fields are updated. It is not possible to explicitly clear a nullable field (e.g. reset [notes] to
 * null) through this request.
 */
@NetworkModel
@Serializable
data class UpdatePaymentRecordNetworkRequest(
    @SerialName("amount_paid") val amountPaid: Double?,
    @SerialName("paid_date") val paidDate: LocalDate?,
    val status: PaymentStatus?,
    val notes: String?,
) : RequestBody
