package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Entity representing a payment record stored in the database.
 */
@Serializable
@SupabaseModel
data class PaymentRecordEntity(
    @SerialName("payment_record_id")
    val paymentRecordId: String,
    @SerialName("unit_id")
    val unitId: UnitId,
    @SerialName("org_id")
    val orgId: OrganizationId,
    @SerialName("payment_type")
    val paymentType: String,
    @SerialName("period_month")
    val periodMonth: LocalDate,
    @SerialName("amount_due")
    val amountDue: Double? = null,
    @SerialName("amount_paid")
    val amountPaid: Double? = null,
    val status: String,
    @SerialName("due_date")
    val dueDate: LocalDate? = null,
    @SerialName("paid_date")
    val paidDate: LocalDate? = null,
    @SerialName("recorded_by")
    val recordedBy: UserId? = null,
    @SerialName("recorded_at")
    val recordedAt: Instant,
    val notes: String? = null,
    @SerialName("deleted_at")
    val deletedAt: Instant? = null,
) {
    companion object {
        const val COLLECTION = "payment_records"
    }

    /**
     * Entity representing a new payment record to be inserted.
     */
    @Serializable
    @SupabaseModel
    data class CreatePaymentRecordEntity(
        @SerialName("unit_id")
        val unitId: UnitId,
        @SerialName("org_id")
        val orgId: OrganizationId,
        @SerialName("payment_type")
        val paymentType: String,
        @SerialName("period_month")
        val periodMonth: LocalDate,
        @SerialName("amount_due")
        val amountDue: Double? = null,
        @SerialName("amount_paid")
        val amountPaid: Double? = null,
        val status: String,
        @SerialName("due_date")
        val dueDate: LocalDate? = null,
        @SerialName("paid_date")
        val paidDate: LocalDate? = null,
        @SerialName("recorded_by")
        val recordedBy: UserId? = null,
        val notes: String? = null,
    )
}
