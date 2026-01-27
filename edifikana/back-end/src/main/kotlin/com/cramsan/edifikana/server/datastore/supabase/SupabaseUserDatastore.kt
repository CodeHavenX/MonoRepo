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
import com.cramsan.edifikana.server.service.models.UserRole
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
     * Creates a new user with the given credentials. Transient users skip Supabase Auth.
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

    /**
     * Associates a Supabase Auth user with an existing transient user record.
     */
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

        // Update the temporary user's ID and auth metadata instead of creating a new user
        val updatedUser = postgrest.from(UserEntity.COLLECTION).update(
            {
                UserEntity::id setTo userId.userId
                UserEntity::authMetadata setTo temporaryUser.authMetadata.copy(
                    pendingAssociation = false,
                    canPasswordAuth = true,
                )
            }
        ) {
            select()
            filter {
                UserEntity::id eq temporaryUser.id
                UserEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<UserEntity>()

        if (updatedUser == null) {
            logW(TAG, "Failed to update temporary user with email: $email - user may have been deleted or modified")
            throw ClientRequestExceptions.NotFoundException(
                message = "Error: Temporary user with email $email was not found or has been modified.",
            )
        }

        // Return the updated user as a domain model
        updatedUser.toUser()
    }

    /**
     * Retrieves a user by [id]. Returns the [User] if found, null otherwise.
     */
    override suspend fun getUser(
        id: UserId,
    ): Result<User?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting user: %s", id)

        val userEntity = getUserImpl(id)

        userEntity?.toUser()
    }

    /**
     * Retrieves a user by [email]. Returns the [User] if found, null otherwise.
     */
    override suspend fun getUser(email: String): Result<User?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting user by email: %s", email)
        val userEntity = getUserByEmail(email)
        userEntity?.toUser()
    }

    private suspend fun getUserImpl(id: UserId): UserEntity? {
        return postgrest.from(UserEntity.COLLECTION).select {
            filter {
                UserEntity::id eq id.userId
                UserEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<UserEntity>()
    }

    private suspend fun getUserByEmail(email: String): UserEntity? {
        return postgrest.from(UserEntity.COLLECTION).select {
            filter {
                UserEntity::email eq email
                UserEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<UserEntity>()
    }

    /**
     * Gets all users belonging to the given [organizationId].
     */
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
                UserOrganizationMappingEntity::organizationId eq organizationId.id
            }
        }
        // Filter out soft-deleted users
        organizations.decodeList<UserEntity>()
            .filter { it.deletedAt == null }
            .map { it.toUser() }
    }

    /**
     * Updates a user's attributes. Only non-null parameters are updated.
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
     * Soft deletes a user by [id]. Also attempts to delete from Supabase Auth.
     */
    override suspend fun deleteUser(
        id: UserId,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Soft deleting user: %s", id)

        val user = getUserImpl(id) ?: throw ClientRequestExceptions.NotFoundException(
            message = "Error: User with ID $id not found in our database.",
        )

        // Soft delete in our database first
        val softDeleted = postgrest.from(UserEntity.COLLECTION).update({
            UserEntity::deletedAt setTo clock.now()
        }) {
            select()
            filter {
                UserEntity::id eq id.userId
                UserEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<UserEntity>()

        // Auth deletion can fail without affecting our soft delete
        // A background process can retry failed auth deletions later
        if (softDeleted != null && !user.authMetadata.pendingAssociation) {
            runCatching { adminApi.deleteUser(id.userId) }
        }

        softDeleted != null
    }

    /**
     * Updates the user's password. Requires current password if one is already set.
     */
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

    /**
     * Creates an invite for an [email] to join an organization with the specified [role].
     */
    override suspend fun recordInvite(
        email: String,
        organizationId: OrganizationId,
        expiration: Instant,
        role: UserRole,
    ): Result<Invite> = runSuspendCatching(TAG) {
        logD(TAG, "Recording invite for email: %s with role: %s", email, role)

        val inviteEntity = InviteEntity.Create(
            email = email,
            organizationId = organizationId.id,
            createdAt = clock.now(),
            expiration = expiration,
            role = role.name,
        )

        val data = postgrest.from(InviteEntity.COLLECTION).insert(inviteEntity) {
            select()
        }
        data.decodeSingle<InviteEntity>().toInvite()
    }

    /**
     * Retrieves an invite by [inviteId]. Returns the [Invite] if found, null otherwise.
     */
    override suspend fun getInvite(inviteId: InviteId): Result<Invite?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting invite: %s", inviteId)

        postgrest.from(InviteEntity.COLLECTION).select {
            filter {
                InviteEntity::id eq inviteId.id
                InviteEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<InviteEntity>()?.toInvite()
    }

    /**
     * Gets all non-expired invites for an organization.
     */
    override suspend fun getInvites(organizationId: OrganizationId): Result<List<Invite>> {
        return runSuspendCatching(TAG) {
            val organizations = postgrest.from(InviteEntity.COLLECTION).select {
                filter {
                    InviteEntity::organizationId eq organizationId.id
                    gt("expiration", clock.now()) // Only non-expired invites
                    InviteEntity::deletedAt isExact null
                }
            }
            organizations.decodeList<InviteEntity>().map { it.toInvite() }
        }
    }

    /**
     * Gets all non-expired invites for an [email] address.
     */
    override suspend fun getInvitesByEmail(email: String): Result<List<Invite>> {
        return runSuspendCatching(TAG) {
            val invites = postgrest.from(InviteEntity.COLLECTION).select {
                filter {
                    InviteEntity::email eq email
                    gt("expiration", clock.now()) // Only non-expired invites
                    InviteEntity::deletedAt isExact null
                }
            }
            invites.decodeList<InviteEntity>().map { it.toInvite() }
        }
    }

    /**
     * Soft deletes an invite by [inviteId].
     */
    override suspend fun removeInvite(
        inviteId: InviteId,
    ): Result<Unit> = runSuspendCatching(TAG) {
        logD(TAG, "Soft deleting invite: %s", inviteId)

        val deleted = postgrest.from(InviteEntity.COLLECTION).update({
            InviteEntity::deletedAt setTo clock.now()
        }) {
            select()
            filter {
                InviteEntity::id eq inviteId.id
                InviteEntity::deletedAt isExact null
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
