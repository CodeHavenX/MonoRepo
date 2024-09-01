package com.codehavenx.alpaca.backend.core.repository.supabase

import com.codehavenx.alpaca.backend.core.repository.supabase.models.AddressEntity
import com.codehavenx.alpaca.backend.core.repository.supabase.models.ConfigurationEntity
import com.codehavenx.alpaca.backend.core.repository.supabase.models.EventEntity
import com.codehavenx.alpaca.backend.core.repository.supabase.models.UserEntity
import com.codehavenx.alpaca.backend.core.service.models.Address
import com.codehavenx.alpaca.backend.core.service.models.AppointmentConfiguration
import com.codehavenx.alpaca.backend.core.service.models.AppointmentType
import com.codehavenx.alpaca.backend.core.service.models.Event
import com.codehavenx.alpaca.backend.core.service.models.StaffId
import com.codehavenx.alpaca.backend.core.service.models.User
import com.codehavenx.alpaca.backend.core.service.models.UserId
import com.codehavenx.alpaca.backend.core.service.models.requests.CreateConfigurationRequest
import com.codehavenx.alpaca.backend.core.service.models.requests.CreateEventRequest
import com.codehavenx.alpaca.backend.core.service.models.requests.CreateUserRequest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.seconds

/**
 * Maps a LocalDateTime and a [timeZone] to a long.
 */
fun LocalDateTime.toLong(timeZone: TimeZone): Long {
    return this.toInstant(timeZone).toEpochMilliseconds()
}

/**
 * Maps a long and a [timeZone] to a LocalDateTime.
 */
fun Long.toLocalDateTime(timeZone: TimeZone): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
}

/**
 * Maps a [CreateEventRequest] to a [EventEntity.CreateEventEntity].
 */
@OptIn(SupabaseModel::class)
fun CreateEventRequest.toEventEntity(): EventEntity.CreateEventEntity {
    return EventEntity.CreateEventEntity(
        owner = owner.staffId,
        attendants = attendants,
        title = title,
        description = description,
        startTime = startTime.toLong(timeZone),
        endTime = endTime.toLong(timeZone),
    )
}

/**
 * Maps an [EventEntity] to the [Event] model.
 */
@OptIn(SupabaseModel::class)
fun EventEntity.toEvent(timeZone: TimeZone): Event {
    return Event(
        id = this.id,
        owner = StaffId(owner),
        attendants = attendants.map { UserId(it) }.toSet(),
        title = title,
        description = description,
        startDateTime = startTime.toLocalDateTime(timeZone),
        endDateTime = endTime.toLocalDateTime(timeZone),
    )
}

/**
 * Maps a [ConfigurationEntity] to the [AppointmentConfiguration] model.
 */
@OptIn(SupabaseModel::class)
fun ConfigurationEntity.toConfiguration(): AppointmentConfiguration {
    return AppointmentConfiguration(
        id = id,
        appointmentType = AppointmentType(appointmentType),
        duration = duration.seconds,
        timeZone = TimeZone.of(timeZone),
    )
}

/**
 * Maps a [UserEntity] to the [User] model.
 */
@SupabaseModel
fun UserEntity.toUser(): User {
    return User(
        id = UserId(this.id),
        isVerified = this.isVerified,
        username = this.username,
        firstName = this.firstName,
        lastName = this.lastName,
        address = this.address?.toAddress(),
        phoneNumbers = this.phoneNumbers,
        emails = this.emails,
    )
}

/**
 * Maps an [AddressEntity] to the [Address] model.
 */
@SupabaseModel
private fun AddressEntity.toAddress(): Address {
    return Address(
        streetAddress = this.streetAddress,
        unit = this.unit,
        city = this.city,
        state = this.state,
        zipCode = this.zipCode,
        country = this.country
    )
}

/**
 * Maps a [CreateUserRequest] to a [UserEntity.CreateUserEntity].
 */
@OptIn(SupabaseModel::class)
fun CreateUserRequest.toUserEntity(): UserEntity.CreateUserEntity {
    return UserEntity.CreateUserEntity(
        username = username,
        phoneNumbers = phoneNumbers,
        emails = emails,
    )
}

/**
 * Maps a [CreateConfigurationRequest] to a [ConfigurationEntity].
 */
@OptIn(SupabaseModel::class)
fun CreateConfigurationRequest.toConfigurationEntity(): ConfigurationEntity {
    TODO("Not yet implemented")
}
