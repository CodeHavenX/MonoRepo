package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.utils.requireNotBlank
import com.cramsan.edifikana.server.datastore.UserDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.AuthMetadataEntity
import com.cramsan.edifikana.server.datastore.supabase.models.InviteEntity
import com.cramsan.edifikana.server.datastore.supabase.models.UserEntity
import com.cramsan.edifikana.server.datastore.supabase.models.UserOrganizationMappingEntity
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.assertlib.assert
import com.cramsan.framework.core.Hashing
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logW
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import com.cramsan.framework.utils.loginvalidation.validatePassword
import com.cramsan.framework.utils.uuid.UUID
import io.github.jan.supabase.auth.admin.AdminApi
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.http.HttpStatusCode
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Datastore for managing users.
 */
@OptIn(SupabaseModel::class)
class SupabaseUserDatastore(
    private val adminApi: AdminApi,
    private val postgrest: Postgrest,
    private val clock: Clock,
) : UserDatastore {

    /**
     * Creates a new user for the given [request]. Returns the [Result] of the operation with the created [User].
     */
    @OptIn(SecureStringAccess::class)
    override suspend fun createUser(
        email: String,
        phoneNumber: String,
        password: String?,
        firstName: String,
        lastName: String,
        isTransient: Boolean,
    ): Result<User> = runSuspendCatching(TAG) {
        logD(TAG, "Creating user: %s", email)

        val createOtpAccount = isTransient

        // Validate that if the account is not transient, a password is provided
        when (isTransient) {
            true -> assert(password.isNullOrBlank(), TAG, "Transient accounts must not have a password")
            false -> assert(!password.isNullOrBlank(), TAG, "Non-transient accounts must have a password")
        }

        val userId = if (!createOtpAccount) {
            // Create the user in Supabase Auth
            try {
                val supabaseUserInfo = adminApi.createUserWithEmail {
                    this.email = email
                    this.password = password.orEmpty()
                    autoConfirm = true
                }
                supabaseUserInfo.id
            } catch (e: AuthRestException) {
                logD(TAG, "Error creating user: %s", e.message)
                if (e.statusCode == HttpStatusCode.UnprocessableEntity.value) {
                    // Conflict error, user already exists
                    throw ClientRequestExceptions.ConflictException(
                        message = "Error: User with email $email already exists.",
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
        val requestEntity: UserEntity.CreateUserEntity = CreateUserEntity(
            userId = UserId(userId),
            email = email,
            phoneNumber = phoneNumber,
            firstName = firstName,
            lastName = lastName,
            pendingAssociation = createOtpAccount,
            canPasswordAuth = !createOtpAccount,
            hashedPassword = if (createOtpAccount) {
                null
            } else {
                // We verified above that if the account is not transient, a password is provided
                SecureString(Hashing.insecureHash(requireNotBlank(password).encodeToByteArray()).toString())
            }
        )
        val createdUser = createUserEntity(requestEntity)

        // Return the created user as a domain model
        createdUser.toUser()
    }

    override suspend fun associateUser(
        userId: UserId,
        email: String,
    ): Result<User> = runSuspendCatching(TAG) {
        logD(TAG, "Associating user: %s", email)

        val supabaseUser = runCatching { adminApi.retrieveUserById(userId.userId) }.getOrNull()
        // Check that the user exists in Supabase Auth
        if (supabaseUser == null) {
            logD(TAG, "Supabase user not found with ID: %s", userId)
            throw ClientRequestExceptions.NotFoundException(
                message = "Error: User with ID $userId does not exist in Supabase.",
            )
        }

        // Check if the user already exists in our database by email. We need to that there is one entry for a temp
        // user pending association.
        val temporaryUser = getUserByEmail(email)
        if (temporaryUser != null) {
            if (!temporaryUser.authMetadata.pendingAssociation) {
                logW(TAG, "User already exists in our database with email: $email")
                throw ClientRequestExceptions.ConflictException(
                    message = "Error: User with email $email already exists in our database.",
                )
            }
        } else {
            logD(TAG, "No existing user found with email: %s", email)
            throw ClientRequestExceptions.NotFoundException(
                message = "Error: User with email $email not found in our database.",
            )
        }

        // Create the new user entity in our database
        val requestEntity: UserEntity.CreateUserEntity = CreateUserEntity(
            userId = userId,
            email = email,
            userEntity = temporaryUser,
        )
        val createdUser = createUserEntity(requestEntity)

        if (deleteUser(UserId(temporaryUser.id)).isFailure) {
            logW(TAG, "Failed to delete temporary user with email: $email")
            error("Failed to delete temporary user with.")
        }

        // Return the created user as a domain model
        createdUser.toUser()
    }

    /**
     * Retrieves a user for the given [request]. Returns the [Result] of the operation with the fetched [User] if found.
     */
    override suspend fun getUser(
        id: UserId,
    ): Result<User?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting user: %s", id)

        val userEntity = getUserImpl(id)

        userEntity?.toUser()
    }

    override suspend fun getUser(email: String): Result<User?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting user by email: %s", email)
        val userEntity = getUserByEmail(email)
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

    override suspend fun getUsers(
        organizationId: OrganizationId,
    ): Result<List<User>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all users")

        val organizations = postgrest.from(UserOrganizationMappingEntity.COLLECTION).select(
            // HINT: Here we are using the POSTgREST feature to select related rows and spread them into the result.
            // https://supabase.com/blog/postgrest-11-prerelease
            Columns.list("...${UserEntity.COLLECTION}(*)")
        ) {
            filter {
                eq("organization_id", organizationId.id)
            }
        }
        organizations.decodeList<UserEntity>().map { it.toUser() }
    }

    /**
     * Updates a user with the given [request]. Returns the [Result] of the operation with the updated [User].
     */
    override suspend fun updateUser(
        id: UserId,
        email: String?,
    ): Result<User> = runSuspendCatching(TAG) {
        logD(TAG, "Updating user: %s", id)

        updateUserImpl(
            id,
            email = email,
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
        id: UserId,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting user: %s", id)

        val user = getUserImpl(id) ?: throw ClientRequestExceptions.NotFoundException(
            message = "Error: User with ID $id not found in our database.",
        )

        // Check if the user is pending association with a supabase auth
        // if the user is pending association, then there is no user to delete in Supabase Auth
        if (!user.authMetadata.pendingAssociation) {
            adminApi.deleteUser(id.userId)
        }

        postgrest.from(UserEntity.COLLECTION).delete {
            select()
            filter {
                UserEntity::id eq id.userId
            }
        }.decodeSingleOrNull<UserEntity>() != null
    }

    @OptIn(SecureStringAccess::class)
    override suspend fun updatePassword(
        id: UserId,
        currentHashedPassword: SecureString?,
        newPassword: SecureString,
    ): Result<Unit> = runSuspendCatching(TAG) {
        logD(TAG, "Updating password for user: %s", id)

        val user = getUserImpl(id) ?: throw ClientRequestExceptions.NotFoundException(
            message = "Error: User with ID $id not found in our database.",
        )

        // If the user has a password set, we need to check for the hash of the current password
        if (user.authMetadata.canPasswordAuth) {
            if (user.authMetadata.hashedPassword == null) {
                logE(TAG, "User's canPasswordAuth is set to true but hashedPassword is null")
                error("Illegal password state for $id")
            }

            if (currentHashedPassword == null) {
                throw ClientRequestExceptions.InvalidRequestException(
                    message = "Error: Current password is required for password update.",
                )
            }

            val requestCurrentHashedPassword = currentHashedPassword.reveal()

            if (requestCurrentHashedPassword != user.authMetadata.hashedPassword) {
                logW(TAG, "Current password does not match for user: $id")
                throw ClientRequestExceptions.UnauthorizedException("Error: Current password is incorrect.")
            }
        }

        val passwordErrors = validatePassword(newPassword.reveal())
        if (passwordErrors.isNotEmpty()) {
            logW(TAG, "Password validation failed for user: $id, with ${passwordErrors.size} errors.")
            throw ClientRequestExceptions.InvalidRequestException(
                message = "Error: Password validation failed for user $id. "
            )
        }

        val newHashedPassword = Hashing.insecureHash(newPassword.reveal().encodeToByteArray()).toString()

        adminApi.updateUserById(user.id) {
            password = newPassword.reveal()
        }

        updateUserImpl(
            id,
            authMetadata = user.authMetadata.copy(
                hashedPassword = newHashedPassword,
                canPasswordAuth = true,
            )
        )
    }

    override suspend fun recordInvite(
        email: String,
        organizationId: OrganizationId,
        expiration: Instant,
    ): Result<Invite> = runSuspendCatching(TAG) {
        logD(TAG, "Recording invite for email: %s", email)

        val inviteEntity = InviteEntity.Create(
            email = email,
            organizationId = organizationId.id,
            createdAt = clock.now(),
            expiration = expiration,
        )

        val data = postgrest.from(InviteEntity.COLLECTION).insert(inviteEntity) {
            select()
        }
        data.decodeSingle<InviteEntity>().toInvite()
    }

    override suspend fun getInvites(organizationId: OrganizationId): Result<List<Invite>> {
        return runSuspendCatching(TAG) {
            val organizations = postgrest.from(InviteEntity.COLLECTION).select {
                filter {
                    eq("organization_id", organizationId.id)
                    gt("expiration", clock.now()) // Only non-expired invites
                }
            }
            organizations.decodeList<InviteEntity>().map { it.toInvite() }
        }
    }

    override suspend fun getInvitesByEmail(email: String): Result<List<Invite>> {
        return runSuspendCatching(TAG) {
            val invites = postgrest.from(InviteEntity.COLLECTION).select {
                filter {
                    eq("email", email)
                    gt("expiration", clock.now()) // Only non-expired invites
                }
            }
            invites.decodeList<InviteEntity>().map { it.toInvite() }
        }
    }

    override suspend fun removeInvite(
        inviteId: InviteId,
    ): Result<Unit> = runSuspendCatching(TAG) {
        logD(TAG, "Removing invite: %s", inviteId)

        val deleted = postgrest.from(InviteEntity.COLLECTION).delete {
            select()
            filter {
                eq("id", inviteId.id)
            }
        }.decodeSingleOrNull<InviteEntity>() != null

        if (!deleted) {
            throw ClientRequestExceptions.NotFoundException(
                message = "Error: Invite with ID $inviteId not found.",
            )
        }
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

@OptIn(SupabaseModel::class)
private fun InviteEntity.toInvite(): Invite {
    return Invite(
        inviteId = InviteId(this.id),
        email = this.email,
        organizationId = OrganizationId(this.organizationId),
    )
}
