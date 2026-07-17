package com.cramsan.edifikana.lib.model.common

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Value type representing an ISO 4217 currency code (e.g. "USD", "PEN").
 */
@JvmInline
@Serializable
@JsonSchema.Description("An ISO 4217 currency code.")
@JsonSchema.Example("\"USD\"")
value class CurrencyCode(val code: String) {
    override fun toString(): String = code
}
