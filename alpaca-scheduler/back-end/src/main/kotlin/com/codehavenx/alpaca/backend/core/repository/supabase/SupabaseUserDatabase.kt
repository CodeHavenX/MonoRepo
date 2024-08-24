package com.codehavenx.alpaca.backend.core.repository.supabase

import com.codehavenx.alpaca.backend.core.repository.UserDatabase
import com.codehavenx.alpaca.backend.core.repository.supabase.models.UserEntity
import com.codehavenx.alpaca.backend.core.service.models.User
import com.codehavenx.alpaca.backend.core.service.models.requests.CreateUserRequest
import com.codehavenx.alpaca.backend.core.service.models.requests.DeleteUserRequest
import com.codehavenx.alpaca.backend.core.service.models.requests.GetUserRequest
import com.codehavenx.alpaca.backend.core.service.models.requests.UpdateUserRequest
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Database for managing users.
 */
class SupabaseUserDatabase(
    private val postgrest: Postgrest,
) : UserDatabase {

    /**
     * Creates a new user for the given [request]. Returns the [Result] of the operation with the created [User].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createUser(
        request: CreateUserRequest,
    ): Result<User> = runSuspendCatching(TAG) {
        logD(TAG, "Creating user: %S", request.username)
        val requestEntity = request.toUserEntity()

        val createdUser = postgrest.from(UserEntity.COLLECTION).insert(requestEntity) {
            select()
        }
            .decodeSingle<UserEntity>()
        logD(TAG, "User created userId=%S", createdUser.id)
        createdUser.toUser()
    }

    /**
     * Retrieves a user for the given [request]. Returns the [Result] of the operation with the fetched [User] if found.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getUser(
        request: GetUserRequest,
    ): Result<User?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting user: %S", request.id)

        val userEntity = postgrest.from(UserEntity.COLLECTION).select {
            filter {
                UserEntity::id eq request.id
            }
            limit(1)
            single()
        }.decodeAsOrNull<UserEntity>()

        userEntity?.toUser()
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getUsers(): Result<List<User>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all users")

        postgrest.from(UserEntity.COLLECTION).select {
            select()
        }.decodeList<UserEntity>().map { it.toUser() }
    }

    /**
     * Updates a user with the given [request]. Returns the [Result] of the operation with the updated [User].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updateUser(
        request: UpdateUserRequest,
    ): Result<User> = runSuspendCatching(TAG) {
        logD(TAG, "Updating user: %S", request.id)

        postgrest.from(UserEntity.COLLECTION).update(
            {
                request.username?.let { value -> User::username setTo value }
            }
        ) {
            select()
            filter {
                UserEntity::id eq request.id
            }
        }.decodeAs<UserEntity>().toUser()
    }

    /**
     * Deletes a user with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteUser(
        request: DeleteUserRequest,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting user: %S", request.id)

        postgrest.from(UserEntity.COLLECTION).delete {
            select()
            filter {
                UserEntity::id eq request.id
            }
        }.decodeSingleOrNull<UserEntity>() != null
    }

    companion object {
        const val TAG = "SupabaseUserDatabase"
    }
}
