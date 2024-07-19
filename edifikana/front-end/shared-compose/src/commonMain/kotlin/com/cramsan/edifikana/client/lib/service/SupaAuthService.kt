package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.managers.supamappers.toDomainModel
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.auth.SignInResult
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.edifikana.lib.UserPk
import com.cramsan.edifikana.lib.supa.SupabaseModel
import com.cramsan.edifikana.lib.supa.User
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

class SupaAuthService(
    private val auth: Auth,
    private val postgrest: Postgrest,
) : AuthService {
    override suspend fun isSignedIn(enforceAllowList: Boolean): Result<Boolean> = runSuspendCatching(TAG) {
        val isSignedIn = auth.currentUserOrNull() != null

        if (!isSignedIn) {
            false
        } else if (enforceAllowList) {
            (userExists(auth.currentUserOrNull()?.email.orEmpty()))
        } else {
            true
        }
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getUser(userPk: UserPk): Result<UserModel> = runSuspendCatching(TAG) {
        postgrest.from(User.COLLECTION)
            .select {
                filter {
                    eq("pk", userPk.documentPath)
                }
                limit(1)
                single()
            }
            .decodeAs<User>()
            .toDomainModel()
    }

    override suspend fun signInAnonymously(): Result<Unit> = runSuspendCatching(TAG) {
        auth.signInAnonymously()
        logI(TAG, "Successful anonymous sign in.")
    }

    override suspend fun handleSignInResult(signInResult: SignInResult): Result<Boolean> = runSuspendCatching(TAG) {
        if (signInResult.success) {
            // Successfully signed in
            val user = auth.currentUserOrNull()
            logI(TAG, "${user?.email} signed in")
            // ...
        } else if (signInResult.error == null) {
            // User canceled sign in
            logI(TAG, "Sign in was cancelled")
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            logW(TAG, "Sign in failed with", signInResult.error)
        }
        isSignedIn(true).getOrThrow()
    }

    private suspend fun userExists(email: String): Boolean {
        return getUser(UserPk(email)).isSuccess
    }

    companion object {
        private const val TAG = "SupaAuthService"
    }
}
