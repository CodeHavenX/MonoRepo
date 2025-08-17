@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.model.network.AssetNetworkResponse
import com.cramsan.edifikana.lib.model.network.AuthMetadataNetworkResponse
import com.cramsan.edifikana.lib.model.network.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.model.network.PropertyNetworkResponse
import com.cramsan.edifikana.lib.model.network.StaffNetworkResponse
import com.cramsan.edifikana.lib.model.network.TimeCardEventNetworkResponse
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.edifikana.server.core.service.models.Asset
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.framework.annotations.NetworkModel
import kotlin.time.ExperimentalTime

/**
 * Converts a [User] domain model to a [UserNetworkResponse] network model.
 */
@NetworkModel
fun User.toUserNetworkResponse(): UserNetworkResponse {
    return UserNetworkResponse(
        id = id.userId,
        email = email,
        phoneNumber = phoneNumber,
        firstName = firstName,
        lastName = lastName,
        authMetadata = authMetadata?.let {
            AuthMetadataNetworkResponse(
                isPasswordSet = it.isPasswordSet,
            )
        },
    )
}

/**
 * Converts a [Staff] domain model to a [StaffNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun Staff.toStaffNetworkResponse(): StaffNetworkResponse {
    return StaffNetworkResponse(
        id = id.staffId,
        idType = idType,
        firstName = firstName,
        lastName = lastName,
        role = role,
        propertyId = propertyId.propertyId,
    )
}

/**
 * Converts a [EventLogEntry] domain model to a [EventLogEntryNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun EventLogEntry.toEventLogEntryNetworkResponse(): EventLogEntryNetworkResponse {
    return EventLogEntryNetworkResponse(
        id = id.eventLogEntryId,
        title = title,
        staffId = staffId?.staffId,
        fallbackStaffName = fallbackStaffName,
        propertyId = propertyId.propertyId,
        type = type,
        fallbackEventType = fallbackEventType,
        timestamp = timestamp.epochSeconds,
        description = description,
        unit = unit,
    )
}

/**
 * Converts a [Property] domain model to a [PropertyNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun Property.toPropertyNetworkResponse(): PropertyNetworkResponse {
    return PropertyNetworkResponse(
        id = id.propertyId,
        name = name,
        address = address,
    )
}

/**
 * Converts a [TimeCardEvent] domain model to a [TimeCardEventNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun TimeCardEvent.toTimeCardEventNetworkResponse(): TimeCardEventNetworkResponse {
    return TimeCardEventNetworkResponse(
        id = id.timeCardEventId,
        staffId = staffId?.staffId,
        fallbackStaffName = fallbackStaffName,
        propertyId = propertyId.propertyId,
        type = type,
        imageUrl = imageUrl,
        timestamp = timestamp.epochSeconds,
    )
}

/**
 * Converts an [Asset] domain model to a [AssetNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun Asset.toAssetNetworkResponse(): AssetNetworkResponse {
    return AssetNetworkResponse(
        id = id.assetId,
        fileName = fileName,
        signedUrl = signedUrl,
    )
}
