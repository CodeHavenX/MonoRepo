package com.cramsan.flyerboard.client.lib.managers

import com.cramsan.flyerboard.client.lib.service.AuthService
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.flow.StateFlow

/**
 * Manager to handle authentication operations.
 */
class AuthManager(
    private val dependencies: ManagerDependencies,
    private val authService: AuthService,
) {

    /**
     * Registers a new user with [email] and [password].
     */
    suspend fun signUp(email: String, password: String): Result<Unit> = dependencies.getOrCatch(TAG) {
        logI(TAG, "signUp")
        authService.signUp(email, password).getOrThrow()
    }

    /**
     * Signs in an existing user with [email] and [password].
     */
    suspend fun signIn(email: String, password: String): Result<Unit> = dependencies.getOrCatch(TAG) {
        logI(TAG, "signIn")
        authService.signIn(email, password).getOrThrow()
    }

    /**
     * Signs out the currently authenticated user.
     */
    suspend fun signOut(): Result<Unit> = dependencies.getOrCatch(TAG) {
        logI(TAG, "signOut")
        authService.signOut().getOrThrow()
    }

    /**
     * Returns true if a user session is currently active.
     */
    suspend fun isAuthenticated(): Result<Boolean> = dependencies.getOrCatch(TAG) {
        logI(TAG, "isAuthenticated")
        authService.isAuthenticated().getOrThrow()
    }

    /**
     * Returns the current session's access token for attaching to backend API calls.
     */
    fun getAccessToken(): String? = authService.getAccessToken()

    /**
     * Returns the current authenticated user's ID, or null if not authenticated.
     */
    fun currentUserId(): UserId? = authService.currentUserId()

    /**
     * Observable flow of the current user ID. Emits null when signed out.
     */
    fun activeUser(): StateFlow<UserId?> = authService.activeUser()

    companion object {
        private const val TAG = "AuthManager"
    }
}
