package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PaymentRecordId
import com.cramsan.edifikana.lib.model.PaymentStatus
import com.cramsan.edifikana.lib.model.PaymentType
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import kotlinx.datetime.LocalDate

/**
 * Domain model representing a payment record for a unit.
 */
data class PaymentRecord(
    val id: PaymentRecordId,
    val unitId: UnitId,
    val orgId: OrganizationId,
    val paymentType: PaymentType,
    val periodMonth: LocalDate,
    val amountDue: Long?,
    val amountPaid: Long?,
    val status: PaymentStatus,
    val dueDate: LocalDate?,
    val paidDate: LocalDate?,
    val recordedBy: UserId?,
    val recordedAt: Long,
    val notes: String?,
)
