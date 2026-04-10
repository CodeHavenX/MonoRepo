package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.commonArea.CommonAreaId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.datetime.LocalDate
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
    val unitId: UnitId?,
    @SerialName("common_area_id")
    val commonAreaId: CommonAreaId?,
    @SerialName("assignee_id")
    val assigneeId: UserId?,
    @SerialName("created_by")
    val createdBy: UserId,
    @SerialName("status_changed_by")
    val statusChangedBy: UserId?,
    val title: String,
    val description: String?,
    val priority: String,
    val status: String,
    @SerialName("due_date")
    val dueDate: LocalDate?,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("completed_at")
    val completedAt: Instant?,
    @SerialName("status_changed_at")
    val statusChangedAt: Instant?,
    @SerialName("deleted_at")
    val deletedAt: Instant?,
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
        val unitId: UnitId?,
        @SerialName("common_area_id")
        val commonAreaId: CommonAreaId?,
        @SerialName("assignee_id")
        val assigneeId: UserId?,
        @SerialName("created_by")
        val createdBy: UserId,
        val title: String,
        val description: String?,
        val priority: String,
        @SerialName("due_date")
        val dueDate: LocalDate?,
    )
}
