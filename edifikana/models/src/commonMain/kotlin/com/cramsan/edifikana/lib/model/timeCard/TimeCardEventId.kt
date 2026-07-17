package com.cramsan.edifikana.lib.model.timeCard

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a time card event ID.
 */
@JvmInline
@Serializable
@JsonSchema.Description("Unique identifier of a time card event.")
@JsonSchema.Example("\"tce_a1b2c3d4\"")
value class TimeCardEventId(val timeCardEventId: String) : PathParam {
    override fun toString(): String = timeCardEventId
}
