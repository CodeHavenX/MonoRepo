package com.cramsan.edifikana.client.lib.features.signinv2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.main.MainActivityDelegatedEvent
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.ui.components.LoadingAnimationOverlay
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

/**
 * SignInV2 screen
 */
@Composable
fun SignInV2Screen(
    mainActivityDelegatedEvent: MainActivityDelegatedEvent,
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
    viewModel: SignInV2ViewModel = koinInject(),
    composeAuth: ComposeAuth = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.events.collectAsState(SignInV2Event.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    }

    LaunchedEffect(event) {
        when (val localEvent = event) {
            is SignInV2Event.Noop -> { }
            is SignInV2Event.TriggerMainActivityEvent -> {
                onMainActivityEventInvoke(localEvent.mainActivityEvent)
            }
            is SignInV2Event.LaunchSignIn -> { }
        }
    }

    LaunchedEffect(mainActivityDelegatedEvent) {
        when (mainActivityDelegatedEvent) {
            else -> Unit
        }
    }

    onTitleChange("")
    SignInV2Content(
        isLoading = false,
        composeAuth = composeAuth,
        onResult = { result ->
            viewModel.handleSignInResult(result)
        },
        fallback = {
            viewModel.handleFallback()
        }
    )
}

@OptIn(AuthUiExperimental::class)
@Composable
private fun SignInV2Content(
    isLoading: Boolean,
    composeAuth: ComposeAuth,
    onResult: (NativeSignInResult) -> Unit,
    fallback: suspend () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        val action = composeAuth.rememberSignInWithGoogle(
            onResult = onResult,
            fallback = fallback,
        )

        OutlinedButton(
            onClick = { action.startFlow() },
            content = { ProviderButtonContent(Google) }
        )
    }
    LoadingAnimationOverlay(isLoading)
}

@Preview
@Composable
private fun ViewStaffScreenPreview() {
    // TODO: Move to a centralized place in core-compose
    AssertUtil.setInstance(NoopAssertUtil)

    SignInV2Content(
        isLoading = true,
        composeAuth = koinInject(),
        onResult = {},
        fallback = {},
    )
}
