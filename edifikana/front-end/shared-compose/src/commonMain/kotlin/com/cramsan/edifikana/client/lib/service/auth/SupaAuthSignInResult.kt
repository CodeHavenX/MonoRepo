package com.cramsan.edifikana.client.lib.service.auth

import io.github.jan.supabase.compose.auth.composable.NativeSignInResult

/**
 * Result of a sign in operation.
 */
class SupaAuthSignInResult(
    private val result: NativeSignInResult,
) : SignInResult {
    override val success: Boolean
        get() = result is NativeSignInResult.Success
    override val error: Throwable?
        get() = (result as? NativeSignInResult.Error)?.exception
}
