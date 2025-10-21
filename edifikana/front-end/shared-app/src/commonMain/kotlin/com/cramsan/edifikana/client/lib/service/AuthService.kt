package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.Invite
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
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
     * Get all users associated with the given organization.
     */
    suspend fun getUsersByOrganization(organizationId: OrganizationId): Result<List<UserModel>>

    /**
     * Sign in the user with the given email and password.
     */
    suspend fun signInWithPassword(email: String, password: String): Result<UserModel>

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
     * Sign up the user with the provided [email], [phoneNumber], and [firstName] & [lastName].
     * Returns the user model if successful.
     */
    suspend fun signUp(
        email: String,
        phoneNumber: String,
        firstName: String,
        lastName: String
    ): Result<UserModel>

    /**
     * Send an OTP email to the user with the provided [email] address.
     */
    suspend fun sendOtpEmail(email: String): Result<Unit>

    /**
     * Verify the user session from an OTP code login with the provided [hashToken] and log them in.
     */
    suspend fun signInWithOtp(email: String, hashToken: String, createUser: Boolean): Result<UserModel>

    /**
     * Request a password reset for the user with the given [email] or [phoneNumber].
     */
    suspend fun passwordReset(email: String?, phoneNumber: String?): Result<Unit>

    /**
     * Verify the permissions of the user.
     */
    suspend fun verifyPermissions(): Result<Boolean>

    /**
     * Check if the user email is registered in our system.
     */
    suspend fun checkUserExists(email: String): Result<Boolean>

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

    /**
     * Update the current user's password and set it to [newPassword]. If a password is already set, then
     * [currentPassword] will need to be provided.
     */
    @OptIn(SecureStringAccess::class)
    suspend fun changePassword(currentPassword: SecureString, newPassword: SecureString): Result<Unit>

    /**
     * Invite an employee member to the organization with the provided [organizationId] using the given [email].
     */
    suspend fun inviteEmployee(email: String, organizationId: OrganizationId): Result<Unit>

    /**
     * Retrieves all pending invites for the given [organizationId].
     */
    suspend fun getInvites(organizationId: OrganizationId): Result<List<Invite>>
}
