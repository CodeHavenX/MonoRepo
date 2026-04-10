@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.lib.model.network.asset.AssetNetworkResponse
import com.cramsan.edifikana.lib.model.network.payment.PaymentRecordNetworkResponse
import com.cramsan.edifikana.lib.model.network.rent.RentConfigNetworkResponse
import com.cramsan.edifikana.lib.model.network.commonArea.CommonAreaNetworkResponse
import com.cramsan.edifikana.lib.model.network.document.DocumentNetworkResponse
import com.cramsan.edifikana.lib.model.network.employee.EmployeeNetworkResponse
import com.cramsan.edifikana.lib.model.network.eventLog.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.model.network.invite.InviteNetworkResponse
import com.cramsan.edifikana.lib.model.network.notification.NotificationNetworkResponse
import com.cramsan.edifikana.lib.model.network.organization.MemberNetworkResponse
import com.cramsan.edifikana.lib.model.network.organization.OrganizationNetworkResponse
import com.cramsan.edifikana.lib.model.network.property.PropertyNetworkResponse
import com.cramsan.edifikana.lib.model.network.task.TaskNetworkResponse
import com.cramsan.edifikana.lib.model.network.timeCard.TimeCardEventNetworkResponse
import com.cramsan.edifikana.lib.model.network.unit.UnitNetworkResponse
import com.cramsan.edifikana.lib.model.network.user.AuthMetadataNetworkResponse
import com.cramsan.edifikana.lib.model.network.user.UserNetworkResponse
import com.cramsan.edifikana.server.service.models.Asset
import com.cramsan.edifikana.server.service.models.PaymentRecord
import com.cramsan.edifikana.server.service.models.RentConfig
import com.cramsan.edifikana.server.service.models.CommonArea
import com.cramsan.edifikana.server.service.models.Document
import com.cramsan.edifikana.server.service.models.Employee
import com.cramsan.edifikana.server.service.models.EventLogEntry
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.Notification
import com.cramsan.edifikana.server.service.models.OrgMemberView
import com.cramsan.edifikana.server.service.models.Organization
import com.cramsan.edifikana.server.service.models.Property
import com.cramsan.edifikana.server.service.models.Task
import com.cramsan.edifikana.server.service.models.TimeCardEvent
import com.cramsan.edifikana.server.service.models.Unit
import com.cramsan.edifikana.server.service.models.User
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
 * Converts a [CommonArea] domain model to a [CommonAreaNetworkResponse] network model.
 */
@NetworkModel
fun CommonArea.toCommonAreaNetworkResponse(): CommonAreaNetworkResponse {
    return CommonAreaNetworkResponse(
        commonAreaId = id,
        propertyId = propertyId,
        name = name,
        type = type,
        description = description,
    )
}

/**
 * Converts a [Task] domain model to a [TaskNetworkResponse] network model.
 */
@NetworkModel
fun Task.toTaskNetworkResponse(): TaskNetworkResponse {
    return TaskNetworkResponse(
        id = id,
        propertyId = propertyId,
        unitId = unitId,
        commonAreaId = commonAreaId,
        assigneeId = assigneeId,
        createdBy = createdBy,
        statusChangedBy = statusChangedBy,
        title = title,
        description = description,
        priority = priority,
        status = status,
        dueDate = dueDate,
        createdAt = createdAt,
        completedAt = completedAt,
        statusChangedAt = statusChangedAt,
    )
}

/**
 * Converts a [Unit] domain model to a [UnitNetworkResponse] network model.
 */
@OptIn(NetworkModel::class)
fun Unit.toUnitNetworkResponse(): UnitNetworkResponse {
    return UnitNetworkResponse(
        unitId = id,
        propertyId = propertyId,
        unitNumber = unitNumber,
        bedrooms = bedrooms,
        bathrooms = bathrooms,
        sqFt = sqFt,
        floor = floor,
        notes = notes,
    )
}

/**
 * Converts a [PaymentRecord] domain model to a [PaymentRecordNetworkResponse] network model.
 */
@NetworkModel
fun PaymentRecord.toPaymentRecordNetworkResponse(): PaymentRecordNetworkResponse {
    return PaymentRecordNetworkResponse(
        paymentRecordId = id,
        unitId = unitId,
        paymentType = paymentType,
        periodMonth = periodMonth,
        amountDue = amountDue,
        amountPaid = amountPaid,
        status = status,
        dueDate = dueDate,
        paidDate = paidDate,
        recordedBy = recordedBy,
        recordedAt = recordedAt,
        notes = notes,
    )
}

/**
 * Converts a [RentConfig] domain model to a [RentConfigNetworkResponse] network model.
 */
@NetworkModel
fun RentConfig.toRentConfigNetworkResponse(): RentConfigNetworkResponse {
    return RentConfigNetworkResponse(
        rentConfigId = id,
        unitId = unitId,
        monthlyAmount = monthlyAmount,
        dueDay = dueDay,
        currency = currency,
        updatedAt = updatedAt,
        updatedBy = updatedBy,
        createdAt = createdAt,
    )
}
