package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.auth.SignInResult
import com.cramsan.edifikana.lib.model.UserId

/**
 * Service for managing authentication.
 */
interface AuthService {

    /**
     * Check if the user is signed in.
     */
    suspend fun isSignedIn(enforceAllowList: Boolean): Result<Boolean>

    /**
     * Get the current user.
     */
    suspend fun getUser(userPk: UserId): Result<UserModel>

    /**
     * Sign in with email and password.
     */
    suspend fun handleSignInResult(signInResult: SignInResult): Result<Boolean>
}
