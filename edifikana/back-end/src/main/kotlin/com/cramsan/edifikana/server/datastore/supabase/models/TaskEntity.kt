package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Entity representing a task row in the `tasks` Supabase table.
 *
 * [priority] and [status] are stored as uppercase strings matching the DB enum values
 * (e.g. "LOW", "OPEN"). Use [TaskPriority.valueOf] and [TaskStatus.valueOf] to convert.
 */
@OptIn(ExperimentalTime::class)
@Serializable
@SupabaseModel
data class TaskEntity(
    val id: String,
    @SerialName("property_id")
    val propertyId: PropertyId,
    @SerialName("unit_id")
    val unitId: UnitId? = null,
    @SerialName("common_area_id")
    val commonAreaId: CommonAreaId? = null,
    @SerialName("assignee_id")
    val assigneeId: UserId? = null,
    @SerialName("created_by")
    val createdBy: UserId,
    @SerialName("status_changed_by")
    val statusChangedBy: UserId? = null,
    val title: String,
    val description: String? = null,
    val priority: String,
    val status: String,
    @SerialName("due_date")
    val dueDate: Instant? = null,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("completed_at")
    val completedAt: Instant? = null,
    @SerialName("status_changed_at")
    val statusChangedAt: Instant? = null,
    @SerialName("deleted_at")
    val deletedAt: Instant? = null,
) {
    companion object {
        const val COLLECTION = "tasks"
    }

    /**
     * Entity used when inserting a new task. Omits auto-generated fields
     * (id, created_at, deleted_at) and server-managed fields
     * (status, completed_at, status_changed_at, status_changed_by).
     */
    @Serializable
    @SupabaseModel
    data class CreateTaskEntity(
        @SerialName("property_id")
        val propertyId: PropertyId,
        @SerialName("unit_id")
        val unitId: UnitId? = null,
        @SerialName("common_area_id")
        val commonAreaId: CommonAreaId? = null,
        @SerialName("assignee_id")
        val assigneeId: UserId? = null,
        @SerialName("created_by")
        val createdBy: UserId,
        val title: String,
        val description: String? = null,
        val priority: String,
        @SerialName("due_date")
        val dueDate: Instant? = null,
    )
}
