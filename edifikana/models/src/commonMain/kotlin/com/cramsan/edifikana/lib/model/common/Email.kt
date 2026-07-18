package com.cramsan.edifikana.lib.model.common

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Value type representing an email address.
 */
@JvmInline
@Serializable
@JsonSchema.Description("An email address.")
@JsonSchema.Format("email")
@JsonSchema.Example("\"jane@example.com\"")
value class Email(val email: String) {
    override fun toString(): String = email
}
