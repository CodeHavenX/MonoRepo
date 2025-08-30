package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.core.datastore.UserDatastore
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.requests.AssociateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.CreateOrganizationRequest
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePasswordRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateUserRequest
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
            request = CreateUserRequest(
                email = email,
                phoneNumber = phoneNumber,
                password = password,
                firstName = firstName,
                lastName = lastName,
                isTransient = isTransient,
            ),
        )

        if (!isTransient) {
            organizationDatastore.createOrganization(
                request = CreateOrganizationRequest(
                    owner = result.getOrThrow().id,
                ),
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
            request = AssociateUserRequest(
                userId = id,
                email = email,
            ),
        )

        if (result.isSuccess) {
            organizationDatastore.createOrganization(
                request = CreateOrganizationRequest(
                    owner = id,
                ),
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
            request = GetUserRequest(
                id = id,
            ),
        )
    }

    /**
     * Retrieves all users.
     */
    suspend fun getUsers(): Result<List<User>> {
        logD(TAG, "getUsers")
        return userDatastore.getUsers()
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
            request = UpdateUserRequest(
                id = id,
                email = email,
            ),
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
            request = DeleteUserRequest(
                id = id,
            )
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
            request = UpdatePasswordRequest(
                id = userId,
                currentHashedPassword = currentHashedPassword,
                newPassword = newPassword,
            ),
        )
    }

    companion object {
        private const val TAG = "UserService"
    }
}
