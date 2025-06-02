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
import com.cramsan.framework.logging.logI
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import com.cramsan.framework.logging.logI

/**
 * Service for user operations.
 */
class UserService(
    private val userDatabase: UserDatabase,
    private val auth: Auth,
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
        // Send an OTP code if the user is created successfully and authorizeOtp is true
        if (authorizeOtp && result.isSuccess) {
            logI(TAG, "Sending OTP to user $email")
            val otpResult = userDatabase.sendOtpCode(email)
            if (!otpResult.isSuccess) {
                logD(TAG, "Failed to send OTP to user $email: ${otpResult.exceptionOrNull()}")
            }
        }

        return result
    }

    /**
     * Retrieves a user with the provided [id].
     */
    suspend fun getUser(
        id: UserId,
        authorizeMagicLink: Boolean = false,
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
     * TODO: Remove as we are using passwordless authentication
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
     * Sends an OTP to the provided [email]
     */
    private suspend fun requestOtp(email: String) {
        auth.signInWith(OTP) {
            this.email = email
        }
    }

    companion object {
        private const val TAG = "UserService"
    }
}
