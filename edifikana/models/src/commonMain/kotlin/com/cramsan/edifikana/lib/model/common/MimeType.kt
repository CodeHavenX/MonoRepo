package com.cramsan.edifikana.lib.model.common

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Value type representing an IANA media (MIME) type, e.g. "application/pdf".
 */
@JvmInline
@Serializable
@JsonSchema.Description("An IANA media (MIME) type.")
@JsonSchema.Example("\"application/pdf\"")
value class MimeType(val value: String) {
    override fun toString(): String = value
}
