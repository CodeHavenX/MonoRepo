@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.datastore.supabase.models.AuthMetadataEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.EventLogEntryEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.OrganizationEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.PropertyEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.StaffEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.TimeCardEventEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.UserEntity
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.Organization
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.UserRole
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
fun CreateStaffEntity(
    idType: IdType,
    firstName: String,
    lastName: String,
    role: StaffRole,
    propertyId: PropertyId,
): StaffEntity.CreateStaffEntity {
    return StaffEntity.CreateStaffEntity(
        idType = idType,
        firstName = firstName,
        lastName = lastName,
        role = role,
        propertyId = propertyId.propertyId,
    )
}

/**
 * Maps a [StaffEntity] to the [Staff] model.
 */
@OptIn(SupabaseModel::class)
fun StaffEntity.toStaff(): Staff {
    return Staff(
        id = StaffId(this.id),
        idType = this.idType,
        firstName = this.firstName,
        lastName = this.lastName,
        role = this.role,
        propertyId = PropertyId(this.propertyId),
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
): PropertyEntity.CreatePropertyEntity {
    return PropertyEntity.CreatePropertyEntity(
        name = name,
        address = address,
        organizationId = organizationId.id,
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
        organizationId = OrganizationId(this.organizationId),
    )
}

/**
 * Creates a [TimeCardEventEntity.CreateTimeCardEventEntity] from the provided parameters.
 */
@OptIn(SupabaseModel::class)
fun CreateTimeCardEventEntity(
    staffId: StaffId,
    fallbackStaffName: String?,
    propertyId: PropertyId,
    type: TimeCardEventType,
    imageUrl: String?,
    timestamp: Instant,
): TimeCardEventEntity.CreateTimeCardEventEntity {
    return TimeCardEventEntity.CreateTimeCardEventEntity(
        staffId = staffId.staffId,
        fallbackStaffName = fallbackStaffName,
        propertyId = propertyId.propertyId,
        type = type,
        imageUrl = imageUrl,
        timestamp = timestamp.epochSeconds,
    )
}

/**
 * Maps a [TimeCardEventEntity] to the [TimeCardEvent] model.
 */
@OptIn(SupabaseModel::class)
fun TimeCardEventEntity.toTimeCardEvent(): TimeCardEvent {
    return TimeCardEvent(
        id = TimeCardEventId(this.id),
        staffId = this.staffId?.let { StaffId(it) },
        fallbackStaffName = this.fallbackStaffName,
        propertyId = PropertyId(this.propertyId),
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
    staffId: StaffId?,
    fallbackStaffName: String?,
    propertyId: PropertyId,
    type: EventLogEventType,
    fallbackEventType: String?,
    timestamp: Instant,
    title: String,
    description: String?,
    unit: String,
): EventLogEntryEntity.CreateEventLogEntryEntity {
    return EventLogEntryEntity.CreateEventLogEntryEntity(
        staffId = staffId?.staffId,
        fallbackStaffName = fallbackStaffName,
        propertyId = propertyId.propertyId,
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
        staffId = this.staffId?.let { StaffId(it) },
        fallbackStaffName = this.fallbackStaffName,
        propertyId = PropertyId(this.propertyId),
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
)
