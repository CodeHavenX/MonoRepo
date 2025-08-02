@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.datastore.supabase.models.AuthMetadataEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.EventLogEntryEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.PropertyEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.StaffEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.TimeCardEventEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.UserEntity
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.requests.AssociateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.CreateEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.CreatePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.CreateStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.CreateTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.framework.ammotations.SupabaseModel
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
        )
    )
}

/**
 * Maps a [CreateUserRequest] to the [UserEntity.CreateUserEntity] model.
 * This is used to create a new user in the database.
 */
@OptIn(SupabaseModel::class)
fun CreateUserRequest.toUserEntity(
    userId: UserId,
    pendingAssociation: Boolean,
    canPasswordAuth: Boolean,
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
        ),
    )
}

/**
 * Maps an [AssociateUserRequest] to the [UserEntity.CreateUserEntity] model.
 * This is used to associate a user from another service with a new user in our system.
 */
@OptIn(SupabaseModel::class)
fun AssociateUserRequest.toUserEntity(
    userId: UserId,
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
 * Maps a [CreateStaffRequest] to the [Staff] model.
 */
@OptIn(SupabaseModel::class)
fun CreateStaffRequest.toStaffEntity(): StaffEntity.CreateStaffEntity {
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
fun CreatePropertyRequest.toPropertyEntity(): PropertyEntity.CreatePropertyEntity {
    return PropertyEntity.CreatePropertyEntity(
        name = name,
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
    )
}

/**
 * Maps a [CreateTimeCardEventRequest] to the [TimeCardEventEntity.CreateTimeCardEventEntity] model.
 */
@OptIn(SupabaseModel::class)
fun CreateTimeCardEventRequest.toTimeCardEventEntity(): TimeCardEventEntity.CreateTimeCardEventEntity {
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
 * Maps a [CreateEventLogEntryRequest] to the [EventLogEntryEntity.CreateEventLogEntryEntity] model.
 */
@OptIn(SupabaseModel::class)
fun CreateEventLogEntryRequest.toEventLogEntryEntity(): EventLogEntryEntity.CreateEventLogEntryEntity {
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
