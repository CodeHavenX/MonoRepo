@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.auth.SignInResult
import com.cramsan.edifikana.lib.model.UserId
import kotlinx.coroutines.delay

/**
 * Dummy implementation of [AuthService] for testing purposes.
 */
class DummyAuthService : AuthService {
    override suspend fun isSignedIn(enforceAllowList: Boolean): Result<Boolean> {
        delay(100)
        return Result.success(true)
    }

    override suspend fun getUser(userPk: UserId): Result<UserModel> {
        delay(100)
        return Result.success(UserModel("user", "user@test.com"))
    }

    override suspend fun handleSignInResult(signInResult: SignInResult): Result<Boolean> {
        delay(100)
        return Result.success(true)
    }
}
