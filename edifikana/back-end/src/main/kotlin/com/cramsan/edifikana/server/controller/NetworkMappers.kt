@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.lib.model.InviteRole
import com.cramsan.edifikana.lib.model.network.AssetNetworkResponse
import com.cramsan.edifikana.lib.model.network.AuthMetadataNetworkResponse
import com.cramsan.edifikana.lib.model.network.DocumentNetworkResponse
import com.cramsan.edifikana.lib.model.network.EmployeeNetworkResponse
import com.cramsan.edifikana.lib.model.network.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.model.network.InviteNetworkResponse
import com.cramsan.edifikana.lib.model.network.MemberNetworkResponse
import com.cramsan.edifikana.lib.model.network.NotificationNetworkResponse
import com.cramsan.edifikana.lib.model.network.OrganizationNetworkResponse
import com.cramsan.edifikana.lib.model.network.PropertyNetworkResponse
import com.cramsan.edifikana.lib.model.network.TimeCardEventNetworkResponse
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.edifikana.server.service.models.Asset
import com.cramsan.edifikana.server.service.models.Document
import com.cramsan.edifikana.server.service.models.Employee
import com.cramsan.edifikana.server.service.models.EventLogEntry
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.Notification
import com.cramsan.edifikana.server.service.models.Organization
import com.cramsan.edifikana.server.service.models.OrgMemberView
import com.cramsan.edifikana.server.service.models.Property
import com.cramsan.edifikana.server.service.models.TimeCardEvent
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.logging.logE
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
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
        timestamp = timestamp,
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
        imageUrl = imageUrl,
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
        timestamp = timestamp,
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
        name = name,
        description = description,
    )
}

/**
 * Converts an [OrgMemberView] domain model to a [MemberNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun OrgMemberView.toMemberNetworkResponse(): MemberNetworkResponse {
    return MemberNetworkResponse(
        userId = userId,
        orgId = orgId,
        role = role,
        status = status,
        joinedAt = joinedAt,
        email = email,
        displayName = "$firstName $lastName".trim(),
    )
}

/**
 * Converts an [Invite] domain model to an [InviteNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun Invite.toInviteNetworkResponse(): InviteNetworkResponse {
    return InviteNetworkResponse(
        inviteId = id,
        email = email,
        organizationId = organizationId,
        role = role,
        expiresAt = expiration,
    )
}

/**
 * Converts a [Notification] domain model to a [NotificationNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun Notification.toNotificationNetworkResponse(): NotificationNetworkResponse {
    return NotificationNetworkResponse(
        id = id,
        notificationType = notificationType,
        description = description,
        isRead = isRead,
        createdAt = createdAt,
        readAt = readAt,
        inviteId = inviteId,
    )
}

/**
 * Converts a [Document] domain model to a [DocumentNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun Document.toDocumentNetworkResponse(): DocumentNetworkResponse {
    return DocumentNetworkResponse(
        documentId = id,
        orgId = orgId,
        propertyId = propertyId,
        unitId = unitId,
        filename = filename,
        mimeType = mimeType,
        documentType = documentType,
        assetId = assetId,
        createdBy = createdBy,
        createdAt = createdAt,
    )
}

/**
 * Converts a string to an [InviteRole] for org invite requests.
 *
 * Throws [ClientRequestExceptions.InvalidRequestException] if the value is not a valid [InviteRole].
 * [InviteRole.RESIDENT] is intentionally included here so the deserializer can
 * reject it at the service layer with a clear error, rather than failing silently.
 */
fun String.toInviteRole(): InviteRole {
    return try {
        enumValueOf<InviteRole>(this)
    } catch (e: IllegalArgumentException) {
        logE("NetworkMappers", e.localizedMessage)
        throw ClientRequestExceptions.InvalidRequestException(
            "Invalid invite role. Please select a role from the invite role list."
        )
    }
}

/**
 * Converts a string to a [UserRole].
 */
fun String.toServiceUserRole(): UserRole {
    return when (this) {
        "SUPERUSER" -> UserRole.SUPERUSER
        "OWNER" -> UserRole.OWNER
        "ADMIN" -> UserRole.ADMIN
        "MANAGER" -> UserRole.MANAGER
        "EMPLOYEE" -> UserRole.EMPLOYEE
        "USER" -> UserRole.USER
        "UNAUTHORIZED" -> UserRole.UNAUTHORIZED
        else -> throw IllegalArgumentException("Invalid UserRole value: $this")
    }
}
