package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.service.auth.SignInResult
import com.cramsan.edifikana.lib.firestore.User
import com.cramsan.edifikana.lib.firestore.UserPk

interface AuthService {

    suspend fun isSignedIn(enforceAllowList: Boolean): Result<Boolean>

    suspend fun getUser(userPk: UserPk): Result<User>

    suspend fun signInAnonymously(): Result<Unit>

    suspend fun handleSignInResult(signInResult: SignInResult): Result<Boolean>
}
