package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.model.PropertyNetworkResponse
import com.cramsan.edifikana.lib.model.StaffNetworkResponse
import com.cramsan.edifikana.lib.model.TimeCardEventNetworkResponse
import com.cramsan.edifikana.lib.model.UserNetworkResponse
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.User

/**
 * Converts a [User] domain model to a [UserNetworkResponse] network model.
 */
@NetworkModel
fun User.toUserResponse(): UserNetworkResponse {
    return UserNetworkResponse(
        id = id.userId,
        email = email,
    )
}

/**
 * Converts a [Staff] domain model to a [StaffNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun Staff.toStaffResponse(): StaffNetworkResponse {
    return StaffNetworkResponse(
        id = id.staffId,
        name = name,
        propertyId = propertyId.propertyId,
    )
}

/**
 * Converts a [EventLogEntry] domain model to a [EventLogEntryNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun EventLogEntry.toEventLogResponse(): EventLogEntryNetworkResponse {
    return EventLogEntryNetworkResponse(
        id = id.eventLogEntryId,
    )
}

/**
 * Converts a [Property] domain model to a [PropertyNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun Property.toPropertyResponse(): PropertyNetworkResponse {
    return PropertyNetworkResponse(
        id = id.propertyId,
        name = name,
    )
}

/**
 * Converts a [TimeCardEvent] domain model to a [TimeCardEventNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun TimeCardEvent.toTimeCardResponse(): TimeCardEventNetworkResponse {
    return TimeCardEventNetworkResponse(
        id = id.timeCardEventId,
        staffId = staffId.staffId,
        type = type.name,
        time = time.toEpochMilliseconds(),
    )
}
