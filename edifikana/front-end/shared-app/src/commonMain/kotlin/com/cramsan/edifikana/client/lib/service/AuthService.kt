package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.model.UserId
import kotlinx.coroutines.flow.StateFlow

/**
 * Service for managing authentication.
 */
interface AuthService {

    /**
     * Check if the user is signed in.
     */
    suspend fun isSignedIn(): Result<Boolean>

    /**
     * Get the current user.
     */
    suspend fun getUser(): Result<UserModel>

    /**
     * Sign in the user with the given email and password.
     */
    suspend fun signIn(email: String, password: String): Result<UserModel>

    /**
     * Sign out the user.
     */
    suspend fun signOut(): Result<Unit>

    /**
     * Get the observable reference to the active user. You can use this function to fetch the current
     * active user or to observe changes to the active user.
     */
    fun activeUser(): StateFlow<UserId?>

    /**
     * Sign up the user with the provided usernames, [password], and [firstName] & [lastName].
     * Returns the user model if successful.
     */
    suspend fun signUp(
        email: String,
        phoneNumber: String,
        password: String,
        firstName: String,
        lastName: String
    ): Result<UserModel>

    /**
     * Verify the user session from an OTP code login with the provided [hashToken].
     */
    suspend fun signInWithOtp(email: String, hashToken: String): Result<UserModel>

    /**
     * Request a password reset for the user with the given [email] or [phoneNumber].
     */
    suspend fun passwordReset(usernameEmail: String?, usernamePhone: String?): Result<Unit>

    /**
     * Verify the permissions of the user.
     */
    suspend fun verifyPermissions(): Result<Boolean>

    /**
     * Update the user information with the provided [firstName], [lastName], [email], and [phoneNumber].
     * Returns the updated user model if successful.
     */
    suspend fun updateUser(
        firstName: String?,
        lastName: String?,
        email: String?,
        phoneNumber: String?,
    ): Result<UserModel>
}
