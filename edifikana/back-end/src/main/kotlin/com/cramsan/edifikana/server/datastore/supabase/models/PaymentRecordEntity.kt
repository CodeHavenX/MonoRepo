package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.common.MonetaryAmount
import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.edifikana.lib.model.payment.PaymentStatus
import com.cramsan.edifikana.lib.model.payment.PaymentType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.DatabaseModel
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Entity representing a payment record row in the `payment_records` Supabase table.
 */
@OptIn(ExperimentalTime::class)
@Serializable
@DatabaseModel
data class PaymentRecordEntity(
    @SerialName("payment_record_id")
    val paymentRecordId: PaymentRecordId,
    @SerialName("unit_id")
    val unitId: UnitId,
    @SerialName("payment_type")
    val paymentType: PaymentType,
    @SerialName("period_month")
    val periodMonth: LocalDate,
    @SerialName("amount_due")
    val amountDue: MonetaryAmount?,
    @SerialName("amount_paid")
    val amountPaid: MonetaryAmount?,
    val status: PaymentStatus,
    @SerialName("due_date")
    val dueDate: LocalDate?,
    @SerialName("paid_date")
    val paidDate: LocalDate?,
    @SerialName("recorded_by")
    val recordedBy: UserId?,
    @SerialName("recorded_at")
    val recordedAt: Instant,
    val notes: String?,
    @SerialName("deleted_at")
    val deletedAt: Instant?,
) {
    companion object {
        const val COLLECTION = "payment_records"
    }

    /**
     * Entity used when inserting a new payment record. Omits auto-generated fields
     * (recorded_at, deleted_at) and server-managed fields (amount_paid, paid_date, status).
     */
    @Serializable
    @DatabaseModel
    data class CreatePaymentRecordEntity(
        @SerialName("unit_id")
        val unitId: UnitId,
        @SerialName("payment_type")
        val paymentType: PaymentType,
        @SerialName("period_month")
        val periodMonth: LocalDate,
        @SerialName("amount_due")
        val amountDue: MonetaryAmount?,
        @SerialName("due_date")
        val dueDate: LocalDate?,
        @SerialName("recorded_by")
        val recordedBy: UserId?,
        val notes: String?,
    )
}
