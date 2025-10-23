package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.datastore.UserDatastore
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.logging.logD
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

/**
 * Service for user operations.
 */
class UserService(
    private val userDatastore: UserDatastore,
    private val organizationDatastore: OrganizationDatastore,
    private val clock: Clock,
) {

    /**
     * Creates a user with the provided information.
     */
    suspend fun createUser(
        email: String,
        phoneNumber: String,
        password: String?,
        firstName: String,
        lastName: String,
    ): Result<User> {
        logD(TAG, "createUser")
        val isTransient = password.isNullOrBlank()
        val result = userDatastore.createUser(
            email,
            phoneNumber,
            password,
            firstName,
            lastName,
            isTransient
        )

        if (!isTransient) {
            val orgID = organizationDatastore.createOrganization().getOrThrow().id
            organizationDatastore.addUserToOrganization(
                userId = result.getOrThrow().id,
                organizationId = orgID,
                role = UserRole.OWNER,
            )
        }

        return result
    }

    /**
     * Associate a user from another service with a new user in our system.
     */
    suspend fun associateUser(
        id: UserId,
        email: String,
    ): Result<User> {
        logD(TAG, "associateUser")
        val result = userDatastore.associateUser(
            userId = id,
            email = email
        )

        if (result.isSuccess) {
            val orgId = organizationDatastore.createOrganization().getOrThrow().id
            organizationDatastore.addUserToOrganization(
                userId = result.getOrThrow().id,
                organizationId = orgId,
                role = UserRole.OWNER
            )
        }

        return result
    }

    /**
     * Retrieves a user with the provided [id].
     */
    suspend fun getUser(
        id: UserId,
    ): Result<User?> {
        logD(TAG, "getUser")
        return userDatastore.getUser(
            id = id,
        )
    }

    /**
     * Retrieves all users.
     */
    suspend fun getUsers(
        organizationId: OrganizationId,
    ): Result<List<User>> {
        logD(TAG, "getUsers")
        return userDatastore.getUsers(
            organizationId = organizationId,
        )
    }

    /**
     * Updates a user with the provided [id] and [email].
     */
    suspend fun updateUser(
        id: UserId,
        email: String?,
    ): Result<User> {
        logD(TAG, "updateUser")
        return userDatastore.updateUser(
            id = id,
            email = email,
        )
    }

    /**
     * Deletes a user with the provided [id].
     */
    suspend fun deleteUser(
        id: UserId,
    ): Result<Boolean> {
        logD(TAG, "deleteUser")
        return userDatastore.deleteUser(
            id = id,
        )
    }

    /**
     * Updates the password for a user with the provided [userId].
     */
    @OptIn(SecureStringAccess::class)
    suspend fun updatePassword(
        userId: UserId,
        currentHashedPassword: SecureString,
        newPassword: SecureString,
    ): Result<Unit> {
        logD(TAG, "updatePassword")
        return userDatastore.updatePassword(
            id = userId,
            currentHashedPassword = currentHashedPassword,
            newPassword = newPassword,
        )
    }

    /**
     * Records an invite for a user with the provided [email] and [organizationId].
     */
    suspend fun inviteUser(
        email: String,
        organizationId: OrganizationId,
    ): Result<Unit> = runCatching {
        logD(TAG, "inviteUser")
        userDatastore.recordInvite(
            email,
            organizationId,
            expiration = clock.now() + 14.days,
        ).getOrThrow()
        Unit
    }

    /**
     * Retrieves all pending invites for the provided [organizationId].
     */
    suspend fun getInvites(
        organizationId: OrganizationId,
    ): Result<List<Invite>> {
        logD(TAG, "getInvites")
        return userDatastore.getInvites(organizationId)
    }

    companion object {
        private const val TAG = "UserService"
    }
}
