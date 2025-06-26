package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.EnrollmentType
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.repository.UserDatabase
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.EnrollUserRequest
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
    @Suppress("UnusedParameter")
    suspend fun createUser(
        email: String,
        phoneNumber: String,
        password: String?,
        firstName: String,
        lastName: String,
        authorizeOtp: Boolean,
    ): Result<User> {
        logD(TAG, "createUser")
        val result = userDatabase.createUser(
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
     * Retrieves a user with the provided [id].
     */
    suspend fun getUser(
        id: UserId,
    ): User? {
        logD(TAG, "getUser")
        val user = userDatabase.getUser(
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

    /**
     * Enrolls an account for the user with the provided [userId] and [userIdentifier].
     * This is used for creating a new user account for users who may have previously signed in with a
     * third-party provider.
     */
    suspend fun enrollUser(
        userId: UserId,
        enrollmentIdentifier: String,
        enrollmentType: EnrollmentType,
    ): Result<User> {
        logD(TAG, "enrollUser for user: %s", userId)

        return userDatabase.enrollUser(
            request = EnrollUserRequest(
                userId = userId,
                enrollmentIdentifier = enrollmentIdentifier,
                enrollmentType = enrollmentType,
            )
        )
    }

    companion object {
        private const val TAG = "UserService"
    }
}
