package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import kotlin.time.Instant

/**
 * Interface for interacting with the user database.
 */
interface UserDatastore {
    /**
     * Creates a new user for the given [request]. Returns the [Result] of the operation with the created [User].
     */
    suspend fun createUser(
        email: String,
        phoneNumber: String,
        password: String?,
        firstName: String,
        lastName: String,
        isTransient: Boolean,
    ): Result<User>

    /**
     * Associates a user from another service with a new user in our system. Returns the [Result] of the operation with
     * the created [User].
     */
    suspend fun associateUser(userId: UserId, email: String): Result<User>

    /**
     * Retrieves a user for the given [request]. Returns the [Result] of the operation with the fetched [User] if found.
     */
    suspend fun getUser(id: UserId): Result<User?>

    /**
     * Retrieves all users. Returns the [Result] of the operation with a list of [User].
     */
    suspend fun getUsers(organizationId: OrganizationId): Result<List<User>>

    /**
     * Retrieves a user by email. Returns the [Result] of the operation with the fetched [User].
     */
    suspend fun getUser(email: String): Result<User?>

    /**
     * Updates a user with the given [request]. Returns the [Result] of the operation with the updated [User].
     */
    suspend fun updateUser(id: UserId, email: String?): Result<User>

    /**
     * Deletes a user with the given [request].
     * Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    suspend fun deleteUser(id: UserId): Result<Boolean>

    /**
     * Updates the password for a user with the given [request]. Returns the [Result] of the operation.
     */
    @OptIn(SecureStringAccess::class)
    suspend fun updatePassword(
        id: UserId,
        currentHashedPassword: SecureString?,
        newPassword: SecureString,
    ): Result<Unit>

    /**
     * Records an invite for a user with the given [email], [organizationId], and [role].
     * Returns the [Result] of the operation with the created [Invite].
     */
    suspend fun recordInvite(
        email: String,
        organizationId: OrganizationId,
        expiration: Instant,
        role: UserRole,
    ): Result<Invite>

    /**
     * Retrieves an invite with the given [inviteId].
     * Returns the [Result] of the operation with the [Invite] if found.
     */
    suspend fun getInvite(inviteId: InviteId): Result<Invite?>

    /**
     * Removes an invite with the given [inviteId]. Returns the [Result] of the operation.
     */
    suspend fun removeInvite(inviteId: InviteId): Result<Unit>

    /**
     * Retrieves all pending invites for the given [organizationId].
     * Returns the [Result] of the operation with a list of [Invite].
     */
    suspend fun getInvites(organizationId: OrganizationId): Result<List<Invite>>

    /**
     * Retrieves all pending invites for the given [email].
     * Returns the [Result] of the operation with a list of [Invite].
     */
    suspend fun getInvitesByEmail(email: String): Result<List<Invite>>

    /**
     * Permanently deletes a soft-deleted user record by ID.
     * Only purges if the record is already soft-deleted.
     * This is intended for testing and maintenance purposes only.
     * Returns the [Result] of the operation with a [Boolean] indicating if the record was purged.
     */
    suspend fun purgeUser(id: UserId): Result<Boolean>

    /**
     * Permanently deletes a soft-deleted invite record by ID.
     * Only purges if the record is already soft-deleted.
     * This is intended for testing and maintenance purposes only.
     * Returns the [Result] of the operation with a [Boolean] indicating if the record was purged.
     */
    suspend fun purgeInvite(id: InviteId): Result<Boolean>
}
