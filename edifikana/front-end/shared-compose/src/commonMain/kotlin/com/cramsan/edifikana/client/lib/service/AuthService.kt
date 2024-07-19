package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.auth.SignInResult
import com.cramsan.edifikana.lib.UserPk

interface AuthService {

    suspend fun isSignedIn(enforceAllowList: Boolean): Result<Boolean>

    suspend fun getUser(userPk: UserPk): Result<UserModel>

    suspend fun signInAnonymously(): Result<Unit>

    suspend fun handleSignInResult(signInResult: SignInResult): Result<Boolean>
}
