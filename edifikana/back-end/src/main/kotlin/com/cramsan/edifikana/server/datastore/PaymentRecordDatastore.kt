package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.edifikana.lib.model.payment.PaymentStatus
import com.cramsan.edifikana.lib.model.payment.PaymentType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.service.models.PaymentRecord
import kotlinx.datetime.LocalDate

/**
 * Interface for the payment record datastore.
 */
interface PaymentRecordDatastore {

    /**
     * Creates a new payment record. Returns the [Result] with the created [PaymentRecord].
     */
    suspend fun createPaymentRecord(
        unitId: UnitId,
        paymentType: PaymentType,
        periodMonth: LocalDate,
        amountDue: Long?,
        dueDate: LocalDate?,
        recordedBy: UserId?,
        notes: String?,
    ): Result<PaymentRecord>

    /**
     * Retrieves a payment record by [paymentRecordId]. Returns null if not found or soft-deleted.
     */
    suspend fun getPaymentRecord(paymentRecordId: PaymentRecordId): Result<PaymentRecord?>

    /**
     * Retrieves all non-deleted payment records for the given [unitId], optionally filtered by [periodMonth].
     *
     * [periodMonth] is a string in "YYYY-MM" format. If null, all periods are returned.
     */
    suspend fun listPaymentRecords(
        unitId: UnitId,
        periodMonth: String?,
    ): Result<List<PaymentRecord>>

    /**
     * Updates an existing payment record. Only non-null fields are updated. Returns the updated [PaymentRecord].
     */
    suspend fun updatePaymentRecord(
        paymentRecordId: PaymentRecordId,
        amountPaid: Long?,
        paidDate: LocalDate?,
        status: PaymentStatus?,
        notes: String?,
    ): Result<PaymentRecord>

    /**
     * Soft-deletes the payment record with the given [paymentRecordId]. Returns true if the record was deleted.
     */
    suspend fun deletePaymentRecord(paymentRecordId: PaymentRecordId): Result<Boolean>

    /**
     * Hard-deletes the payment record with the given [paymentRecordId]. For integration test cleanup only.
     */
    suspend fun purgePaymentRecord(paymentRecordId: PaymentRecordId): Result<Boolean>
}
