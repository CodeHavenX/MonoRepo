package com.cramsan.flyerboard.server.datastore.impl

import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.datastore.UserDatastore
import com.cramsan.flyerboard.server.datastore.entity.UserEntity
import com.cramsan.flyerboard.server.datastore.entity.toUser
import com.cramsan.flyerboard.server.service.models.User
import com.cramsan.framework.annotations.BackendDatastore
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.exception.PostgrestRestException

/**
 * Supabase implementation of [UserDatastore].
 */
@BackendDatastore
class SupabaseUserDatastore(private val postgrest: Postgrest) : UserDatastore {
    override suspend fun createUser(
        userId: UserId,
        firstName: String,
        lastName: String,
    ): Result<User> =
        runSuspendCatching(TAG) {
            logD(TAG, "Creating user: %s", userId)
            val entity =
                UserEntity.CreateUserEntity(
                    id = userId.userId,
                    firstName = firstName,
                    lastName = lastName,
                )
            try {
                postgrest
                    .from(UserEntity.COLLECTION)
                    .insert(entity) {
                        select()
                    }.decodeSingle<UserEntity>()
                    .toUser()
            } catch (e: PostgrestRestException) {
                if (e.code == POSTGRES_UNIQUE_VIOLATION) {
                    throw ClientRequestExceptions.ForbiddenException(
                        "User already exists: ${userId.userId}",
                        e,
                    )
                }
                throw e
            }
        }

    override suspend fun getUser(userId: UserId): Result<User> =
        runSuspendCatching(TAG) {
            logD(TAG, "Getting user: %s", userId)
            postgrest
                .from(UserEntity.COLLECTION)
                .select {
                    filter {
                        UserEntity::id eq userId.userId
                    }
                }.decodeSingleOrNull<UserEntity>()
                ?.toUser()
                ?: throw ClientRequestExceptions.NotFoundException("User not found: ${userId.userId}")
        }

    companion object {
        private const val TAG = "SupabaseUserDatastore"
        private const val POSTGRES_UNIQUE_VIOLATION = "23505"
    }
}
