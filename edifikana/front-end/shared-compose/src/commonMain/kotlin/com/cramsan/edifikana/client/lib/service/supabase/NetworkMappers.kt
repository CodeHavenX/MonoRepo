package com.cramsan.edifikana.client.lib.service.supabase

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.EventLogRecordPK
import com.cramsan.edifikana.lib.StaffPK
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.CreateEventLogEntryNetworkRequest
import com.cramsan.edifikana.lib.model.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.model.UpdateEventLogEntryNetworkRequest

/**
 * Maps the [EventLogEntryNetworkResponse] models to [EventLogRecordModel] domain models.
 */
@OptIn(NetworkModel::class)
fun EventLogEntryNetworkResponse.toEventLogRecordModel(): EventLogRecordModel {
    return EventLogRecordModel(
        id = EventLogRecordPK(id),
        entityId = null,
        staffPk = staffId?.let { it1 -> StaffPK(it1) },
        timeRecorded = timestamp,
        unit = unit,
        eventType = type,
        fallbackStaffName = fallbackEventType,
        fallbackEventType = fallbackStaffName,
        summary = summary,
        description = description.orEmpty(),
        emptyList(),
    )
}

/**
 * Maps the [EventLogRecordModel] domain models to [CreateEventLogEntryNetworkRequest] models.
 */
@OptIn(NetworkModel::class)
fun EventLogRecordModel.toCreateEventLogEntryNetworkRequest(): CreateEventLogEntryNetworkRequest {
    return CreateEventLogEntryNetworkRequest(
        staffId = staffPk?.documentPath,
        fallbackStaffName = fallbackStaffName,
        propertyId = "",
        type = eventType,
        fallbackEventType = fallbackEventType,
        timestamp = timeRecorded,
        summary = summary,
        description = description,
        unit = unit,
    )
}

/**
 * Maps the [EventLogRecordModel] domain models to [UpdateEventLogEntryNetworkRequest] models.
 */
@OptIn(NetworkModel::class)
fun EventLogRecordModel.toUpdateEventLogEntryNetworkRequest(): UpdateEventLogEntryNetworkRequest {
    return UpdateEventLogEntryNetworkRequest(
        type = eventType,
        fallbackEventType = fallbackEventType,
        summary = summary,
        description = description,
        unit = unit,
    )
}
