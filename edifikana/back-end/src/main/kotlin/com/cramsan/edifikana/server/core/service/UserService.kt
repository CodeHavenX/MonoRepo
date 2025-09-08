package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.core.datastore.UserDatastore
import com.cramsan.edifikana.server.core.service.models.Invite
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.logging.logD

/**
 * Service for user operations.
 */
class UserService(
    private val userDatastore: UserDatastore,
    private val organizationDatastore: OrganizationDatastore,
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
            val orgID = organizationDatastore.createOrganization(
                owner = result.getOrThrow().id,
            ).getOrThrow().id
            organizationDatastore.addUserToOrganization(
                userId = result.getOrThrow().id,
                organizationId = orgID,
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
            val orgId = organizationDatastore.createOrganization(
                owner = id,
            ).getOrThrow().id
            organizationDatastore.addUserToOrganization(
                userId = result.getOrThrow().id,
                organizationId = orgId,
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
    ): Result<Unit> {
        logD(TAG, "inviteUser")
        return userDatastore.recordInvite(email, organizationId)
    }

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
