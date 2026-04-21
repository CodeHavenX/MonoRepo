package com.cramsan.flyerboard.client.lib.service.impl

import com.cramsan.flyerboard.client.lib.service.AuthService
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Supabase implementation of [AuthService].
 */
class AuthServiceImpl(
    private val auth: Auth,
) : AuthService {

    private val _activeUser = MutableStateFlow<UserId?>(null)

    override suspend fun signUp(email: String, password: String): Result<Unit> =
        runSuspendCatching(TAG) {
            logD(TAG, "signUp: %s", email)
            try {
                auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                _activeUser.value = auth.currentUserOrNull()?.id?.let { UserId(it) }
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
                _activeUser.value = auth.currentUserOrNull()?.id?.let { UserId(it) }
            } catch (e: AuthRestException) {
                logE(TAG, "Error signing in", e)
                throw ClientRequestExceptions.UnauthorizedException("Invalid credentials", e)
            }
        }

    override suspend fun signOut(): Result<Unit> = runSuspendCatching(TAG) {
        logD(TAG, "signOut")
        auth.signOut()
        _activeUser.value = null
    }

    override suspend fun isAuthenticated(): Result<Boolean> = runSuspendCatching(TAG) {
        auth.awaitInitialization()
        val user = auth.currentUserOrNull()
        if (user == null) {
            _activeUser.value = null
            logD(TAG, "User not authenticated")
            false
        } else {
            try {
                auth.refreshCurrentSession()
                _activeUser.value = UserId(user.id)
                true
            } catch (e: RestException) {
                logE(TAG, "Failed to refresh session", e)
                _activeUser.value = null
                false
            }
        }
    }

    override fun getAccessToken(): String? = auth.currentAccessTokenOrNull()

    override fun currentUserId(): UserId? = auth.currentUserOrNull()?.id?.let { UserId(it) }

    override fun activeUser(): StateFlow<UserId?> = _activeUser.asStateFlow()

    companion object {
        private const val TAG = "AuthServiceImpl"
    }
}
