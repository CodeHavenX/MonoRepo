package com.cramsan.edifikana.lib.model.common

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Value type representing a monetary amount, in the major unit of its associated currency
 * (e.g. dollars, not cents).
 */
@JvmInline
@Serializable
@JsonSchema.Description("A monetary amount, in the major unit of its associated currency.")
@JsonSchema.Minimum(0.0)
value class MonetaryAmount(val amount: Double)
