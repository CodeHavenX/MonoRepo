package com.cramsan.edifikana.client.lib.service.auth

import android.app.Activity
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult

class FirebaseAuthSignInResult(
    private val result: FirebaseAuthUIAuthenticationResult?,
) : SignInResult {
    override val success: Boolean
        get() = result?.resultCode == Activity.RESULT_OK
    override val error: Throwable?
        get() = result?.idpResponse?.error
}
