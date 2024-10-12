package com.cramsan.edifikana.client.lib.service.supabase

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.auth.SignInResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email

class SupabaseAuthService(
    private val supabase: SupabaseClient,
) : AuthService {
    override suspend fun isSignedIn(enforceAllowList: Boolean): Result<Boolean> {
        TODO()
    }

    override suspend fun getUser(userPk: UserPk): Result<UserModel> {
        TODO("Not yet implemented")
    }

    override suspend fun signInAnonymously(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun handleSignInResult(signInResult: SignInResult): Result<Boolean> {
        TODO("Not yet implemented")
    }
}