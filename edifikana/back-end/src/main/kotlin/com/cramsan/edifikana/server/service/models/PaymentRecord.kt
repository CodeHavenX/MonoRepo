package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.edifikana.lib.model.payment.PaymentStatus
import com.cramsan.edifikana.lib.model.payment.PaymentType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing a payment record for a unit.
 *
 * Monetary amounts ([amountDue], [amountPaid]) are stored in the smallest currency unit
 * (e.g. cents for USD: $12.34 → 1234). The display layer is responsible for formatting
 * these values with the appropriate decimal precision.
 */
@OptIn(ExperimentalTime::class)
data class PaymentRecord(
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
    val recordedAt: Instant,
    val notes: String?,
)
