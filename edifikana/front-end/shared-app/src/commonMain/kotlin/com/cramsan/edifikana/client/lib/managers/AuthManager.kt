package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.Invite
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.flow.StateFlow

/**
 * Manager for authentication.
 */
class AuthManager(
    private val dependencies: ManagerDependencies,
    private val authService: AuthService,
) {
    /**
     * Signs in the user with the given email and password.
     */
    suspend fun isSignedIn(): Result<Boolean> = dependencies.getOrCatch(TAG) {
        logI(TAG, "isSignedIn")
        authService.isSignedIn().getOrThrow()
    }

    /**
     * Signs in the user with the given email and password.
     */
    suspend fun signInWithPassword(email: String, password: String): Result<UserModel> = dependencies.getOrCatch(TAG) {
        logI(TAG, "signIn")
        val userModel = authService.signInWithPassword(email, password).getOrThrow()

        userModel
    }

    /**
     * Sends an OTP code email to the user with the provided [email] address.
     */
    suspend fun sendOtpCode(
        email: String
    ): Result<Unit> = dependencies.getOrCatch(TAG) {
        logI(TAG, "sending OTP code email")
        authService.sendOtpEmail(email).getOrThrow()
    }

    /**
     * Sign in the user with a magic link that contains the user's [email] and a [hashToken] to verify.
     */
    suspend fun signInWithOtp(
        email: String,
        hashToken: String,
        createUser: Boolean,
    ): Result<UserModel> = dependencies.getOrCatch(TAG) {
        logI(TAG, "signing in with OTP code")
        val userModel = authService.signInWithOtp(email, hashToken, createUser).getOrThrow()

        userModel
    }

    /**
     * Signs up the user with the given email and password.
     */
    suspend fun signUp(
        email: String,
        phoneNumber: String,
        firstName: String,
        lastName: String,
    ): Result<UserModel> = dependencies.getOrCatch(
        TAG
    ) {
        logI(TAG, "signUp")
        authService.signUp(email, phoneNumber, firstName, lastName).getOrThrow()
    }

    /**
     * Signs out the user.
     */
    suspend fun signOut(): Result<Unit> = dependencies.getOrCatch(TAG) {
        logI(TAG, "signOut")
        authService.signOut()
    }

    /**
     * Gets the current user.
     */
    suspend fun getUser(): Result<UserModel> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getUser")
        authService.getUser().getOrThrow()
    }

    /**
     * Gets the users for the given organization.
     */
    suspend fun getUsers(organizationId: OrganizationId): Result<List<UserModel>> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getUsers for organizationId: $organizationId")
        authService.getUsersByOrganization(organizationId).getOrThrow()
    }

    /**
     * Gets the active user as an observable flow.
     */
    fun activeUser(): StateFlow<UserId?> = authService.activeUser()

    /**
     * Verifies the permissions of the library. This is used to ensure that we
     * have the right permissions and also to prevent using admin credentials.
     */
    suspend fun verifyPermissions() = dependencies.getOrCatch(TAG) {
        logI(TAG, "verifyPermissions")
        authService.verifyPermissions().getOrThrow()
    }

    /**
     * Check if a user exists with the provided [email].
     */
    suspend fun checkUserExists(email: String): Result<Boolean> = dependencies.getOrCatch(TAG) {
        logI(TAG, "checkUserExists")
        authService.checkUserExists(email).getOrThrow()
    }

    /**
     * Update the user information with the provided [firstName], [lastName], [email], and [phoneNumber].
     */
    suspend fun updateUser(
        firstName: String?,
        lastName: String?,
        email: String?,
        phoneNumber: String?,
    ) = dependencies.getOrCatch(TAG) {
        logI(TAG, "updateUser")
        authService.updateUser(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber
        ).getOrThrow()
    }

    /**
     * Update the current user's password and set it to [newPassword]. If a password is already set, then
     * [currentPassword] will need to be provided.
     */
    @OptIn(SecureStringAccess::class)
    suspend fun changePassword(
        currentPassword: SecureString,
        newPassword: SecureString,
    ): Result<Unit> = dependencies.getOrCatch(TAG) {
        logI(TAG, "changePassword")
        val email = authService.getUser().getOrThrow().email
        authService.changePassword(
            email,
            currentPassword,
            newPassword,
        ).getOrThrow()
    }

    /**
     * Invite a employee.
     */
    suspend fun inviteEmployee(email: String, orgId: OrganizationId) = dependencies.getOrCatch(TAG) {
        logI(TAG, "inviteEmployee")
        authService.inviteEmployee(
            email = email,
            organizationId = orgId,
        ).getOrThrow()
    }

    /**
     * Get the invites for the given organization.
     */
    suspend fun getInvites(organizationId: OrganizationId): Result<List<Invite>> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getInvitedEmployees for organizationId: $organizationId")
        authService.getInvites(organizationId).getOrThrow()
    }

    companion object {
        private const val TAG = "AuthManager"
    }
}
