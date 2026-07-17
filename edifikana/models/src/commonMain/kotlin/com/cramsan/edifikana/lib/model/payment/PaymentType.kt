package com.cramsan.edifikana.lib.model.payment

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Represents the type of a payment record.
 */
@Serializable
@JsonSchema.Description("Type of a payment record.")
enum class PaymentType {
    RENT,
    HOA,
    UTILITIES,
    OTHER,
}
