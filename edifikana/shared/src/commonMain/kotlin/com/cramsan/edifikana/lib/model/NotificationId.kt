package com.cramsan.edifikana.lib.model

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Inline value class representing a Notification ID.
 * This is used to uniquely identify a notification in the system.
 */
@Serializable
@JvmInline
value class NotificationId(
    val id: String,
) : PathParam {
    override fun toString(): String = id
}
