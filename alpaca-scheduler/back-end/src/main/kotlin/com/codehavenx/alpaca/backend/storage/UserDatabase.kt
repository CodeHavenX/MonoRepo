package com.codehavenx.alpaca.backend.storage

import com.codehavenx.alpaca.backend.models.User
import com.codehavenx.alpaca.backend.models.UserId
import com.codehavenx.alpaca.backend.storage.entity.COLLECTION
import com.codehavenx.alpaca.backend.storage.entity.CreateUserEntity
import com.codehavenx.alpaca.backend.storage.entity.SupabaseModel
import com.codehavenx.alpaca.backend.storage.entity.UserEntity
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.datetime.Clock

@Suppress("UnusedParameter", "UnusedPrivateProperty", "RedundantSuspendModifier")
class UserDatabase(
    private val postgrest: Postgrest,
    private val clock: Clock,
) {

    @OptIn(SupabaseModel::class)
    suspend fun createUser(
        name: String,
    ): Result<User> = runSuspendCatching(TAG) {
        logD(TAG, "Creating user: %S", name)
        val userRequest = CreateUserEntity(
            username = name,
        )

        val createdUser = postgrest.from(COLLECTION)
            .insert(userRequest) {
                select()
            }
            .decodeSingle<UserEntity>()
        logD(TAG, "User created userId=%S", createdUser.id)
        createdUser.toUser()
    }

    @OptIn(SupabaseModel::class)
    suspend fun getUser(userId: UserId): Result<User?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting user: %S", userId)

        postgrest.from(COLLECTION)
            .select {
                filter {
                    eq("id", userId.userId)
                }
                limit(1)
                single()
            }
            .decodeAs<UserEntity>()
            .toUser()
    }

    @OptIn(SupabaseModel::class)
    suspend fun updateUser(user: User): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Updating user: %S", user.id)
        val userEntity = user.toUserEntity()

        postgrest.from(COLLECTION)
            .update(userEntity)
            .decodeAsOrNull<UserEntity>() != null
    }

    @OptIn(SupabaseModel::class)
    suspend fun deleteUser(userId: UserId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting user: %S", userId)

        postgrest.from(COLLECTION)
            .delete {
                filter {
                    eq("id", userId.userId)
                }
            }.decodeAsOrNull<UserEntity>() != null
    }

    companion object {
        private const val TAG = "UserDatabase"
    }
}
