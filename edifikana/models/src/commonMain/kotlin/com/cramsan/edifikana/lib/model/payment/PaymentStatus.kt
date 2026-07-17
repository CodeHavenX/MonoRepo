package com.cramsan.edifikana.lib.model.payment

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Represents the payment status of a payment record.
 */
@Serializable
@JsonSchema.Description("Payment status of a payment record.")
enum class PaymentStatus {
    PENDING,
    PARTIAL,
    PAID,
    OVERDUE,
}
