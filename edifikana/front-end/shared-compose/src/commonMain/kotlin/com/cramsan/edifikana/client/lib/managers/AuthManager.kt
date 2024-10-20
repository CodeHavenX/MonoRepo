package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.auth.SignInResult
import com.cramsan.edifikana.client.lib.utils.getOrCatch
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.logging.logI

/**
 * Manager for authentication.
 */
class AuthManager(
    private val authService: AuthService,
    private val workContext: WorkContext,
) {
    /**
     * Signs in the user with the given email and password.
     */
    suspend fun isSignedIn(enforceAllowList: Boolean): Result<Boolean> = workContext.getOrCatch(TAG) {
        logI(TAG, "isSignedIn")
        authService.isSignedIn(enforceAllowList).getOrThrow()
    }

    /**
     * Get the user with the provided [UserPk].
     */
    suspend fun getUser(userPk: UserId): Result<UserModel> = workContext.getOrCatch(TAG) {
        logI(TAG, "getUser")
        authService.getUser(userPk).getOrThrow()
    }

    /**
     * Handle the result of signing in.
     */
    suspend fun handleSignInResult(signInResult: SignInResult): Result<Boolean> = workContext.getOrCatch(TAG) {
        logI(TAG, "handleSignInResult")
        authService.handleSignInResult(signInResult).getOrThrow()
    }

    companion object {
        private const val TAG = "AuthManager"
    }
}
