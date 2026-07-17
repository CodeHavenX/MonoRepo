package com.cramsan.architecture.server.test.supabase

import com.cramsan.framework.utils.password.generateRandomPassword
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient

/**
 * A real Supabase session obtained for test/dev purposes: an auth user id and a live access token.
 */
data class SupabaseTestSession(val userId: String, val accessToken: String)

/**
 * Creates a brand-new, auto-confirmed Supabase Auth user for [email] and signs in as them,
 * returning a real access token.
 *
 * Builds its own throwaway [Auth] client rather than accepting one from the caller: every
 * backend's `SupabaseIntegrationTest` injects a shared, service-role-keyed `Auth`/`SupabaseClient`
 * singleton that every other datastore call in the test relies on falling back to the service-role
 * key (see `SupabaseClient.resolveAccessToken`, which prefers `Auth.currentAccessTokenOrNull()`
 * over the service-role key the instant a session exists). Signing in on that shared instance would
 * silently switch every subsequent Postgrest/Storage/admin call — including teardown deletions — to
 * the freshly signed-in test user's identity.
 */
suspend fun createAndSignInSupabaseTestUser(
    supabaseUrl: String,
    supabaseServiceRoleKey: String,
    email: String,
    password: String = generateRandomPassword(),
): SupabaseTestSession =
    withThrowawaySupabaseAuth(supabaseUrl, supabaseServiceRoleKey) { auth ->
        val userInfo =
            auth.admin.createUserWithEmail {
                this.email = email
                this.password = password
                autoConfirm = true
            }
        signInAndCapture(auth, userInfo.id, email, password)
    }

/**
 * Sets/overwrites a password on an EXISTING Supabase Auth user (e.g. an OTP-only seeded fixture
 * from a project's `supabase/seed.sql`) via the admin API, then signs in as them. Use this to
 * authenticate as a specific pre-seeded fixture rather than a throwaway blank user.
 *
 * See [createAndSignInSupabaseTestUser] for why this builds its own throwaway [Auth] client.
 */
suspend fun signInAsExistingSupabaseUser(
    supabaseUrl: String,
    supabaseServiceRoleKey: String,
    userId: String,
    email: String,
    password: String = generateRandomPassword(),
): SupabaseTestSession =
    withThrowawaySupabaseAuth(supabaseUrl, supabaseServiceRoleKey) { auth ->
        auth.admin.updateUserById(userId) { this.password = password }
        signInAndCapture(auth, userId, email, password)
    }

private suspend fun signInAndCapture(
    auth: Auth,
    userId: String,
    email: String,
    password: String,
): SupabaseTestSession {
    auth.signInWith(Email) {
        this.email = email
        this.password = password
    }
    val token =
        auth.currentAccessTokenOrNull()
            ?: error("Sign-in for '$email' did not produce an access token.")
    return SupabaseTestSession(userId, token)
}

private suspend fun <T> withThrowawaySupabaseAuth(
    supabaseUrl: String,
    supabaseServiceRoleKey: String,
    block: suspend (Auth) -> T,
): T {
    val client =
        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseServiceRoleKey,
        ) {
            install(Auth)
        }
    return try {
        block(client.auth)
    } finally {
        client.close()
    }
}
