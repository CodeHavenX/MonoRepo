package com.cramsan.edifikana.client.lib.features.auth.passwordreset

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaPrimaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTextField
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.edifikana_string_email
import edifikana_lib.password_reset_screen_send_button
import edifikana_lib.password_reset_screen_subtitle
import edifikana_lib.password_reset_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Password reset screen — email entry step.
 */
@Composable
fun PasswordResetScreen(
    destination: AuthDestination.PasswordResetDestination,
    viewModel: PasswordResetViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize(destination.prefillEmail)
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            PasswordResetEvent.Noop -> Unit
        }
    }

    PasswordResetContent(
        uiState = uiState,
        onEmailValueChange = { viewModel.changeEmailValue(it) },
        onSendClicked = { viewModel.sendPasswordReset() },
        onCloseClicked = { viewModel.navigateBack() },
    )
}

/**
 * Content of the password reset screen.
 */
@Composable
internal fun PasswordResetContent(
    uiState: PasswordResetUIState,
    modifier: Modifier = Modifier,
    onEmailValueChange: (String) -> Unit,
    onSendClicked: () -> Unit,
    onCloseClicked: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.password_reset_screen_title),
                onNavigationIconSelected = onCloseClicked,
            )
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            sectionContent = { sectionModifier ->
                Text(
                    text = stringResource(Res.string.password_reset_screen_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = sectionModifier,
                )

                AnimatedContent(
                    uiState.errorMessages,
                    modifier = sectionModifier,
                    transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                ) {
                    if (!it.isNullOrEmpty()) {
                        it.forEach { errorMessage ->
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }

                EdifikanaTextField(
                    value = uiState.email,
                    onValueChange = onEmailValueChange,
                    modifier = sectionModifier,
                    placeholder = stringResource(Res.string.edifikana_string_email),
                    maxLines = 1,
                    singleLine = true,
                )
            },
            buttonContent = { buttonModifier ->
                EdifikanaPrimaryButton(
                    text = stringResource(Res.string.password_reset_screen_send_button),
                    onClick = onSendClicked,
                    modifier = buttonModifier,
                )
            },
            overlay = {
                LoadingAnimationOverlay(uiState.isLoading)
            },
            contentAlignment = Alignment.Center,
        )
    }
}
