package com.cramsan.edifikana.lib.model.payment

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a payment record ID.
 */
@JvmInline
@Serializable
value class PaymentRecordId(val paymentRecordId: String) : PathParam {
    override fun toString(): String = paymentRecordId
}
