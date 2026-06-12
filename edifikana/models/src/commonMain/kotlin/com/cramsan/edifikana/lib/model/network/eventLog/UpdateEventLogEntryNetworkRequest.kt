package com.cramsan.edifikana.lib.model.network.eventLog

import com.cramsan.edifikana.lib.model.eventLog.EventLogEventType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new event log entry.
 */
@NetworkModel
@Serializable
data class UpdateEventLogEntryNetworkRequest(
    val type: EventLogEventType?,
    @SerialName("fallback_type")
    val fallbackEventType: String?,
    val title: String?,
    val description: String?,
    val unit: UnitId?,
) : RequestBody
