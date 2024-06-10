package com.cramsan.edifikana.client.android.service

import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.auth.SignInResult
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.edifikana.lib.firestore.User
import com.cramsan.edifikana.lib.firestore.UserPk
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
) : AuthService {
    override suspend fun isSignedIn(enforceAllowList: Boolean): Result<Boolean> = runSuspendCatching {
        val isSignedIn = auth.currentUser != null

        if (!isSignedIn) {
            false
        } else if (enforceAllowList) {
            (auth.currentUser?.isAnonymous == true) ||
                userExists(auth.currentUser?.email.orEmpty())
        } else {
            true
        }
    }

    private suspend fun userExists(email: String): Boolean {
        return getUser(UserPk(email)).isSuccess
    }

    override suspend fun getUser(userPk: UserPk): Result<User> = runSuspendCatching {
        fireStore.collection(User.COLLECTION)
            .document(userPk.documentPath)
            .get()
            .await()
            .toObject(User::class.java) ?: throw RuntimeException("Employee $userPk not found")
    }

    override suspend fun signInAnonymously(): Result<Unit> = runSuspendCatching {
        auth.signInAnonymously().await()
        logI(TAG, "Successful anonymous sign in.")
    }

    override suspend fun handleSignInResult(signInResult: SignInResult): Result<Boolean> = runSuspendCatching {
        if (signInResult.success) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
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

    companion object {
        private const val TAG = "FirebaseAuthService"
    }
}
