package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.server.core.repository.UserDatabase
import com.cramsan.edifikana.server.core.repository.supabase.models.UserEntity
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePasswordRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateUserRequest
import com.cramsan.framework.assertlib.assertNull
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import javax.management.Query.eq

/**
 * Database for managing users.
 */
class SupabaseUserDatabase(
    private val auth: Auth,
    private val postgrest: Postgrest,
) : UserDatabase {

    /**
     * Creates a new user for the given [request]. Returns the [Result] of the operation with the created [User].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createUser(
        request: CreateUserRequest,
    ): Result<User> = runSuspendCatching(TAG) {
        logD(TAG, "Creating user: %S", request.email)

        val supabaseUser = auth.admin.createUserWithEmail {
            email = request.email
            password = request.password
            autoConfirm = true
        }

        val requestEntity: UserEntity.CreateUserEntity = request.toUserEntity(supabaseUser.id)

        val createdUser = postgrest.from(UserEntity.COLLECTION).insert(requestEntity) {
            select()
        }.decodeSingle<UserEntity>()
        logD(TAG, "User created userId: %S", createdUser.id)
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
                eq("id", request.id.userId)
            }
        }.decodeSingleOrNull<UserEntity>()

        userEntity?.toUser()
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getUsers(): Result<List<User>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all users")

        postgrest.from(UserEntity.COLLECTION).select().decodeList<UserEntity>().map { it.toUser() }
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
                request.email?.let { value -> User::email setTo value }
            }
        ) {
            select()
            filter {
                UserEntity::id eq request.id.userId
            }
        }.decodeSingle<UserEntity>().toUser()
    }

    /**
     * Deletes a user with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteUser(
        request: DeleteUserRequest,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting user: %S", request.id)

        auth.admin.deleteUser(request.id.userId)

        postgrest.from(UserEntity.COLLECTION).delete {
            select()
            filter {
                UserEntity::id eq request.id.userId
            }
        }.decodeSingleOrNull<UserEntity>() != null
    }

    override suspend fun updatePassword(request: UpdatePasswordRequest): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Updating password for user: %S", request.id)

        assertNull(auth.currentUserOrNull(), TAG, "We cannot have a user signed in on the BE")
        // Sign out the client by using
        // auth.signOut()

        auth.admin.updateUserById(request.id.userId) {
            password = request.password
        }

        true
    }

    companion object {
        const val TAG = "SupabaseUserDatabase"
    }
}