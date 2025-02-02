package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.repository.UserDatabase
import com.cramsan.edifikana.server.core.service.models.User
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
    private val userDatabase: UserDatabase,
) {

    /**
     * Creates a user with the provided information.
     */
    suspend fun createUser(
        usernameEmail: String,
        usernamePhone: String,
        password: String,
        firstName: String,
        lastName: String,
    ): User {
        logD(TAG, "createUser")
        return userDatabase.createUser(
            request = CreateUserRequest(
                usernameEmail = usernameEmail,
                usernamePhone = usernamePhone,
                password = password,
                firstName = firstName,
                lastName = lastName,
            ),
        ).getOrThrow()
    }

    /**
     * Retrieves a user with the provided [id].
     */
    suspend fun getUser(
        id: UserId,
        checkGlobalPerms: Boolean,
    ): User? {
        logD(TAG, "getUser")
        val user = userDatabase.getUser(
            request = GetUserRequest(
                id = id,
                checkGlobalPerms = checkGlobalPerms,
            ),
        ).getOrNull()

        return user
    }

    /**
     * Retrieves all users.
     */
    suspend fun getUsers(): List<User> {
        logD(TAG, "getUsers")
        val users = userDatabase.getUsers().getOrThrow()
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
        return userDatabase.updateUser(
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
        return userDatabase.deleteUser(
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
        return userDatabase.updatePassword(
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
