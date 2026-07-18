package com.cramsan.edifikana.lib.model.network.payment

import com.cramsan.edifikana.lib.model.payment.PaymentType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new payment record.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to create a new payment record for a unit.")
data class CreatePaymentRecordNetworkRequest(
    @SerialName("unit_id")
    @JsonSchema.Description("Identifier of the unit the payment record belongs to.")
    val unitId: UnitId,
    @SerialName("payment_type")
    @JsonSchema.Description("Type of the payment record.")
    val paymentType: PaymentType,
    @SerialName("period_month")
    @JsonSchema.Description("First day of the month the payment covers.")
    @JsonSchema.Format("date")
    val periodMonth: LocalDate,
    @SerialName("amount_due")
    @JsonSchema.Description("Amount due for this period.")
    @JsonSchema.Minimum(0.0)
    val amountDue: Double?,
    @SerialName("due_date")
    @JsonSchema.Description("Date the payment is due.")
    @JsonSchema.Format("date")
    val dueDate: LocalDate?,
    @JsonSchema.Description("Freeform notes about the payment record.")
    val notes: String?,
) : RequestBody
