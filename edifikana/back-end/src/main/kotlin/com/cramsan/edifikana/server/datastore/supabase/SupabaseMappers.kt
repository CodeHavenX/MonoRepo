@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.DocumentId
import com.cramsan.edifikana.lib.model.DocumentType
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.InviteRole
import com.cramsan.edifikana.lib.model.OrgRole
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.supabase.models.AuthMetadataEntity
import com.cramsan.edifikana.server.datastore.supabase.models.DocumentEntity
import com.cramsan.edifikana.server.datastore.supabase.models.EmployeeEntity
import com.cramsan.edifikana.server.datastore.supabase.models.EventLogEntryEntity
import com.cramsan.edifikana.server.datastore.supabase.models.InviteEntity
import com.cramsan.edifikana.server.datastore.supabase.models.NotificationEntity
import com.cramsan.edifikana.server.datastore.supabase.models.OrganizationEntity
import com.cramsan.edifikana.server.datastore.supabase.models.PropertyEntity
import com.cramsan.edifikana.server.datastore.supabase.models.TimeCardEventEntity
import com.cramsan.edifikana.server.datastore.supabase.models.UserEntity
import com.cramsan.edifikana.server.service.models.Document
import com.cramsan.edifikana.server.service.models.Employee
import com.cramsan.edifikana.server.service.models.EventLogEntry
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.Notification
import com.cramsan.edifikana.server.service.models.Organization
import com.cramsan.edifikana.server.service.models.Property
import com.cramsan.edifikana.server.service.models.TimeCardEvent
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Maps a [UserRole] to the corresponding [OrgRole] stored in the database.
 *
 * This is a temporary bridge used until [Invite.role] is migrated from [UserRole]
 * to [InviteRole] in a follow-up PR. Only the four roles that have a valid
 * [OrgRole] equivalent are mapped; all others throw [IllegalArgumentException].
 */
fun UserRole.toOrgRole(): OrgRole = when (this) {
    UserRole.OWNER -> OrgRole.OWNER
    UserRole.ADMIN -> OrgRole.ADMIN
    UserRole.MANAGER -> OrgRole.MANAGER
    UserRole.EMPLOYEE -> OrgRole.EMPLOYEE
    else -> throw IllegalArgumentException("UserRole $this has no OrgRole equivalent")
}

/**
 * Maps an [OrgRole] to the back-end [UserRole] privilege ladder.
 *
 * [OrgRole] represents what is stored in the database for org membership.
 * [UserRole] is the back-end RBAC type used for privilege-level comparisons.
 * This bridge allows the RBAC service to continue using [UserRole.level] checks
 * without being aware of the DB-level role model.
 */
fun OrgRole.toUserRole(): UserRole = when (this) {
    OrgRole.OWNER -> UserRole.OWNER
    OrgRole.ADMIN -> UserRole.ADMIN
    OrgRole.MANAGER -> UserRole.MANAGER
    OrgRole.EMPLOYEE -> UserRole.EMPLOYEE
}

/**
 * Maps an [InviteRole] to the corresponding [OrgRole] for org membership insertion.
 *
 * [InviteRole.RESIDENT] does not produce an org membership row and therefore has no
 * [OrgRole] equivalent. Callers must handle [RESIDENT] before calling this function.
 */
fun InviteRole.toOrgRole(): OrgRole = when (this) {
    InviteRole.ADMIN -> OrgRole.ADMIN
    InviteRole.MANAGER -> OrgRole.MANAGER
    InviteRole.EMPLOYEE -> OrgRole.EMPLOYEE
    InviteRole.RESIDENT -> throw IllegalArgumentException("RESIDENT has no OrgRole equivalent")
}

/**
 * Maps an [InviteRole] to the [UserRole] privilege ladder for permission-level comparisons.
 *
 * [InviteRole.RESIDENT] maps to [UserRole.USER] (lowest non-UNAUTHORIZED privilege).
 */
fun InviteRole.toUserRole(): UserRole = when (this) {
    InviteRole.ADMIN -> UserRole.ADMIN
    InviteRole.MANAGER -> UserRole.MANAGER
    InviteRole.EMPLOYEE -> UserRole.EMPLOYEE
    InviteRole.RESIDENT -> UserRole.USER
}

/**
 * Maps a [UserEntity] to the [User] model.
 */
@OptIn(SupabaseModel::class)
fun UserEntity.toUser(): User {
    return User(
        id = UserId(this.id),
        email = this.email,
        phoneNumber = this.phoneNumber,
        firstName = this.firstName,
        lastName = this.lastName,
        authMetadata = User.AuthMetadata(
            isPasswordSet = this.authMetadata.canPasswordAuth,
        ),
        role = UserRole.USER,

    )
}

