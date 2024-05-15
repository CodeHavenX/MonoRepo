package com.cramsan.edifikana.client.android.managers

import android.app.Activity
import com.cramsan.edifikana.client.android.utils.getOrCatch
import com.cramsan.edifikana.lib.firestore.User
import com.cramsan.edifikana.lib.firestore.UserPk
import com.cramsan.framework.logging.logI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class AuthManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val workContext: WorkContext,
) {
    suspend fun isSignedIn(enforceAllowList: Boolean): Result<Boolean> = workContext.getOrCatch {
        val isSignedIn = auth.currentUser != null

        if (!isSignedIn) {
            false
        } else if (enforceAllowList) {
            (auth.currentUser?.isAnonymous == true) ||
                userExists(auth.currentUser?.email ?: "")
        } else {
            true
        }
    }

    suspend fun getUser(userPk: UserPk): Result<User> = workContext.getOrCatch {
        fireStore.collection(User.COLLECTION)
            .document(userPk.documentPath)
            .get()
            .await()
            .toObject(User::class.java) ?: throw RuntimeException("Employee $userPk not found")
    }

    private suspend fun userExists(email: String): Boolean {
        return getUser(UserPk(email)).isSuccess
    }

    suspend fun signInAnonymously(): Result<AuthResult> = workContext.getOrCatch {
        val result = auth.signInAnonymously().await()
        logI(TAG, "Successful anonymous sign in.")
        result
    }

    suspend fun handleSignInResult(result: FirebaseAuthUIAuthenticationResult?): Result<Boolean> = workContext.getOrCatch {
        val response = result?.idpResponse
        if (result?.resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // ...
        } else if (response == null) {
            // User canceled sign in
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
        isSignedIn(true).getOrThrow()
    }

    companion object {
        const val TAG = "AuthManager"
    }
}
