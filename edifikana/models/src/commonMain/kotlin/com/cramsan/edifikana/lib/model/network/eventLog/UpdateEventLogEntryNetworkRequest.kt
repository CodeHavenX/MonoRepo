package com.cramsan.edifikana.lib.model.network.eventLog

import com.cramsan.edifikana.lib.model.eventLog.EventLogEventType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new event log entry.
 */
@NetworkModel
@Serializable
@JsonSchema.Description(
    "Request payload to update an existing event log entry. Only provided (non-null) fields are updated.",
)
data class UpdateEventLogEntryNetworkRequest(
    @JsonSchema.Description("New event type, or null to leave unchanged.")
    val type: EventLogEventType?,
    @SerialName("fallback_type")
    @JsonSchema.Description("New freeform event type, or null to leave unchanged.")
    val fallbackEventType: String?,
    @JsonSchema.Description("New title, or null to leave unchanged.")
    val title: String?,
    @JsonSchema.Description("New description, or null to leave unchanged.")
    val description: String?,
    @JsonSchema.Description("New associated unit identifier, or null to leave unchanged.")
    val unit: UnitId?,
) : RequestBody
