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

    private val user = MutableStateFlow<UserId?>(null)

    override suspend fun isSignedIn(): Result<Boolean> {
        return Result.success(user.value != null)
    }

    override suspend fun getUser(): Result<UserModel> {
        val user = user.value?.let {
            USER_1
        }
        return if (user == null) {
            TODO()
        } else {
            Result.success(user)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<UserModel> {
        user.value = USER_1.id
        return Result.success(USER_1)
    }

    override suspend fun signOut(): Result<Unit> {
        user.value = null
        return Result.success(Unit)
    }

    override fun activeUser(): StateFlow<UserId?> {
        return user.asStateFlow()
    }

    override suspend fun signUp(
        email: String,
        phoneNumber: String,
        password: String,
        firstName: String,
        lastName: String
    ): Result<UserModel> {
        user.value = USER_1.id
        return Result.success(USER_1)
    }

    override suspend fun verifyPermissions(): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun passwordReset(usernameEmail: String?, usernamePhone: String?): Result<Unit> {
        TODO("Not yet implemented")
    }

    companion object {
        private val USER_1 = UserModel(
            UserId("user_id_1"),
            "user_1@test.com",
            true,
        )
    }
}
