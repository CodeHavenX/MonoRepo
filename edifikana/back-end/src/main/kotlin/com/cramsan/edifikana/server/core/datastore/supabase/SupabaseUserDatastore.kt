package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.edifikana.server.core.datastore.UserDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.models.AuthMetadataEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.UserEntity
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.requests.AssociateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePasswordRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateUserRequest
import com.cramsan.framework.core.Hashing
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.ammotations.SupabaseModel
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logW
import com.cramsan.framework.utils.loginvalidation.validatePassword
import com.cramsan.framework.utils.uuid.UUID
import io.github.jan.supabase.auth.admin.AdminApi
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.http.HttpStatusCode

/**
 * Datastore for managing users.
 */
@OptIn(SupabaseModel::class)
class SupabaseUserDatastore(
    private val adminApi: AdminApi,
    private val postgrest: Postgrest,
) : UserDatastore {

    /**
     * Creates a new user for the given [request]. Returns the [Result] of the operation with the created [User].
     */
    override suspend fun createUser(
        request: CreateUserRequest,
    ): Result<User> = runSuspendCatching(TAG) {
        logD(TAG, "Creating user: %s", request.email)

        val createOtpAccount = request.password.isNullOrBlank()

        val userId = if (!createOtpAccount) {
            // Create the user in Supabase Auth
            try {
                val supabaseUserInfo = adminApi.createUserWithEmail {
                    email = request.email
                    password = request.password
                    autoConfirm = true
                }
                supabaseUserInfo.id
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
        } else {
            UUID.random().also {
                logD(TAG, "Creating user without password, using UUID: %s", it)
            }
        }

        // Create the user entity in our database
        val requestEntity: UserEntity.CreateUserEntity = request.toUserEntity(
            UserId(userId),
            pendingAssociation = createOtpAccount,
            canPasswordAuth = !createOtpAccount,
        )
        val createdUser = createUserEntity(requestEntity)

        // Return the created user as a domain model
        createdUser.toUser()
    }

    override suspend fun associateUser(request: AssociateUserRequest): Result<User> = runSuspendCatching(TAG) {
        logD(TAG, "Associating user: %s", request.email)

        val supabaseUser = runCatching { adminApi.retrieveUserById(request.userId.userId) }.getOrNull()
        // Check that the user exists in Supabase Auth
        if (supabaseUser == null) {
            logD(TAG, "Supabase user not found with ID: %s", request.userId)
            throw ClientRequestExceptions.NotFoundException(
                message = "Error: User with ID ${request.userId} does not exist in Supabase.",
            )
        }

        // Check if the user already exists in our database by email. We need to that there is one entry for a temp
        // user pending association.
        val temporaryUser = getUserByEmail(request.email)
        if (temporaryUser != null) {
            if (!temporaryUser.authMetadata.pendingAssociation) {
                logW(TAG, "User already exists in our database with email: ${request.email}")
                throw ClientRequestExceptions.ConflictException(
                    message = "Error: User with email ${request.email} already exists in our database.",
                )
            }
        } else {
            logD(TAG, "No existing user found with email: %s", request.email)
            throw ClientRequestExceptions.NotFoundException(
                message = "Error: User with email ${request.email} not found in our database.",
            )
        }

        // Create the new user entity in our database
        val requestEntity: UserEntity.CreateUserEntity = request.toUserEntity(
            userId = request.userId,
            userEntity = temporaryUser,
        )
        val createdUser = createUserEntity(requestEntity)

        if (deleteUser(DeleteUserRequest(UserId(temporaryUser.id))).isFailure) {
            logW(TAG, "Failed to delete temporary user with email: ${request.email}")
            error("Failed to delete temporary user with.")
        }

        // Return the created user as a domain model
        createdUser.toUser()
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

    private suspend fun getUserByEmail(email: String): UserEntity? {
        return postgrest.from(UserEntity.COLLECTION).select {
            filter {
                eq("email", email)
            }
        }.decodeSingleOrNull<UserEntity>()
    }

    override suspend fun getUsers(): Result<List<User>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all users")

        postgrest.from(UserEntity.COLLECTION).select().decodeList<UserEntity>().map { it.toUser() }
    }

    /**
     * Updates a user with the given [request]. Returns the [Result] of the operation with the updated [User].
     */
    override suspend fun updateUser(
        request: UpdateUserRequest,
    ): Result<User> = runSuspendCatching(TAG) {
        logD(TAG, "Updating user: %s", request.id)

        updateUserImpl(
            request.id,
            email = request.email,
        ).toUser()
    }

    private suspend fun updateUserImpl(
        id: UserId,
        email: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        phoneNumber: String? = null,
        authMetadata: AuthMetadataEntity? = null,
    ): UserEntity {
        return postgrest.from(UserEntity.COLLECTION).update(
            {
                email?.let { UserEntity::email setTo it }
                firstName?.let { UserEntity::firstName setTo it }
                lastName?.let { UserEntity::lastName setTo it }
                phoneNumber?.let { UserEntity::phoneNumber setTo it }
                authMetadata?.let { UserEntity::authMetadata setTo it }
            }
        ) {
            select()
            filter {
                UserEntity::id eq id.userId
            }
        }.decodeSingle<UserEntity>()
    }

    /**
     * Deletes a user with the given [request].
     * Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    override suspend fun deleteUser(
        request: DeleteUserRequest,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting user: %s", request.id)

        val user = getUserImpl(request.id) ?: throw ClientRequestExceptions.NotFoundException(
            message = "Error: User with ID ${request.id} not found in our database.",
        )

        // Check if the user is pending association with a supabase auth
        // if the user is pending association, then there is no user to delete in Supabase Auth
        if (!user.authMetadata.pendingAssociation) {
            adminApi.deleteUser(request.id.userId)
        }

        postgrest.from(UserEntity.COLLECTION).delete {
            select()
            filter {
                UserEntity::id eq request.id.userId
            }
        }.decodeSingleOrNull<UserEntity>() != null
    }

    @OptIn(SecureStringAccess::class)
    override suspend fun updatePassword(request: UpdatePasswordRequest): Result<Unit> = runSuspendCatching(TAG) {
        logD(TAG, "Updating password for user: %s", request.id)

        val user = getUserImpl(request.id) ?: throw ClientRequestExceptions.NotFoundException(
            message = "Error: User with ID ${request.id} not found in our database.",
        )

        // If the user has a password set, we need to check for the hash of the current password
        if (user.authMetadata.canPasswordAuth) {
            if (request.currentHashedPassword == null) {
                throw ClientRequestExceptions.InvalidRequestException(
                    message = "Error: Current password is required for password update.",
                )
            }

            val requestCurrentHashedPassword = request.currentHashedPassword.reveal()

            if (requestCurrentHashedPassword != user.authMetadata.hashedPassword) {
                logW(TAG, "Current password does not match for user: ${request.id}")
                throw ClientRequestExceptions.UnauthorizedException("Error: Current password is incorrect.")
            }
        }

        val passwordErrors = validatePassword(request.newPassword.reveal())
        if (passwordErrors.isNotEmpty()) {
            logW(TAG, "Password validation failed for user: ${request.id}, with ${passwordErrors.size} errors.")
            throw ClientRequestExceptions.InvalidRequestException(
                message = "Error: Password validation failed for user ${request.id}. "
            )
        }

        val newHashedPassword = Hashing.insecureHash(request.newPassword.reveal().encodeToByteArray()).toString()

        adminApi.updateUserById(user.id) {
            password = request.newPassword.reveal()
        }

        updateUserImpl(
            request.id,
            authMetadata = user.authMetadata.copy(
                hashedPassword = newHashedPassword,
                canPasswordAuth = true,
            )
        )
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
        const val TAG = "SupabaseUserDatastore"
    }
}
