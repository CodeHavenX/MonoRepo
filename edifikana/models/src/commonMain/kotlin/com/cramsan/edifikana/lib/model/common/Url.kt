package com.cramsan.edifikana.lib.model.common

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Value type representing a URL.
 */
@JvmInline
@Serializable
@JsonSchema.Description("A URL.")
@JsonSchema.Format("uri")
@JsonSchema.Example("\"https://example.com/asset.png\"")
value class Url(val url: String) {
    override fun toString(): String = url
}
