package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable

/**
 * Represents the priority level of a task.
 */
@Serializable
enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
}
