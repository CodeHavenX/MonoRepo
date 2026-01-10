package com.cramsan.edifikana.client.lib.features.auth.signup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import com.cramsan.ui.theme.Padding
import edifikana_lib.Res
import edifikana_lib.sign_up_screen_text_email
import edifikana_lib.sign_up_screen_text_first_name
import edifikana_lib.sign_up_screen_text_last_name
import edifikana_lib.sign_up_screen_text_phone_number
import edifikana_lib.sign_up_screen_text_policy
import edifikana_lib.sign_up_screen_text_sign_up
import edifikana_lib.signup_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Sign Up screen
 */
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initializePage()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            SignUpEvent.Noop -> Unit
        }
    }

    SignUpContent(
        uiState = uiState,
        onEmailValueChange = { viewModel.onEmailValueChange(it) },
        onPhoneNumberValueChange = { viewModel.onPhoneNumberValueChange(it) },
        onFirstNameValueChange = { viewModel.onFirstNameValueChange(it) },
        onLastNameValueChange = { viewModel.onLastNameValueChange(it) },
        onPolicyChecked = { viewModel.onPolicyChecked(it) },
        onSignUpClicked = { viewModel.signUp() },
        onCloseClicked = { viewModel.navigateBack() },
    )
}

@Composable
internal fun SignUpContent(
    uiState: SignUpUIState,
    modifier: Modifier = Modifier,
    onFirstNameValueChange: (String) -> Unit,
    onLastNameValueChange: (String) -> Unit,
    onEmailValueChange: (String) -> Unit,
    onPhoneNumberValueChange: (String) -> Unit,
    onPolicyChecked: (Boolean) -> Unit,
    onSignUpClicked: () -> Unit,
    onCloseClicked: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.signup_screen_title),
                onNavigationIconSelected = onCloseClicked,
            )
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            sectionContent = { modifier ->
                AnimatedContent(
                    uiState.errorMessages,
                    modifier = modifier,
                    transitionSpec = {
                        fadeIn()
                            .togetherWith(
                                fadeOut()
                            )
                    },
                ) {
                    if (!uiState.errorMessages.isNullOrEmpty()) {
                        it?.forEach { errorMessage ->
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.firstName,
                    onValueChange = { onFirstNameValueChange(it) },
                    modifier = modifier,
                    label = { Text(stringResource(Res.string.sign_up_screen_text_first_name)) },
                    maxLines = 1,
                )

                OutlinedTextField(
                    value = uiState.lastName,
                    onValueChange = { onLastNameValueChange(it) },
                    modifier = modifier,
                    label = { Text(stringResource(Res.string.sign_up_screen_text_last_name)) },
                    maxLines = 1,
                )
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { onEmailValueChange(it) },
                    modifier = modifier,
                    label = { Text(stringResource(Res.string.sign_up_screen_text_email)) },
                    maxLines = 1,
                )
                OutlinedTextField(
                    value = uiState.phoneNumber,
                    onValueChange = { onPhoneNumberValueChange(it) },
                    modifier = modifier,
                    label = { Text(stringResource(Res.string.sign_up_screen_text_phone_number)) },
                    maxLines = 1,
                )
                val interactionSource = remember { MutableInteractionSource() }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = ripple(),
                        ) {
                            onPolicyChecked(!uiState.policyChecked)
                        }
                        .padding(Padding.XX_SMALL)
                ) {
                    Checkbox(
                        checked = uiState.policyChecked,
                        onCheckedChange = null,
                        modifier = Modifier.padding(end = Padding.SMALL),
                    )
                    Text(
                        stringResource(Res.string.sign_up_screen_text_policy),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            buttonContent = { modifier ->
                ElevatedButton(
                    modifier = modifier,
                    enabled = uiState.registerEnabled,
                    onClick = onSignUpClicked,
                ) {
                    Text(stringResource(Res.string.sign_up_screen_text_sign_up))
                }
            },
            overlay = {
                LoadingAnimationOverlay(uiState.isLoading)
            },
            contentAlignment = Alignment.Center,
        )
    }
}
