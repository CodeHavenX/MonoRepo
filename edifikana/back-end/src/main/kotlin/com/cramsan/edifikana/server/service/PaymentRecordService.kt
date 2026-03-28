package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PaymentRecordId
import com.cramsan.edifikana.lib.model.PaymentStatus
import com.cramsan.edifikana.lib.model.PaymentType
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
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
    ): PaymentRecord {
        logD(TAG, "createPaymentRecord")
        return paymentRecordDatastore.createPaymentRecord(
            unitId = unitId,
            orgId = orgId,
            paymentType = paymentType,
            periodMonth = periodMonth,
            amountDue = amountDue,
            amountPaid = amountPaid,
            status = status,
            dueDate = dueDate,
            paidDate = paidDate,
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
     * Lists all payment records for [unitId], optionally filtered by [periodMonth].
     */
    suspend fun getPaymentRecords(
        unitId: UnitId,
        periodMonth: LocalDate?,
    ): List<PaymentRecord> {
        logD(TAG, "getPaymentRecords")
        return paymentRecordDatastore.getPaymentRecords(
            unitId = unitId,
            periodMonth = periodMonth,
        ).getOrThrow()
    }

    /**
     * Updates fields of an existing payment record. Returns the updated [PaymentRecord].
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
    ): PaymentRecord {
        logD(TAG, "updatePaymentRecord")
        return paymentRecordDatastore.updatePaymentRecord(
            paymentRecordId = paymentRecordId,
            paymentType = paymentType,
            amountDue = amountDue,
            amountPaid = amountPaid,
            status = status,
            dueDate = dueDate,
            paidDate = paidDate,
            notes = notes,
        ).getOrThrow()
    }

    companion object {
        private const val TAG = "PaymentRecordService"
    }
}
