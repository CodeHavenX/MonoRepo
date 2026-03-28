package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PaymentRecordId
import com.cramsan.edifikana.lib.model.PaymentStatus
import com.cramsan.edifikana.lib.model.PaymentType
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.service.models.PaymentRecord
import kotlinx.datetime.LocalDate

/**
 * Interface for the payment record datastore.
 */
interface PaymentRecordDatastore {

    /**
     * Creates a new payment record. Returns the [Result] of the operation with the created [PaymentRecord].
     */
    suspend fun createPaymentRecord(
        unitId: UnitId,
        orgId: OrganizationId,
        paymentType: PaymentType,
        periodMonth: LocalDate,
        amountDue: Long?,
        amountPaid: Long?,
        status: PaymentStatus,
        dueDate: LocalDate?,
        paidDate: LocalDate?,
        recordedBy: UserId?,
        notes: String?,
    ): Result<PaymentRecord>

    /**
     * Retrieves a single payment record by [paymentRecordId]. Returns [Result] with the [PaymentRecord] if found.
     */
    suspend fun getPaymentRecord(paymentRecordId: PaymentRecordId): Result<PaymentRecord?>

    /**
     * Retrieves all payment records for [unitId], optionally filtered by [periodMonth].
     */
    suspend fun getPaymentRecords(
        unitId: UnitId,
        periodMonth: LocalDate?,
    ): Result<List<PaymentRecord>>

    /**
     * Updates fields of an existing payment record. Null parameters leave the existing value unchanged.
     */
    suspend fun updatePaymentRecord(
        paymentRecordId: PaymentRecordId,
        paymentType: PaymentType?,
        amountDue: Long?,
        amountPaid: Long?,
        status: PaymentStatus?,
        dueDate: LocalDate?,
        paidDate: LocalDate?,
        notes: String?,
    ): Result<PaymentRecord>

    /**
     * Soft-deletes the payment record with the given [paymentRecordId]. Returns true if the record was deleted.
     */
    suspend fun deletePaymentRecord(paymentRecordId: PaymentRecordId): Result<Boolean>

    /**
     * Hard-deletes a previously soft-deleted payment record. Used only for test teardown.
     */
    suspend fun purgePaymentRecord(paymentRecordId: PaymentRecordId): Result<Boolean>
}
