package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffStatus
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.CreateEventLogEntryNetworkRequest
import com.cramsan.edifikana.lib.model.network.CreateStaffNetworkRequest
import com.cramsan.edifikana.lib.model.network.CreateTimeCardEventNetworkRequest
import com.cramsan.edifikana.lib.model.network.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.model.network.StaffNetworkResponse
import com.cramsan.edifikana.lib.model.network.TimeCardEventNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpdateEventLogEntryNetworkRequest
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse

/**
 * Maps the [EventLogEntryNetworkResponse] models to [EventLogRecordModel] domain models.
 */
@OptIn(NetworkModel::class)
fun EventLogEntryNetworkResponse.toEventLogRecordModel(): EventLogRecordModel {
    return EventLogRecordModel(
        id = EventLogEntryId(id),
        entityId = null,
        staffPk = staffId?.let { it1 -> StaffId(it1) },
        propertyId = PropertyId(propertyId),
        timeRecorded = timestamp,
        unit = unit,
        eventType = type,
        fallbackStaffName = fallbackEventType,
        fallbackEventType = fallbackStaffName,
        title = title,
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
        staffId = staffPk?.staffId,
        fallbackStaffName = fallbackStaffName,
        propertyId = propertyId.propertyId,
        type = eventType,
        fallbackEventType = fallbackEventType,
        timestamp = timeRecorded,
        title = title,
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
        title = title,
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
        propertyId = propertyId.propertyId,
    )
}

/**
 * Maps the [StaffNetworkResponse] models to [StaffModel] domain models.
 */
@NetworkModel
fun StaffNetworkResponse.toStaffModel(): StaffModel {
    return StaffModel(
        id = StaffId(id),
        firstName = firstName,
        lastName = lastName,
        role = role,
        idType = idType,
        email = "email",
        status = StaffStatus.PENDING,
    )
}

/**
 * Maps the [TimeCardRecordModel] domain models to [CreateTimeCardEventNetworkRequest] models.
 */
@NetworkModel
fun TimeCardRecordModel.toCreateTimeCardEventNetworkRequest(): CreateTimeCardEventNetworkRequest {
    return CreateTimeCardEventNetworkRequest(
        staffId = staffPk.staffId,
        fallbackStaffName = "",
        type = eventType,
        propertyId = propertyId.propertyId,
        imageUrl = imageUrl,
    )
}

/**
 * Maps the [TimeCardEventNetworkResponse] models to [TimeCardRecordModel] domain models.
 */
@NetworkModel
fun TimeCardEventNetworkResponse.toTimeCardRecordModel(): TimeCardRecordModel {
    return TimeCardRecordModel(
        id = TimeCardEventId(id),
        entityId = null,
        staffPk = StaffId(staffId ?: ""), // TODO: Fix this
        propertyId = PropertyId(propertyId),
        eventType = type,
        eventTime = timestamp,
        imageUrl = imageUrl,
        imageRef = null,
    )
}

/**
 * Maps the [UserNetworkResponse] models to [UserModel] domain models.
 */
@NetworkModel
fun UserNetworkResponse.toUserModel(): UserModel {
    return UserModel(
        id = UserId(id),
        email = email,
        phoneNumber = phoneNumber,
        firstName = firstName,
        lastName = lastName,
        authMetadata = authMetadata?.let {
            UserModel.AuthMetadataModel(
                isPasswordSet = it.isPasswordSet,
            )
        },
    )
}
