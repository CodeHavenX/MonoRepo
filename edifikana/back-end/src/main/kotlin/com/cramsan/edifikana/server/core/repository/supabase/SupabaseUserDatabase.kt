package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.edifikana.server.core.repository.UserDatabase
import com.cramsan.edifikana.server.core.repository.supabase.models.UserEntity
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePasswordRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateUserRequest
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import com.cramsan.framework.utils.password.generateRandomPassword
import io.github.jan.supabase.auth.admin.AdminApi
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.http.HttpStatusCode

/**
 * Database for managing users.
 */
@OptIn(SupabaseModel::class)
class SupabaseUserDatabase(
    private val adminApi: AdminApi,
    private val postgrest: Postgrest,
) : UserDatabase {

    /**
     * Creates a new user for the given [request]. Returns the [Result] of the operation with the created [User].
     */
    override suspend fun createUser(
        request: CreateUserRequest,
    ): Result<User> = runSuspendCatching(TAG) {
        logD(TAG, "Creating user: %s", request.email)

        val isPasswordAuthEnabled = request.password != null && request.password.isNotBlank()
        val password = if (isPasswordAuthEnabled) {
            request.password
        } else {
            // Generate a random password if not provided
            val generatedPassword = generateRandomPassword(LONG_PASSWORD)
            logD(TAG, "Generated password for user")
            generatedPassword
        }

        // Create the user in Supabase Auth
        val supabaseUser = try {
            adminApi.createUserWithEmail {
                email = request.email
                this.password = password
                autoConfirm = true
            }
        } catch (e: AuthRestException) {
            logD(TAG, "Error creating user: %s", e.message)
            if (e.statusCode == HttpStatusCode.UnprocessableEntity.value) {
                // Conflict error, user already exists
                throw ClientRequestExceptions.ConflictException(
                    message = "Error: User with email ${request.email} already exists.",
                )
            } else {
                throw e
            }
        }

        // Create the user entity in our database
        val requestEntity: UserEntity.CreateUserEntity = request.toUserEntity(
            supabaseUser.id,
            canPasswordAuth = isPasswordAuthEnabled,
        )
        val createdUser = createUserEntity(requestEntity)

        // Return the created user as a domain model
        createdUser.toUser(false)
    }

    /**
     * Retrieves a user for the given [request]. Returns the [Result] of the operation with the fetched [User] if found.
     */
    override suspend fun getUser(
        request: GetUserRequest,
    ): Result<User?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting user: %s", request.id)

        val userEntity = getUserImpl(request.id)

        userEntity?.toUser()
    }

    private suspend fun getUserImpl(id: UserId): UserEntity? {
        return postgrest.from(UserEntity.COLLECTION).select {
            filter {
                eq("id", id.userId)
            }
        }.decodeSingleOrNull<UserEntity>()
    }

    override suspend fun getUsers(): Result<List<User>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all users")

        postgrest.from(UserEntity.COLLECTION).select().decodeList<UserEntity>().map { it.toUser(false) }
    }

    /**
     * Updates a user with the given [request]. Returns the [Result] of the operation with the updated [User].
     */
    override suspend fun updateUser(
        request: UpdateUserRequest,
    ): Result<User> = runSuspendCatching(TAG) {
        logD(TAG, "Updating user: %s", request.id)

        postgrest.from(UserEntity.COLLECTION).update(
            {
                request.email?.let { value -> User::email setTo value }
            }
        ) {
            select()
            filter {
                UserEntity::id eq request.id.userId
            }
        }.decodeSingle<UserEntity>().toUser(false)
    }

    /**
     * Deletes a user with the given [request].
     * Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    override suspend fun deleteUser(
        request: DeleteUserRequest,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting user: %s", request.id)

        adminApi.deleteUser(request.id.userId)

        postgrest.from(UserEntity.COLLECTION).delete {
            select()
            filter {
                UserEntity::id eq request.id.userId
            }
        }.decodeSingleOrNull<UserEntity>() != null
    }

    override suspend fun updatePassword(request: UpdatePasswordRequest): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Updating password for user: %s", request.id)

        adminApi.updateUserById(request.id.userId) {
            password = request.password
        }

        true
    }

    private suspend fun createUserEntity(
        userEntity: UserEntity.CreateUserEntity,
    ): UserEntity {
        // Create the user entity in our database
        val createdUser = postgrest.from(UserEntity.COLLECTION).insert(userEntity) {
            select()
        }.decodeSingle<UserEntity>()
        logD(TAG, "User created userId: %s", createdUser.id)
        return createdUser
    }

    companion object {
        const val TAG = "SupabaseUserDatabase"
    }
}

private const val LONG_PASSWORD = 128
