package com.cramsan.edifikana.lib.model.task

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Represents the priority level of a task.
 */
@Serializable
@JsonSchema.Description("Priority level of a task.")
enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
}
