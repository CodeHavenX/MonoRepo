package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.EventLogEntryResponse
import com.cramsan.edifikana.lib.model.PropertyResponse
import com.cramsan.edifikana.lib.model.StaffResponse
import com.cramsan.edifikana.lib.model.TimeCardEventResponse
import com.cramsan.edifikana.lib.model.UserResponse
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.User

/**
 * Converts a [User] domain model to a [UserResponse] network model.
 */
@NetworkModel
fun User.toUserResponse(): UserResponse {
    return UserResponse(
        id = id.userId,
        email = email,
    )
}

/**
 * Converts a [Staff] domain model to a [StaffResponse] network model.
 */
@OptIn(NetworkModel::class)
fun Staff.toStaffResponse(): StaffResponse {
    return StaffResponse(
        id = id.staffId,
        name = name,
        propertyId = propertyId.propertyId,
    )
}

/**
 * Converts a [EventLogEntry] domain model to a [EventLogEntryResponse] network model.
 */
@OptIn(NetworkModel::class)
fun EventLogEntry.toEventLogResponse(): EventLogEntryResponse {
    return EventLogEntryResponse(
        id = id.eventLogEntryId,
    )
}

/**
 * Converts a [Property] domain model to a [PropertyResponse] network model.
 */
@OptIn(NetworkModel::class)
fun Property.toPropertyResponse(): PropertyResponse {
    return PropertyResponse(
        id = id.propertyId,
        name = name,
    )
}

/**
 * Converts a [TimeCardEvent] domain model to a [TimeCardEventResponse] network model.
 */
@OptIn(NetworkModel::class)
fun TimeCardEvent.toTimeCardResponse(): TimeCardEventResponse {
    return TimeCardEventResponse(
        id = id.timeCardEventId,
        staffId = staffId.staffId,
        type = type.name,
        time = time.toEpochMilliseconds(),
    )
}
