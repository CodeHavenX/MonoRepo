package com.cramsan.edifikana.lib.model.notification

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Inline value class representing a Notification ID.
 * This is used to uniquely identify a notification in the system.
 */
@Serializable
@JvmInline
@JsonSchema.Description("Unique identifier of a notification.")
@JsonSchema.Example("\"ntf_a1b2c3d4\"")
value class NotificationId(val id: String) : PathParam {
    override fun toString(): String = id
}
