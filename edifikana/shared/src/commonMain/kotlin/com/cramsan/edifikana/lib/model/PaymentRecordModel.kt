package com.cramsan.edifikana.lib.model

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
    val orgId: OrganizationId,
    val paymentType: PaymentType,
    val periodMonth: Long,
    val amountDue: Double?,
    val amountPaid: Double?,
    val status: PaymentStatus,
    val dueDate: Long?,
    val paidDate: Long?,
    val recordedBy: UserId?,
    val recordedAt: Long,
    val notes: String?,
)
