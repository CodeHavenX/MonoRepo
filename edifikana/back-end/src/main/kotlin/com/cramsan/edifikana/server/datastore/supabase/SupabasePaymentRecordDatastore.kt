package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PaymentRecordId
import com.cramsan.edifikana.lib.model.PaymentStatus
import com.cramsan.edifikana.lib.model.PaymentType
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.PaymentRecordDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.PaymentRecordEntity
import com.cramsan.edifikana.server.service.models.PaymentRecord
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.datetime.LocalDate
import kotlin.time.Clock

/**
 * Datastore for managing payment records using Supabase.
 */
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
    ): Result<PaymentRecord> = runSuspendCatching(TAG) {
        logD(TAG, "Creating payment record for unit: %s", unitId)
        val created = postgrest.from(PaymentRecordEntity.COLLECTION).insert(
            PaymentRecordEntity.CreatePaymentRecordEntity(
                unitId = unitId,
                orgId = orgId,
                paymentType = paymentType.name,
                periodMonth = periodMonth,
                amountDue = amountDue?.toDouble(),
                amountPaid = amountPaid?.toDouble(),
                status = status.name,
                dueDate = dueDate,
                paidDate = paidDate,
                recordedBy = recordedBy,
                notes = notes,
            )
        ) {
            select()
        }.decodeSingle<PaymentRecordEntity>()
        logD(TAG, "Payment record created: %s", created.paymentRecordId)
        created.toPaymentRecord()
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
     * Lists all non-deleted payment records for [unitId], with optional [periodMonth] filter.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getPaymentRecords(
        unitId: UnitId,
        periodMonth: LocalDate?,
    ): Result<List<PaymentRecord>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting payment records for unit: %s", unitId)
        postgrest.from(PaymentRecordEntity.COLLECTION).select {
            filter {
                PaymentRecordEntity::unitId eq unitId.unitId
                PaymentRecordEntity::deletedAt isExact null
                periodMonth?.let { PaymentRecordEntity::periodMonth eq it }
            }
        }.decodeList<PaymentRecordEntity>().map { it.toPaymentRecord() }
    }

    /**
     * Updates the specified fields of an existing payment record. Null parameters leave existing values unchanged.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updatePaymentRecord(
        paymentRecordId: PaymentRecordId,
        paymentType: PaymentType?,
        amountDue: Long?,
        amountPaid: Long?,
        status: PaymentStatus?,
        dueDate: LocalDate?,
        paidDate: LocalDate?,
        notes: String?,
    ): Result<PaymentRecord> = runSuspendCatching(TAG) {
        logD(TAG, "Updating payment record: %s", paymentRecordId)
        postgrest.from(PaymentRecordEntity.COLLECTION).update({
            paymentType?.let { value -> PaymentRecordEntity::paymentType setTo value.name }
            amountDue?.let { value -> PaymentRecordEntity::amountDue setTo value.toDouble() }
            amountPaid?.let { value -> PaymentRecordEntity::amountPaid setTo value.toDouble() }
            status?.let { value -> PaymentRecordEntity::status setTo value.name }
            dueDate?.let { value -> PaymentRecordEntity::dueDate setTo value }
            paidDate?.let { value -> PaymentRecordEntity::paidDate setTo value }
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
     * Soft-deletes a payment record. Returns true if the record was found and updated.
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
     * Hard-deletes a previously soft-deleted payment record. Used only for test teardown.
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
