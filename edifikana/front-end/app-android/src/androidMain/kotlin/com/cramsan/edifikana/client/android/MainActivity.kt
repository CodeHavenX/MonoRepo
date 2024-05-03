package com.cramsan.edifikana.client.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.cramsan.edifikana.client.android.camera.CameraContract
import com.cramsan.edifikana.client.android.theme.AppTheme
import com.cramsan.edifikana.client.android.utils.shareToWhatsApp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    // See: https://developer.android.com/training/basics/intents/result
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        viewModel.onSignInResult(res)
    }

    private val cameraLauncher = registerForActivityResult(CameraContract()) { filePath ->
        viewModel.handleCameraResult(filePath)
    }

    public override fun onStart() {
        super.onStart()
        viewModel.checkSignIn()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val events by viewModel.events.collectAsState()

            LaunchedEffect(events) {
                when (val activityEvent = events) {
                    is MainActivityEvents.LaunchSignIn -> {
                        signIn()
                    }
                    is MainActivityEvents.Noop -> { }
                    is MainActivityEvents.OnCameraComplete -> { }
                    is MainActivityEvents.ShareToWhatsApp -> {
                        shareToWhatsApp(activityEvent.text, activityEvent.imageUri)
                    }
                }
            }

            AppTheme {
                MainScreen(
                    mainActivityEvents = events,
                ) {
                    cameraLauncher.launch(it)
                }
            }
        }

    }

    fun signIn() {
        // Choose authentication providers
        val providers = arrayListOf(
            //AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }
}
