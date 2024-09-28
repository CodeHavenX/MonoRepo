package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.server.core.repository.supabase.models.UserEntity
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.UserId
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

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
 * Maps a [UserEntity] to the [User] model.
 */
@SupabaseModel
fun UserEntity.toUser(): User {
    return User(
        id = UserId(this.id),
        email = this.email,
    )
}

/**
 * Maps a [CreateUserRequest] to a [UserEntity].
 */
@OptIn(SupabaseModel::class)
fun CreateUserRequest.toUserEntity(): UserEntity {
    TODO()
}
