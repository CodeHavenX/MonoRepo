package com.cramsan.edifikana.lib.model.common

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Value type representing a phone number.
 */
@JvmInline
@Serializable
@JsonSchema.Description("A phone number.")
@JsonSchema.Example("\"+1-555-123-4567\"")
value class PhoneNumber(val phoneNumber: String) {
    override fun toString(): String = phoneNumber
}
