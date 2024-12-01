package com.codehavenx.alpaca.frontend.appcore.managers

import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration.Companion.seconds

/**
 * Manager to handle authentication operations.
 */
class AuthenticationManager(
    private val dependencies: ManagerDependencies,
) {
    private val _userSignInState: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val userSignInState: StateFlow<Boolean> = _userSignInState

    /**
     * Check if the user is signed in.
     */
    suspend fun isUserSignedIn(): Result<Boolean> = dependencies.getOrCatch(TAG) {
        _userSignInState.value
    }

    /**
     * Sign in the user.
     */
    suspend fun signIn(username: String, password: String): Result<Unit> = dependencies.getOrCatch(TAG) {
        delay(2.seconds)
        _userSignInState.value = (username == USERNAME && password == PASSWORD)
        if (!_userSignInState.value) {
            throw Exception("Invalid credentials")
        }
    }

    /**
     * Sign out the user.
     */
    suspend fun signOut(): Result<Unit> = dependencies.getOrCatch(TAG) {
        delay(2.seconds)
        _userSignInState.value = false
        _userSignInState.value
    }

    companion object {
        private const val TAG = "AuthenticationManager"

        private const val USERNAME = "admin"
        private const val PASSWORD = "admin"
    }
}
