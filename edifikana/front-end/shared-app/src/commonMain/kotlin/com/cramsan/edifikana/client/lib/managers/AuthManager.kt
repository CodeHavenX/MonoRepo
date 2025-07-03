package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.flow.StateFlow

/**
 * Manager for authentication.
 */
class AuthManager(
    private val dependencies: ManagerDependencies,
    private val propertyService: PropertyService,
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
    suspend fun signIn(email: String, password: String): Result<UserModel> = dependencies.getOrCatch(TAG) {
        logI(TAG, "signIn")
        val userModel = authService.signInWithPassword(email, password).getOrThrow()

        val properties = propertyService.getPropertyList().getOrThrow()
        if (properties.isNotEmpty()) {
            propertyService.setActiveProperty(properties.firstOrNull()?.id)
        }

        userModel
    }

    /**
     * Sends an OTP code email to the user with the provided [email] address.
     */
    suspend fun sendOtpCode(
        email: String
    ) {
        logI(TAG, "sending OTP code email")
        authService.sendOtpEmail(email).getOrThrow()
    }

    /**
     * Sign in the user with a magic link that contains the user's [email] and a [hashToken] to verify.
     */
    suspend fun signInWithOtp(
        email: String,
        hashToken: String,
    ): Result<UserModel> = dependencies.getOrCatch(TAG) {
        logI(TAG, "signing in with OTP code")
        authService.signInWithOtp(email, hashToken).getOrThrow()
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

    companion object {
        private const val TAG = "AuthManager"
    }
}