/**
 * Create a [UserEntity.CreateUserEntity] from the provided parameters.
 * This function is used to create a new user entity in the database.
 */
@OptIn(SupabaseModel::class, SecureStringAccess::class)
fun CreateUserEntity(
    userId: UserId,
    email: String,
    phoneNumber: String,
    firstName: String,
    lastName: String,
    pendingAssociation: Boolean,
    canPasswordAuth: Boolean,
    hashedPassword: SecureString?,
): UserEntity.CreateUserEntity {
    return UserEntity.CreateUserEntity(
        id = userId.userId,
        email = email,
        phoneNumber = phoneNumber,
        firstName = firstName,
        lastName = lastName,
        authMetadata = AuthMetadataEntity(
            pendingAssociation = pendingAssociation,
            canPasswordAuth = canPasswordAuth,
            hashedPassword = hashedPassword?.reveal(),
        ),
    )
}

/**
 * Create a [UserEntity.CreateUserEntity] from the provided [UserId] and [UserEntity].
 * This function is used to create a new user entity in the database with the existing user entity.
 * This is useful in cases of migrating from a transient user to a permanent user.
 */
@OptIn(SupabaseModel::class)
fun CreateUserEntity(
    userId: UserId,
    email: String,
    userEntity: UserEntity,
): UserEntity.CreateUserEntity {
    return UserEntity.CreateUserEntity(
        id = userId.userId,
        email = email,
        phoneNumber = userEntity.phoneNumber,
        firstName = userEntity.firstName,
        lastName = userEntity.lastName,
        authMetadata = userEntity.authMetadata.copy(
            pendingAssociation = false,
        )
    )
}

/**
 * Create a [UserEntity.CreateUserEntity] from the provided parameters.
 */
@OptIn(SupabaseModel::class)
fun CreateEmployeeEntity(
    idType: IdType,
    firstName: String,
    lastName: String,
    role: EmployeeRole,
    propertyId: PropertyId,
): EmployeeEntity.CreateEmployeeEntity {
    return EmployeeEntity.CreateEmployeeEntity(
        idType = idType,
        firstName = firstName,
        lastName = lastName,
        role = role,
        propertyId = propertyId,
    )
}

/**
 * Maps a [EmployeeEntity] to the [Employee] model.
 */
@OptIn(SupabaseModel::class)
fun EmployeeEntity.toEmployee(): Employee {
    return Employee(
        id = EmployeeId(this.id),
        idType = this.idType,
        firstName = this.firstName,
        lastName = this.lastName,
        role = this.role,
        propertyId = this.propertyId,
    )
}

/**
 * Maps a [CreatePropertyRequest] to the [PropertyEntity.CreatePropertyEntity] model.
 */
@OptIn(SupabaseModel::class)
fun CreatePropertyEntity(
    name: String,
    address: String,
    organizationId: OrganizationId,
    imageUrl: String? = null,
): PropertyEntity.CreatePropertyEntity {
    return PropertyEntity.CreatePropertyEntity(
        name = name,
        address = address,
        organizationId = organizationId,
        imageUrl = imageUrl,
    )
}

/**
 * Maps a [PropertyEntity] to the [Property] model.
 */
@OptIn(SupabaseModel::class)
fun PropertyEntity.toProperty(): Property {
    return Property(
        id = PropertyId(this.id),
        name = this.name,
        address = this.address,
        organizationId = this.organizationId,
        imageUrl = this.imageUrl,
    )
}

/**
 * Creates a [TimeCardEventEntity.CreateTimeCardEventEntity] from the provided parameters.
 */
@OptIn(SupabaseModel::class)
fun CreateTimeCardEventEntity(
    employeeId: EmployeeId,
    fallbackEmpName: String?,
    propertyId: PropertyId,
    type: TimeCardEventType,
    imageUrl: String?,
    timestamp: Instant,
): TimeCardEventEntity.CreateTimeCardEventEntity {
    return TimeCardEventEntity.CreateTimeCardEventEntity(
        employeeId = employeeId,
        fallbackEmployeeName = fallbackEmpName,
        propertyId = propertyId,
        type = type,
        imageUrl = imageUrl,
        timestamp = timestamp.epochSeconds,
    )
}

/**
 * Maps a [TimeCardEventEntity] to the [TimeCardEvent] model.
 */
