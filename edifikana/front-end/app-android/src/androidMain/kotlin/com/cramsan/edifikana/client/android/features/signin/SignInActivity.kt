package com.cramsan.edifikana.client.android.features.signin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cramsan.edifikana.client.android.ui.theme.AppTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInActivity : ComponentActivity() {

    private val viewModel: SignInViewModel by viewModels()

    // See: https://developer.android.com/training/basics/intents/result
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        viewModel.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Trigger the flow and start listening for values.
                // Note that this happens when lifecycle is STARTED and stops
                // collecting when the lifecycle is STOPPED
                viewModel.events.collect { event ->
                    when (event) {
                        is SignInActivityEvents.LaunchSignIn -> { signIn() }
                        is SignInActivityEvents.Noop -> {}
                        is SignInActivityEvents.CloseSignIn -> finish()
                    }
                }
            }
        }

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            AppTheme {
                SignInScreen(
                    uiState = uiState,
                    signSingInClicked = { viewModel.enforceSignIn() },
                    infoButtonClicked = { viewModel.showAccessCodeDialog() },
                    onDismissRequest = { viewModel.closeAccessCodeDialog() },
                    onCodeSubmitClicked = { viewModel.submitAccessCode(it) },
                )
            }
        }

        viewModel.enforceSignIn()
    }

    private fun signIn() {
        // Choose authentication providers
        val providers = arrayListOf(
            // AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            // AuthUI.IdpConfig.EmailBuilder().build(),
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }
}
