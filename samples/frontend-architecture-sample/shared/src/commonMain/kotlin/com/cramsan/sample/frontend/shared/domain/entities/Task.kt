package com.cramsan.sample.frontend.shared.domain.entities

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Core domain entity representing a task in the task management system.
 * This entity contains only business logic and no UI or platform-specific code.
 */
@Serializable
data class Task(
    val id: TaskId,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val createdAt: Instant,
    val completedAt: Instant? = null,
    val dueDate: Instant? = null,
) {
    /**
     * Business rule: A task can only be marked as completed if it has a title
     */
    fun canBeCompleted(): Boolean = title.isNotBlank()

    /**
     * Business rule: Check if task is overdue
     */
    fun isOverdue(currentTime: Instant): Boolean {
        return dueDate?.let { due ->
            !isCompleted && currentTime > due
        } ?: false
    }
}

/**
 * Value object for task identifier
 */
@Serializable
@kotlin.jvm.JvmInline
value class TaskId(val value: String)

/**
 * Constants for task priority sort orders
 */
private const val PRIORITY_SORT_ORDER_LOW = 1
private const val PRIORITY_SORT_ORDER_MEDIUM = 2
private const val PRIORITY_SORT_ORDER_HIGH = 3
private const val PRIORITY_SORT_ORDER_URGENT = 4

/**
 * Task priority levels following business requirements
 */
@Serializable
enum class TaskPriority(val displayName: String, val sortOrder: Int) {
    LOW("Low", PRIORITY_SORT_ORDER_LOW),
    MEDIUM("Medium", PRIORITY_SORT_ORDER_MEDIUM),
    HIGH("High", PRIORITY_SORT_ORDER_HIGH),
    URGENT("Urgent", PRIORITY_SORT_ORDER_URGENT);

    companion object {
        /**
         * Find a TaskPriority by its display name
         * @param displayName The display name to search for
         * @return The matching TaskPriority or null if not found
         */
        fun fromDisplayName(displayName: String): TaskPriority? {
            return entries.find { it.displayName == displayName }
        }
    }
}
