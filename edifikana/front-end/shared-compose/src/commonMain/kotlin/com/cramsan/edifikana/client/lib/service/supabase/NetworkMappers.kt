package com.cramsan.edifikana.client.lib.service.supabase

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.network.CreateEventLogEntryNetworkRequest
import com.cramsan.edifikana.lib.model.network.CreateStaffNetworkRequest
import com.cramsan.edifikana.lib.model.network.CreateTimeCardEventNetworkRequest
import com.cramsan.edifikana.lib.model.network.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.model.network.StaffNetworkResponse
import com.cramsan.edifikana.lib.model.network.TimeCardEventNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpdateEventLogEntryNetworkRequest

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

/**
 * Maps the [StaffModel.CreateStaffRequest] models to [CreateStaffNetworkRequest] models.
 */
@NetworkModel
fun StaffModel.CreateStaffRequest.toCreateStaffNetworkRequest(): CreateStaffNetworkRequest {
    return CreateStaffNetworkRequest(
        idType = idType,
        firstName = firstName,
        lastName = lastName,
        role = role,
        propertyId = "", // TODO: Add property ID
    )
}

/**
 * Maps the [StaffNetworkResponse] models to [StaffModel] domain models.
 */
@NetworkModel
fun StaffNetworkResponse.toStaffModel(): StaffModel {
    return StaffModel(
        id = StaffPK(id),
        name = firstName, // TODO: Rename to first name
        lastName = lastName,
        role = role,
        idType = idType,
    )
}

/**
 * Maps the [TimeCardRecordModel] domain models to [CreateTimeCardEventNetworkRequest] models.
 */
@NetworkModel
fun TimeCardRecordModel.toCreateTimeCardEventNetworkRequest(): CreateTimeCardEventNetworkRequest {
    return CreateTimeCardEventNetworkRequest(
        staffId = staffPk.documentPath,
        fallbackStaffName = "",
        type = eventType,
        propertyId = "",
        imageUrl = imageUrl,
    )
}

/**
 * Maps the [TimeCardEventNetworkResponse] models to [TimeCardRecordModel] domain models.
 */
@NetworkModel
fun TimeCardEventNetworkResponse.toTimeCardRecordModel(): TimeCardRecordModel {
    return TimeCardRecordModel(
        id = TimeCardRecordPK(id),
        entityId = null,
        staffPk = StaffPK(staffId ?: ""), // TODO: Fix this
        eventType = type,
        eventTime = timestamp,
        imageUrl = imageUrl,
        imageRef = null,
    )
}
