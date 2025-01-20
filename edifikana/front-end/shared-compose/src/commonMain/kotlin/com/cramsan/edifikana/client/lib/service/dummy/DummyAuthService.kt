package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.lib.model.UserId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Dummy implementation of [AuthService] for testing purposes.
 */
class DummyAuthService : AuthService {

    private val user = MutableStateFlow(USER_1.id)

    override suspend fun isSignedIn(): Result<Boolean> {
        return Result.success(false)
    }

    override suspend fun getUser(): Result<UserModel> {
        return Result.success(USER_1)
    }

    override suspend fun signIn(email: String, password: String): Result<UserModel> {
        return Result.success(USER_1)
    }

    override suspend fun signOut(): Result<Unit> {
        return Result.success(Unit)
    }

    override fun activeUser(): StateFlow<UserId?> {
        return user.asStateFlow()
    }

    override suspend fun signUp(username: String, password: String, fullname: String): Result<UserModel> {
        return Result.success(USER_1)
    }

    companion object {
        private val USER_1 = UserModel(
            UserId("user_id_1"),
            "user_1@test.com",
            true,
        )
    }
}
