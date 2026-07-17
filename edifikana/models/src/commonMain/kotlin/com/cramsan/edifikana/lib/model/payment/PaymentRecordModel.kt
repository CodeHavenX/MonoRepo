package com.cramsan.edifikana.lib.model.payment

import com.cramsan.edifikana.lib.model.common.MonetaryAmount
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

/**
 * Domain model representing a payment record for a unit.
 */
data class PaymentRecordModel(
    val id: PaymentRecordId,
    val unitId: UnitId,
    val paymentType: PaymentType,
    val periodMonth: LocalDate,
    val amountDue: MonetaryAmount?,
    val amountPaid: MonetaryAmount?,
    val status: PaymentStatus,
    val dueDate: LocalDate?,
    val paidDate: LocalDate?,
    val recordedBy: UserId?,
    val recordedAt: Instant,
    val notes: String?,
)