@OptIn(SupabaseModel::class)
fun TimeCardEventEntity.toTimeCardEvent(): TimeCardEvent? {
    val employeeId = this.employeeId ?: return null
    return TimeCardEvent(
        id = TimeCardEventId(this.id),
        employeeId = employeeId,
        fallbackEmployeeName = this.fallbackEmployeeName,
        propertyId = this.propertyId,
        type = this.type,
        imageUrl = this.imageUrl,
        timestamp = Instant.fromEpochSeconds(this.timestamp),
    )
}

/**
 * Create a [EventLogEntryEntity.CreateEventLogEntryEntity] from the provided parameters.
 */
@OptIn(SupabaseModel::class)
fun CreateEventLogEntryEntity(
    employeeId: EmployeeId?,
    fallbackEmployeeName: String?,
    propertyId: PropertyId,
    type: EventLogEventType,
    fallbackEventType: String?,
    timestamp: Instant,
    title: String,
    description: String?,
    unit: UnitId?,
): EventLogEntryEntity.CreateEventLogEntryEntity {
    return EventLogEntryEntity.CreateEventLogEntryEntity(
        employeeId = employeeId,
        fallbackEmployeeName = fallbackEmployeeName,
        propertyId = propertyId,
        type = type,
        fallbackEventType = fallbackEventType,
        timestamp = timestamp.epochSeconds,
        title = title,
        description = description,
        unit = unit,
    )
}

/**
 * Maps a [EventLogEntryEntity] to the [EventLogEntry] model.
 */
@OptIn(SupabaseModel::class)
fun EventLogEntryEntity.toEventLogEntry(): EventLogEntry {
    return EventLogEntry(
        id = EventLogEntryId(this.id),
        employeeId = this.employeeId,
        fallbackEmployeeName = this.fallbackEmployeeName,
        propertyId = this.propertyId,
        type = this.type,
        fallbackEventType = this.fallbackEventType,
        timestamp = Instant.fromEpochSeconds(this.timestamp),
        title = this.title,
        description = this.description,
        unit = this.unit,
    )
}

/**
 * Maps an [OrganizationEntity] to the [Organization] model.
 */
@OptIn(SupabaseModel::class)
fun OrganizationEntity.toOrganization() = Organization(
    id = OrganizationId(this.id),
    name = this.name,
    description = this.description,
)

/**
 * Maps a [NotificationEntity] to the [Notification] model.
 */
@SupabaseModel
fun NotificationEntity.toNotification(): Notification {
    return Notification(
        id = NotificationId(this.id),
        recipientUserId = this.recipientUserId,
        recipientEmail = this.recipientEmail,
        notificationType = NotificationType.fromString(this.notificationType),
        description = this.description,
        isRead = this.isRead,
        createdAt = this.createdAt,
        readAt = this.readAt,
        inviteId = this.inviteId?.let { InviteId(it) }
    )
}

/**
 * Maps an [InviteEntity] to the [Invite] model.
 */
@OptIn(SupabaseModel::class)
fun InviteEntity.toInvite(): Invite {
    return Invite(
        id = InviteId(this.id),
        email = this.email,
        organizationId = this.organizationId,
        role = this.role,
        expiration = this.expiration,
        inviteCode = this.inviteCode,
        invitedBy = this.invitedBy,
        acceptedAt = this.acceptedAt,
        unitId = this.unitId,
    )
}

/**
 * Creates a [DocumentEntity.CreateDocumentEntity] from the provided parameters.
 */
@OptIn(SupabaseModel::class)
fun CreateDocumentEntity(
    orgId: OrganizationId,
    propertyId: PropertyId?,
    unitId: UnitId?,
    filename: String,
    mimeType: String,
    documentType: DocumentType,
    assetId: String,
    createdBy: UserId?,
): DocumentEntity.CreateDocumentEntity {
    return DocumentEntity.CreateDocumentEntity(
        orgId = orgId,
        propertyId = propertyId,
        unitId = unitId,
        filename = filename,
        mimeType = mimeType,
        documentType = documentType.name,
        assetId = assetId,
        createdBy = createdBy,
    )
}

/**
 * Maps a [DocumentEntity] to the [Document] service model.
 */
@OptIn(SupabaseModel::class)
fun DocumentEntity.toDocument(): Document {
    return Document(
        id = DocumentId(this.documentId),
        orgId = this.orgId,
        propertyId = this.propertyId,
        unitId = this.unitId,
        filename = this.filename,
        mimeType = this.mimeType,
        documentType = enumValueOf<DocumentType>(this.documentType),
        assetId = this.assetId,
        createdBy = this.createdBy,
        createdAt = this.createdAt,
    )
}
