package com.cramsan.edifikana.lib.model.task

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a task ID.
 */
@JvmInline
@Serializable
@JsonSchema.Description("Unique identifier of a task.")
@JsonSchema.Example("\"task_a1b2c3d4\"")
value class TaskId(val taskId: String) : PathParam {
    override fun toString(): String = taskId
}
