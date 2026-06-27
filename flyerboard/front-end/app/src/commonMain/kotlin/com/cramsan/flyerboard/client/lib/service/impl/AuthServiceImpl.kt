package com.cramsan.flyerboard.client.lib.service.impl

import com.cramsan.architecture.client.service.execute
import com.cramsan.flyerboard.api.UserApi
import com.cramsan.flyerboard.client.lib.models.UserModel
import com.cramsan.flyerboard.client.lib.service.AuthService
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.network.CreateUserNetworkRequest
import com.cramsan.framework.annotations.FrontendService
import com.cramsan.framework.assertlib.assert
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import com.cramsan.framework.networkapi.buildRequest
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Supabase implementation of [AuthService].
 */
@OptIn(ExperimentalAtomicApi::class)
@FrontendService
class AuthServiceImpl(private val auth: Auth, private val http: HttpClient) : AuthService {
    private val _activeUser = MutableStateFlow<UserModel?>(null)
    private val isInitialized = AtomicBoolean(false)

    private suspend fun createUser(
        firstName: String,
        lastName: String,
    ): Result<UserModel> =
        runSuspendCatching(TAG) {
            val response =
                UserApi.createUser
                    .buildRequest(
                        CreateUserNetworkRequest(
                            firstName = firstName,
                            lastName = lastName,
                        ),
                    ).execute(http)
            val userModel = response.toUserModel()
            userModel
        }

    private suspend fun getCurrentUser(): Result<UserModel> =
        runSuspendCatching(TAG) {
            UserApi.getCurrentUser
                .buildRequest()
                .execute(http)
                .toUserModel()
        }

    override suspend fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
    ): Result<Unit> =
        runSuspendCatching(TAG) {
            logD(TAG, "signUp: %s", email)
            try {
                // Sign up with Supabase
                auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                val createdUser = createUser(firstName, lastName).getOrThrow()
                _activeUser.value = createdUser
            } catch (e: AuthRestException) {
                logE(TAG, "Error signing up", e)
                throw ClientRequestExceptions.UnauthorizedException("Sign-up failed: ${e.message}", e)
            }
        }

    override suspend fun signIn(email: String, password: String): Result<Unit> =
        runSuspendCatching(TAG) {
            logD(TAG, "signIn: %s", email)
            try {
                auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                // Here we want to detect null in case the user was not created previously even though the user exists
                // in supabase
                val user = getCurrentUser().getOrNull()
                if (user == null) {
                    val createdUser = createUser("", "").getOrThrow()
                    _activeUser.value = createdUser
                } else {
                    _activeUser.value = user
                }
            } catch (e: AuthRestException) {
                logE(TAG, "Error signing in", e)
                throw ClientRequestExceptions.UnauthorizedException("Invalid credentials", e)
            }
        }

    override suspend fun signOut(): Result<Unit> =
        runSuspendCatching(TAG) {
            logD(TAG, "signOut")
            auth.signOut()
            _activeUser.value = null
        }

    override suspend fun isAuthenticated(): Result<Boolean> =
        runSuspendCatching(TAG) {
            auth.awaitInitialization()
            val user = auth.currentUserOrNull()
            if (user == null) {
                _activeUser.value = null
                logD(TAG, "User not authenticated")
                isInitialized.store(true)
                false
            } else {
                try {
                    auth.refreshCurrentSession()
                    _activeUser.value = getCurrentUser().getOrThrow()
                    true
                } catch (e: RestException) {
                    logE(TAG, "Failed to refresh session", e)
                    _activeUser.value = null
                    false
                } finally {
                    isInitialized.store(true)
                }
            }
        }

    override fun getAccessToken(): String? = auth.currentAccessTokenOrNull()

    override fun currentUserId(): UserId? = auth.currentUserOrNull()?.id?.let { UserId(it) }

    override fun activeUser(): StateFlow<UserModel?> {
        assert(isInitialized.load(), TAG, "Reading active user before initialization")
        return _activeUser.asStateFlow()
    }

    companion object {
        private const val TAG = "AuthServiceImpl"
    }
}
