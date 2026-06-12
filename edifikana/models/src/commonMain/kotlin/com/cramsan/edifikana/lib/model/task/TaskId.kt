package com.cramsan.edifikana.lib.model.task

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a task ID.
 */
@JvmInline
@Serializable
value class TaskId(val taskId: String) : PathParam {
    override fun toString(): String = taskId
}
