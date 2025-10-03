@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.model.network.AssetNetworkResponse
import com.cramsan.edifikana.lib.model.network.AuthMetadataNetworkResponse
import com.cramsan.edifikana.lib.model.network.EmployeeNetworkResponse
import com.cramsan.edifikana.lib.model.network.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.model.network.InviteNetworkResponse
import com.cramsan.edifikana.lib.model.network.OrganizationNetworkResponse
import com.cramsan.edifikana.lib.model.network.PropertyNetworkResponse
import com.cramsan.edifikana.lib.model.network.TimeCardEventNetworkResponse
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.edifikana.server.core.service.models.Asset
import com.cramsan.edifikana.server.core.service.models.Employee
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.Invite
import com.cramsan.edifikana.server.core.service.models.Organization
import com.cramsan.edifikana.server.core.service.models.Property
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
 * Converts a [Employee] domain model to a [EmployeeNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun Employee.toEmployeeNetworkResponse(): EmployeeNetworkResponse {
    return EmployeeNetworkResponse(
        id = id,
        idType = idType,
        firstName = firstName,
        lastName = lastName,
        role = role,
        propertyId = propertyId,
    )
}

/**
 * Converts a [EventLogEntry] domain model to a [EventLogEntryNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun EventLogEntry.toEventLogEntryNetworkResponse(): EventLogEntryNetworkResponse {
    return EventLogEntryNetworkResponse(
        id = id,
        title = title,
        employeeId = employeeId,
        fallbackEmployeeName = fallbackEmployeeName,
        propertyId = propertyId,
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
        id = id,
        name = name,
        address = address,
        organizationId = organizationId,
    )
}

/**
 * Converts a [TimeCardEvent] domain model to a [TimeCardEventNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun TimeCardEvent.toTimeCardEventNetworkResponse(): TimeCardEventNetworkResponse {
    return TimeCardEventNetworkResponse(
        id = id,
        employeeId = employeeId,
        fallbackEmployeeName = fallbackEmployeeName,
        propertyId = propertyId,
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
        id = id,
        fileName = fileName,
        signedUrl = signedUrl,
    )
}

/**
 * Converts an [Organization] domain model to an [OrganizationNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun Organization.toOrganizationNetworkResponse(): OrganizationNetworkResponse {
    return OrganizationNetworkResponse(
        id = id,
    )
}

/**
 * Converts an [Invite] domain model to an [InviteNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun Invite.toInviteNetworkResponse(): InviteNetworkResponse {
    return InviteNetworkResponse(
        inviteId = inviteId,
        email = email,
    )
}
