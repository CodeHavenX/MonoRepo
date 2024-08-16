package com.codehavenx.alpaca.backend.storage

import com.codehavenx.alpaca.backend.models.AppointmentConfiguration
import com.codehavenx.alpaca.backend.models.AppointmentType
import com.codehavenx.alpaca.backend.models.Event
import com.codehavenx.alpaca.backend.models.StaffId
import com.codehavenx.alpaca.backend.models.User
import com.codehavenx.alpaca.backend.models.UserId
import com.codehavenx.alpaca.backend.storage.entity.ConfigurationEntity
import com.codehavenx.alpaca.backend.storage.entity.EventEntity
import com.codehavenx.alpaca.backend.storage.entity.SupabaseModel
import com.codehavenx.alpaca.backend.storage.entity.UserEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun createEventEntity(
    owner: StaffId,
    attendants: Set<String>,
    title: String,
    description: String,
    startTime: LocalDateTime,
    endTime: LocalDateTime,
    timeZone: TimeZone,
    uniqueIdProvider: () -> String,
): EventEntity {
    return EventEntity(
        id = uniqueIdProvider(),
        owner = owner.staffId,
        attendants = attendants,
        title = title,
        description = description,
        startTime = startTime.toLong(timeZone),
        endTime = endTime.toLong(timeZone),
    )
}

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

fun Event.toEventEntity(timeZone: TimeZone): EventEntity {
    return EventEntity(
        id = this.id,
        owner = owner.staffId,
        attendants = attendants.map { it.userId }.toSet(),
        title = title,
        description = description,
        startTime = startDateTime.toLong(timeZone),
        endTime = endDateTime.toLong(timeZone),
    )
}

fun LocalDateTime.toLong(timeZone: TimeZone): Long {
    return this.toInstant(timeZone).toEpochMilliseconds()
}

fun Long.toLocalDateTime(timeZone: TimeZone): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
}

fun ConfigurationEntity.toConfiguration(): AppointmentConfiguration {
    return AppointmentConfiguration(
        id = id,
        appointmentType = AppointmentType(appointmentType),
        duration = duration.seconds,
        timeZone = TimeZone.of(timeZone),
    )
}

fun AppointmentConfiguration.toConfigurationEntity(): ConfigurationEntity {
    return ConfigurationEntity(
        id = id,
        appointmentType = appointmentType.appointmentType,
        duration = duration.inWholeSeconds,
        timeZone = timeZone.id,
    )
}

fun createConfigurationEntity(
    appointmentType: AppointmentType,
    duration: Duration,
    timeZone: TimeZone,
    uniqueIdProvider: () -> String,
): ConfigurationEntity {
    return ConfigurationEntity(
        id = uniqueIdProvider(),
        appointmentType = appointmentType.appointmentType,
        duration = duration.inWholeSeconds,
        timeZone = timeZone.id,
    )
}

@SupabaseModel
fun User.toUserEntity(): UserEntity {
    return UserEntity(
        id = this.id.userId,
        username = username,
    )
}

@SupabaseModel
fun UserEntity.toUser(): User {
    return User(
        id = UserId(this.id),
        username = this.username,
    )
}
