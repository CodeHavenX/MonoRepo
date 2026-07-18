package com.cramsan.edifikana.lib.model.task

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Represents the lifecycle status of a task.
 */
@Serializable
@JsonSchema.Description("Lifecycle status of a task.")
enum class TaskStatus {
    OPEN,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
}
