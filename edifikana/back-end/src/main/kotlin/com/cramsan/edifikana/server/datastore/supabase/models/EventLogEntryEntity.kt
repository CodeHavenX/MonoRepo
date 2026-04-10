package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Entity representing an event log entry.
 */
@Serializable
@SupabaseModel
data class EventLogEntryEntity(
    val id: String,
    @SerialName("employee_id")
    val employeeId: EmployeeId?,
    @SerialName("fallback_employee_name")
    val fallbackEmployeeName: String?,
    @SerialName("property_id")
    val propertyId: PropertyId,
    val type: EventLogEventType,
    @SerialName("fallback_event_type")
    val fallbackEventType: String?,
    val timestamp: Instant,
    val title: String,
    val description: String?,
    val unit: UnitId,
    @SerialName("deleted_at")
    val deletedAt: Instant? = null,
) {
    companion object {
        const val COLLECTION = "event_log_entries"
    }

    /**
     * Entity representing a new event log entry.
     */
    @Serializable
    @SupabaseModel
    data class CreateEventLogEntryEntity(
        @SerialName("employee_id")
        val employeeId: EmployeeId?,
        @SerialName("fallback_employee_name")
        val fallbackEmployeeName: String?,
        @SerialName("property_id")
        val propertyId: PropertyId,
        val type: EventLogEventType,
        @SerialName("fallback_event_type")
        val fallbackEventType: String?,
        val timestamp: Instant,
        val title: String,
        val description: String?,
        val unit: UnitId?,
    )
}
