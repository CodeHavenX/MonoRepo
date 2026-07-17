@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.lib.model.network.payment

import com.cramsan.edifikana.lib.model.common.MonetaryAmount
import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.edifikana.lib.model.payment.PaymentStatus
import com.cramsan.edifikana.lib.model.payment.PaymentType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
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
@JsonSchema.Description("A financial record tracking a rent or other payment for a unit.")
data class PaymentRecordNetworkResponse(
    @SerialName("payment_record_id")
    @JsonSchema.Description("Unique identifier of the payment record.")
    val paymentRecordId: PaymentRecordId,
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
    @JsonSchema.Description("Amount due for this period, or null if not set.")
    val amountDue: MonetaryAmount?,
    @SerialName("amount_paid")
    @JsonSchema.Description("Amount paid so far for this period, or null if nothing has been paid.")
    val amountPaid: MonetaryAmount?,
    @JsonSchema.Description("Current payment status of the record.")
    val status: PaymentStatus,
    @SerialName("due_date")
    @JsonSchema.Description("Date the payment is due, or null if not set.")
    @JsonSchema.Format("date")
    val dueDate: LocalDate?,
    @SerialName("paid_date")
    @JsonSchema.Description("Date the payment was made, or null if not yet paid.")
    @JsonSchema.Format("date")
    val paidDate: LocalDate?,
    @SerialName("recorded_by")
    @JsonSchema.Description("Identifier of the user who recorded this payment, or null if unknown.")
    val recordedBy: UserId?,
    @SerialName("recorded_at")
    @JsonSchema.Description("ISO-8601 timestamp when the payment record was created.")
    @JsonSchema.Format("date-time")
    val recordedAt: Instant,
    @JsonSchema.Description("Freeform notes about the payment record, or null if none.")
    val notes: String?,
) : ResponseBody
