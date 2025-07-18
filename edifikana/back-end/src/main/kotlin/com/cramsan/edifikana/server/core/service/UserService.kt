package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.datastore.UserDatastore
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.requests.AssociateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePasswordRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateUserRequest
import com.cramsan.framework.logging.logD

/**
 * Service for user operations.
 */
class UserService(
    private val userDatastore: UserDatastore,
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
        val result = userDatastore.createUser(
            request = CreateUserRequest(
                email = email,
                phoneNumber = phoneNumber,
                password = password,
                firstName = firstName,
                lastName = lastName,
            ),
        )

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
        return userDatastore.associateUser(
            request = AssociateUserRequest(
                userId = id,
                email = email,
            ),
        )
    }

    /**
     * Retrieves a user with the provided [id].
     */
    suspend fun getUser(
        id: UserId,
    ): User? {
        logD(TAG, "getUser")
        val user = userDatastore.getUser(
            request = GetUserRequest(
                id = id,
            ),
        ).getOrNull()

        return user
    }

    /**
     * Retrieves all users.
     */
    suspend fun getUsers(): List<User> {
        logD(TAG, "getUsers")
        val users = userDatastore.getUsers().getOrThrow()
        return users
    }

    /**
     * Updates a user with the provided [id] and [email].
     */
    suspend fun updateUser(
        id: UserId,
        email: String?,
    ): User {
        logD(TAG, "updateUser")
        return userDatastore.updateUser(
            request = UpdateUserRequest(
                id = id,
                email = email,
            ),
        ).getOrThrow()
    }

    /**
     * Deletes a user with the provided [id].
     */
    suspend fun deleteUser(
        id: UserId,
    ): Boolean {
        logD(TAG, "deleteUser")
        return userDatastore.deleteUser(
            request = DeleteUserRequest(
                id = id,
            )
        ).getOrThrow()
    }

    /**
     * Updates the password for a user with the provided [userId].
     */
    suspend fun updatePassword(userId: UserId, password: String): Boolean {
        logD(TAG, "updatePassword")
        return userDatastore.updatePassword(
            request = UpdatePasswordRequest(
                id = userId,
                password = password,
            ),
        ).getOrThrow()
    }

    companion object {
        private const val TAG = "UserService"
    }
}
