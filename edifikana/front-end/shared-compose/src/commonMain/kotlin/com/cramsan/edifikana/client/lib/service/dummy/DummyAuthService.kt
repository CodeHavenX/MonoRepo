@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.lib.model.UserId
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

/**
 * Dummy implementation of [AuthService] for testing purposes.
 */
class DummyAuthService : AuthService {

    override suspend fun isSignedIn(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(): Result<UserModel> {
        TODO("Not yet implemented")
    }

    override suspend fun signIn(email: String, password: String): Result<UserModel> {
        delay(100)
        return Result.success(UserModel(UserId("user"), "user@test.com"))
    }

    override suspend fun signOut(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun activeUser(): StateFlow<UserId?> {
        TODO("Not yet implemented")
    }

    override suspend fun signUp(username: String, password: String, fullname: String): Result<UserModel> {
        TODO("Not yet implemented")
    }
}
