package com.codehavenx.platform.bot.storage

import com.codehavenx.platform.bot.domain.models.AppointmentConfiguration
import com.codehavenx.platform.bot.domain.models.AppointmentType
import com.codehavenx.platform.bot.domain.models.Event
import com.codehavenx.platform.bot.domain.models.StaffId
import com.codehavenx.platform.bot.domain.models.User
import com.codehavenx.platform.bot.domain.models.UserId
import com.codehavenx.platform.bot.storage.entity.ConfigurationEntity
import com.codehavenx.platform.bot.storage.entity.EventEntity
import com.codehavenx.platform.bot.storage.entity.UserEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.bson.types.ObjectId
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
    objectIdProvider: () -> ObjectId,
): EventEntity {
    return EventEntity(
        id = objectIdProvider(),
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
        id = this.id.toHexString() ?: TODO(),
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
        id = ObjectId(this.id),
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
        id = id.toHexString(),
        appointmentType = AppointmentType(appointmentType),
        duration = duration.seconds,
        timeZone = TimeZone.of(timeZone),
    )
}

fun AppointmentConfiguration.toConfigurationEntity(): ConfigurationEntity {
    return ConfigurationEntity(
        id = ObjectId(id),
        appointmentType = appointmentType.appointmentType,
        duration = duration.inWholeSeconds,
        timeZone = timeZone.id,
    )
}

fun createConfigurationEntity(
    appointmentType: AppointmentType,
    duration: Duration,
    timeZone: TimeZone,
    objectIdProvider: () -> ObjectId,
): ConfigurationEntity {
    return ConfigurationEntity(
        id = objectIdProvider(),
        appointmentType = appointmentType.appointmentType,
        duration = duration.inWholeSeconds,
        timeZone = timeZone.id,
    )
}

fun User.toUserEntity(): UserEntity {
    return UserEntity(
        id = ObjectId(this.userId.userId),
        name = name,
    )
}

fun UserEntity.toUser(): User {
    return User(
        userId = UserId(this.id.toHexString()),
        name = this.name,
    )
}
