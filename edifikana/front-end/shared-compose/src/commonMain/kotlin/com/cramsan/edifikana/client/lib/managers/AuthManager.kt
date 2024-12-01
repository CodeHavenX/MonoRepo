package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.flow.StateFlow

/**
 * Manager for authentication.
 */
class AuthManager(
    private val authService: AuthService,
    private val dependencies: ManagerDependencies,
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
        authService.signIn(email, password).getOrThrow()
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

    companion object {
        private const val TAG = "AuthManager"
    }
}
