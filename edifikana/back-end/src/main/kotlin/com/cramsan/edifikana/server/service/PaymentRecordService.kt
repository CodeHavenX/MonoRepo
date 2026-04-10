package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.edifikana.lib.model.payment.PaymentStatus
import com.cramsan.edifikana.lib.model.payment.PaymentType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.datastore.PaymentRecordDatastore
import com.cramsan.edifikana.server.service.models.PaymentRecord
import com.cramsan.framework.logging.logD
import kotlinx.datetime.LocalDate

/**
 * Service for managing payment records. Delegates persistence to [PaymentRecordDatastore].
 */
class PaymentRecordService(
    private val paymentRecordDatastore: PaymentRecordDatastore,
) {

    /**
     * Creates a new payment record.
     */
    suspend fun createPaymentRecord(
        unitId: UnitId,
        paymentType: PaymentType,
        periodMonth: LocalDate,
        amountDue: Long?,
        dueDate: LocalDate?,
        recordedBy: UserId?,
        notes: String?,
    ): PaymentRecord {
        logD(TAG, "createPaymentRecord")
        return paymentRecordDatastore.createPaymentRecord(
            unitId = unitId,
            paymentType = paymentType,
            periodMonth = periodMonth,
            amountDue = amountDue,
            dueDate = dueDate,
            recordedBy = recordedBy,
            notes = notes,
        ).getOrThrow()
    }

    /**
     * Retrieves a single payment record by [paymentRecordId]. Returns null if not found.
     */
    suspend fun getPaymentRecord(paymentRecordId: PaymentRecordId): PaymentRecord? {
        logD(TAG, "getPaymentRecord")
        return paymentRecordDatastore.getPaymentRecord(paymentRecordId).getOrNull()
    }

    /**
     * Lists payment records for the given [unitId], optionally filtered by [periodMonth].
     *
     * [periodMonth] is a string in "YYYY-MM" format (e.g. "2026-03").
     */
    suspend fun listPaymentRecords(
        unitId: UnitId,
        periodMonth: String?,
    ): List<PaymentRecord> {
        logD(TAG, "listPaymentRecords")
        return paymentRecordDatastore.listPaymentRecords(
            unitId = unitId,
            periodMonth = periodMonth,
        ).getOrThrow()
    }

    /**
     * Updates an existing payment record. Returns the updated [PaymentRecord].
     */
    suspend fun updatePaymentRecord(
        paymentRecordId: PaymentRecordId,
        amountPaid: Long?,
        paidDate: LocalDate?,
        status: PaymentStatus?,
        notes: String?,
    ): PaymentRecord {
        logD(TAG, "updatePaymentRecord")
        return paymentRecordDatastore.updatePaymentRecord(
            paymentRecordId = paymentRecordId,
            amountPaid = amountPaid,
            paidDate = paidDate,
            status = status,
            notes = notes,
        ).getOrThrow()
    }

    companion object {
        private const val TAG = "PaymentRecordService"
    }
}
