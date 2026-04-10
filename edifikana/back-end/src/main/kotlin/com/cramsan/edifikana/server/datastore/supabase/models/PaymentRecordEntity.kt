package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Entity representing a payment record row in the `payment_records` Supabase table.
 *
 * [paymentType] and [status] are stored as uppercase strings matching the DB enum values.
 * Monetary amounts are stored in the smallest currency unit (e.g. cents for USD).
 */
@OptIn(ExperimentalTime::class)
@Serializable
@SupabaseModel
data class PaymentRecordEntity(
    @SerialName("payment_record_id")
    val paymentRecordId: PaymentRecordId,
    @SerialName("unit_id")
    val unitId: UnitId,
    @SerialName("payment_type")
    val paymentType: String,
    @SerialName("period_month")
    val periodMonth: LocalDate,
    @SerialName("amount_due")
    val amountDue: Long?,
    @SerialName("amount_paid")
    val amountPaid: Long?,
    val status: String,
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
    @SupabaseModel
    data class CreatePaymentRecordEntity(
        @SerialName("unit_id")
        val unitId: UnitId,
        @SerialName("payment_type")
        val paymentType: String,
        @SerialName("period_month")
        val periodMonth: LocalDate,
        @SerialName("amount_due")
        val amountDue: Long?,
        @SerialName("due_date")
        val dueDate: LocalDate?,
        @SerialName("recorded_by")
        val recordedBy: UserId?,
        val notes: String?,
    )
}
