package com.cramsan.flyerboard.client.lib.service

import com.cramsan.flyerboard.lib.model.UserId
import kotlinx.coroutines.flow.StateFlow

/**
 * Service to manage authentication.
 */
interface AuthService {

    /**
     * Registers a new user with the given [email] and [password].
     */
    suspend fun signUp(email: String, password: String): Result<Unit>

    /**
     * Signs in an existing user with [email] and [password].
     */
    suspend fun signIn(email: String, password: String): Result<Unit>

    /**
     * Signs out the currently authenticated user.
     */
    suspend fun signOut(): Result<Unit>

    /**
     * Returns true if a user is currently authenticated (session is valid).
     */
    suspend fun isAuthenticated(): Result<Boolean>

    /**
     * Returns the current session's access token, or null if not authenticated.
     */
    fun getAccessToken(): String?

    /**
     * Returns the current authenticated user's ID, or null if not authenticated.
     */
    fun currentUserId(): UserId?

    /**
     * Observable flow of the current user ID. Emits null when signed out.
     */
    fun activeUser(): StateFlow<UserId?>
}
