package com.cramsan.edifikana.lib.model.network.payment

import com.cramsan.edifikana.lib.model.common.MonetaryAmount
import com.cramsan.edifikana.lib.model.payment.PaymentStatus
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to update an existing payment record. Only provided (non-null) fields are updated. It is not possible to explicitly clear a nullable field (e.g. reset [notes] to
 * null) through this request.
 */
@NetworkModel
@Serializable
@JsonSchema.Description(
    "Request payload to update an existing payment record. Only provided (non-null) fields are updated; " +
        "it is not possible to explicitly clear a nullable field through this request.",
)
data class UpdatePaymentRecordNetworkRequest(
    @SerialName("amount_paid")
    @JsonSchema.Description("New amount paid, or null to leave unchanged.")
    val amountPaid: MonetaryAmount?,
    @SerialName("paid_date")
    @JsonSchema.Description("New date the payment was made, or null to leave unchanged.")
    @JsonSchema.Format("date")
    val paidDate: LocalDate?,
    @JsonSchema.Description("New payment status, or null to leave unchanged.")
    val status: PaymentStatus?,
    @JsonSchema.Description("New freeform notes, or null to leave unchanged.")
    val notes: String?,
) : RequestBody
