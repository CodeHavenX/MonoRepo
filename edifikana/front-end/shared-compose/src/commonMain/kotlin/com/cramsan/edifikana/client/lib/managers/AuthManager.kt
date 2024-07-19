package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.auth.SignInResult
import com.cramsan.edifikana.client.lib.utils.getOrCatch
import com.cramsan.edifikana.lib.UserPk
import com.cramsan.framework.logging.logI

class AuthManager(
    private val authService: AuthService,
    private val workContext: WorkContext,
) {
    suspend fun isSignedIn(enforceAllowList: Boolean): Result<Boolean> = workContext.getOrCatch(TAG) {
        logI(TAG, "isSignedIn")
        authService.isSignedIn(enforceAllowList).getOrThrow()
    }

    suspend fun getUser(userPk: UserPk): Result<UserModel> = workContext.getOrCatch(TAG) {
        logI(TAG, "getUser")
        authService.getUser(userPk).getOrThrow()
    }

    suspend fun signInAnonymously(): Result<Unit> = workContext.getOrCatch(TAG) {
        logI(TAG, "signInAnonymously")
        authService.signInAnonymously().getOrThrow()
    }

    suspend fun handleSignInResult(signInResult: SignInResult): Result<Boolean> = workContext.getOrCatch(TAG) {
        logI(TAG, "handleSignInResult")
        authService.handleSignInResult(signInResult).getOrThrow()
    }

    companion object {
        private const val TAG = "AuthManager"
    }
}
