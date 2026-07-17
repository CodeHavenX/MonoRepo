package com.cramsan.edifikana.lib.model.payment

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a payment record ID.
 */
@JvmInline
@Serializable
@JsonSchema.Description("Unique identifier of a payment record.")
@JsonSchema.Example("\"pay_a1b2c3d4\"")
value class PaymentRecordId(val paymentRecordId: String) : PathParam {
    override fun toString(): String = paymentRecordId
}
