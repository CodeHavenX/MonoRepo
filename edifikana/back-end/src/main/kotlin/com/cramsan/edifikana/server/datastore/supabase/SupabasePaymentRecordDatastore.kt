package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.edifikana.lib.model.payment.PaymentStatus
import com.cramsan.edifikana.lib.model.payment.PaymentType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.datastore.PaymentRecordDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.PaymentRecordEntity
import com.cramsan.edifikana.server.datastore.supabase.models.PaymentRecordEntity.CreatePaymentRecordEntity
import com.cramsan.edifikana.server.service.models.PaymentRecord
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Supabase implementation of [PaymentRecordDatastore].
 */
@OptIn(ExperimentalTime::class)
class SupabasePaymentRecordDatastore(
    private val postgrest: Postgrest,
    private val clock: Clock,
) : PaymentRecordDatastore {

    /**
     * Inserts a new payment record row and returns the created [PaymentRecord].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createPaymentRecord(
        unitId: UnitId,
        paymentType: PaymentType,
        periodMonth: LocalDate,
        amountDue: Double?,
        dueDate: LocalDate?,
        recordedBy: UserId?,
        notes: String?,
    ): Result<PaymentRecord> = runSuspendCatching(TAG) {
        logD(TAG, "Creating payment record for unit: %s", unitId)
        val entity = CreatePaymentRecordEntity(
            unitId = unitId,
            paymentType = paymentType.name,
            periodMonth = periodMonth,
            amountDue = amountDue,
            dueDate = dueDate,
            recordedBy = recordedBy,
            notes = notes,
        )
        postgrest.from(PaymentRecordEntity.COLLECTION).insert(entity) {
            select()
        }.decodeSingle<PaymentRecordEntity>().toPaymentRecord()
    }

    /**
     * Retrieves a single payment record by [paymentRecordId]. Returns null if not found or soft-deleted.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getPaymentRecord(paymentRecordId: PaymentRecordId): Result<PaymentRecord?> =
        runSuspendCatching(TAG) {
            logD(TAG, "Getting payment record: %s", paymentRecordId)
            postgrest.from(PaymentRecordEntity.COLLECTION).select {
                filter {
                    PaymentRecordEntity::paymentRecordId eq paymentRecordId.paymentRecordId
                    PaymentRecordEntity::deletedAt isExact null
                }
            }.decodeSingleOrNull<PaymentRecordEntity>()?.toPaymentRecord()
        }

    /**
     * Lists all non-deleted payment records for the given [unitId], optionally filtered by [periodMonth].
     *
     * [periodMonth] is a string in "YYYY-MM" format (e.g. "2026-03"). If provided, only records
     * whose period_month starts with that prefix are returned.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun listPaymentRecords(
        unitId: UnitId,
        periodMonth: String?,
    ): Result<List<PaymentRecord>> = runSuspendCatching(TAG) {
        logD(TAG, "Listing payment records for unit: %s, period: %s", unitId, periodMonth)
        postgrest.from(PaymentRecordEntity.COLLECTION).select {
            filter {
                PaymentRecordEntity::unitId eq unitId.unitId
                PaymentRecordEntity::deletedAt isExact null
                periodMonth?.let { PaymentRecordEntity::periodMonth eq LocalDate.parse("$it-01") }
            }
        }.decodeList<PaymentRecordEntity>().map { it.toPaymentRecord() }
    }

    /**
     * Updates an existing payment record. Only non-null parameters are applied.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updatePaymentRecord(
        paymentRecordId: PaymentRecordId,
        amountPaid: Double?,
        paidDate: LocalDate?,
        status: PaymentStatus?,
        notes: String?,
    ): Result<PaymentRecord> = runSuspendCatching(TAG) {
        logD(TAG, "Updating payment record: %s", paymentRecordId)
        postgrest.from(PaymentRecordEntity.COLLECTION).update({
            amountPaid?.let { value -> PaymentRecordEntity::amountPaid setTo value }
            paidDate?.let { value -> PaymentRecordEntity::paidDate setTo value }
            status?.let { value -> PaymentRecordEntity::status setTo value.name }
            notes?.let { value -> PaymentRecordEntity::notes setTo value }
        }) {
            select()
            filter {
                PaymentRecordEntity::paymentRecordId eq paymentRecordId.paymentRecordId
                PaymentRecordEntity::deletedAt isExact null
            }
        }.decodeSingle<PaymentRecordEntity>().toPaymentRecord()
    }

    /**
     * Soft-deletes a payment record by setting [PaymentRecordEntity.deletedAt].
     * Returns true if the record was found and deleted.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deletePaymentRecord(paymentRecordId: PaymentRecordId): Result<Boolean> =
        runSuspendCatching(TAG) {
            logD(TAG, "Soft deleting payment record: %s", paymentRecordId)
            postgrest.from(PaymentRecordEntity.COLLECTION).update({
                PaymentRecordEntity::deletedAt setTo clock.now()
            }) {
                select()
                filter {
                    PaymentRecordEntity::paymentRecordId eq paymentRecordId.paymentRecordId
                    PaymentRecordEntity::deletedAt isExact null
                }
            }.decodeSingleOrNull<PaymentRecordEntity>() != null
        }

    /**
     * Hard-deletes a payment record row. For integration test cleanup only.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun purgePaymentRecord(paymentRecordId: PaymentRecordId): Result<Boolean> =
        runSuspendCatching(TAG) {
            logD(TAG, "Purging payment record: %s", paymentRecordId)
            postgrest.from(PaymentRecordEntity.COLLECTION).delete {
                select()
                filter {
                    PaymentRecordEntity::paymentRecordId eq paymentRecordId.paymentRecordId
                }
            }.decodeSingleOrNull<PaymentRecordEntity>() != null
        }

    companion object {
        const val TAG = "SupabasePaymentRecordDatastore"
    }
}
