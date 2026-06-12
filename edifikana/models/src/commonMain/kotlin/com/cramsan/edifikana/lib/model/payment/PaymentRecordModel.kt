package com.cramsan.edifikana.lib.model.payment

import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import kotlinx.datetime.LocalDate

/**
 * Domain model representing a payment record for a unit.
 *
 * Timestamp fields are represented as epoch seconds (Long) to avoid exposing
 * the experimental [kotlin.time.Instant] type in the public API.
 * Date fields (periodMonth, dueDate, paidDate) are represented as epoch seconds (Long)
 * truncated to day precision (midnight UTC).
 */
data class PaymentRecordModel(
    val id: PaymentRecordId,
    val unitId: UnitId,
    val paymentType: PaymentType,
    val periodMonth: LocalDate,
    val amountDue: Double?,
    val amountPaid: Double?,
    val status: PaymentStatus,
    val dueDate: LocalDate?,
    val paidDate: LocalDate?,
    val recordedBy: UserId?,
    val recordedAt: Long,
    val notes: String?,
)
