package com.cramsan.flyerboard.client.lib.features.window

/**
 * Represents the current authentication and authorization state of the session.
 * Using a sealed class makes the impossible state isAdmin=true, isAuthenticated=false
 * unrepresentable at compile time.
 */
sealed class AuthState {
    /** Auth state is undetermined. Useful for cases like a loading state **/
    data object Undefined : AuthState()

    /** The user is not signed in. */
    data object Unauthenticated : AuthState()

    /** The user is signed in. [isAdmin] is true when the account has moderation privileges. */
    data class Authenticated(val isAdmin: Boolean) : AuthState()
}
