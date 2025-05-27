package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.lib.model.UserId
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration.Companion.seconds

/**
 * Dummy implementation of [AuthService] for testing purposes.
 */
class DummyAuthService : AuthService {

    private val user = MutableStateFlow<UserId?>(null)

    override suspend fun isSignedIn(): Result<Boolean> {
        delay(2.seconds)
        return Result.success(user.value != null)
    }

    override suspend fun getUser(): Result<UserModel> {
        delay(1.seconds)
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
        delay(1.seconds)
        user.value = USER_1.id
        return Result.success(USER_1)
    }

    override suspend fun signOut(): Result<Unit> {
        delay(1.seconds)
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
        delay(1.seconds)
        user.value = USER_1.id
        return Result.success(USER_1)
    }

    override suspend fun signInWithMagicLink(email: String, hashToken: String): Result<UserModel> {
        delay(1.seconds)
        user.value = USER_1.id
        getUser().getOrThrow()
        return Result.success(USER_1)
    }

    override suspend fun verifyPermissions(): Result<Boolean> {
        delay(1.seconds)
        return Result.success(true)
    }

    override suspend fun passwordReset(usernameEmail: String?, usernamePhone: String?): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(
        firstName: String?,
        lastName: String?,
        email: String?,
        phoneNumber: String?
    ): Result<UserModel> {
        delay(1.seconds)
        val updatedUser = USER_1.copy(
            firstName = firstName ?: USER_1.firstName,
            lastName = lastName ?: USER_1.lastName,
            email = email ?: USER_1.email,
            phoneNumber = phoneNumber ?: USER_1.phoneNumber
        )
        user.value = updatedUser.id
        return Result.success(updatedUser)
    }

    companion object {
        private val USER_1 = UserModel(
            UserId("user_id_1"),
            "user_1@test.com",
            "1234567890",
            "User",
            "One",
            isVerified = false,
        )
    }
}
