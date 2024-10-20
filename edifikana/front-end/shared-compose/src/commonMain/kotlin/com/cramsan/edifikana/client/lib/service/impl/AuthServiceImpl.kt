package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.auth.SignInResult
import com.cramsan.edifikana.lib.model.UserId

/**
 * Default implementation for the [AuthService].
 */
class AuthServiceImpl : AuthService {
    override suspend fun isSignedIn(enforceAllowList: Boolean): Result<Boolean> {
        TODO()
    }

    override suspend fun getUser(userPk: UserId): Result<UserModel> {
        TODO("Not yet implemented")
    }

    override suspend fun handleSignInResult(signInResult: SignInResult): Result<Boolean> {
        TODO("Not yet implemented")
    }
}
