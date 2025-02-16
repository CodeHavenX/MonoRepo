package com.cramsan.edifikana.client.lib.features.auth.signup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.client.ui.components.ScreenLayout
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.theme.Padding
import edifikana_lib.Res
import edifikana_lib.sign_up_screen_text_email
import edifikana_lib.sign_up_screen_text_first_name
import edifikana_lib.sign_up_screen_text_last_name
import edifikana_lib.sign_up_screen_text_password
import edifikana_lib.sign_up_screen_text_phone_number
import edifikana_lib.sign_up_screen_text_policy
import edifikana_lib.sign_up_screen_text_sign_up
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Sign Up screen
 */
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = koinViewModel(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.events.collectAsState(SignUpEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initializePage()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        viewModel.clearPage()
    }

    LaunchedEffect(event) {
        when (val localEvent = event) {
            SignUpEvent.Noop -> Unit
            is SignUpEvent.TriggerEdifikanaApplicationEvent -> {
                applicationViewModel.executeEvent(localEvent.applicationEvent)
            }
        }
    }

    SignUpContent(
        uistate = uiState,
        onUsernameEmailValueChange = { viewModel.onUsernameEmailValueChange(it) },
        onUsernamePhoneNumberValueChange = { viewModel.onUsernameEmailValueChange(it) },
        onPasswordValueChange = { viewModel.onPasswordValueChange(it) },
        onFirstNameValueChange = { viewModel.onFirstNameValueChange(it) },
        onLastNameValueChange = { viewModel.onLastNameValueChange(it) },
        onPolicyChecked = { viewModel.onPolicyChecked(it) },
        onSignUpClicked = { viewModel.signUp() },
        onCloseClicked = { viewModel.navigateBack() },
    )
}

@Composable
internal fun SignUpContent(
    uistate: SignUpUIState,
    modifier: Modifier = Modifier,
    onFirstNameValueChange: (String) -> Unit,
    onLastNameValueChange: (String) -> Unit,
    onUsernameEmailValueChange: (String) -> Unit,
    onUsernamePhoneNumberValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onPolicyChecked: (Boolean) -> Unit,
    onSignUpClicked: () -> Unit,
    onCloseClicked: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = "Sign Up",
                onCloseClicked = onCloseClicked,
            )
        },
    ) { innerPadding ->
        // Render the screen
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            ScreenLayout(
                sectionContent = { modifier ->
                    AnimatedContent(
                        uistate.signUpForm.errorMessage,
                        modifier = modifier,
                        transitionSpec = {
                            fadeIn()
                                .togetherWith(
                                    fadeOut()
                                )
                        },
                    ) {
                        val showErrorMessage = it.isNullOrBlank().not()
                        if (showErrorMessage) {
                            Text(
                                it.orEmpty(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }

                    OutlinedTextField(
                        value = uistate.signUpForm.firstName,
                        onValueChange = { onFirstNameValueChange(it) },
                        modifier = modifier,
                        label = { Text(stringResource(Res.string.sign_up_screen_text_first_name)) },
                        maxLines = 1,
                    )

                    OutlinedTextField(
                        value = uistate.signUpForm.lastName,
                        onValueChange = { onLastNameValueChange(it) },
                        modifier = modifier,
                        label = { Text(stringResource(Res.string.sign_up_screen_text_last_name)) },
                        maxLines = 1,
                    )
                    OutlinedTextField(
                        value = uistate.signUpForm.usernameEmail,
                        onValueChange = { onUsernameEmailValueChange(it) },
                        modifier = modifier,
                        label = { Text(stringResource(Res.string.sign_up_screen_text_email)) },
                        maxLines = 1,
                    )
                    OutlinedTextField(
                        value = uistate.signUpForm.usernamePhone,
                        onValueChange = { onUsernamePhoneNumberValueChange(it) },
                        modifier = modifier,
                        label = { Text(stringResource(Res.string.sign_up_screen_text_phone_number)) },
                        maxLines = 1,
                    )
                    OutlinedTextField(
                        value = uistate.signUpForm.password,
                        onValueChange = { onPasswordValueChange(it) },
                        modifier = modifier,
                        label = { Text(stringResource(Res.string.sign_up_screen_text_password)) },
                        maxLines = 1,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                    )

                    var isChecked by remember { mutableStateOf(false) }
                    val interactionSource = remember { MutableInteractionSource() }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = ripple(),
                            ) {
                                isChecked = !isChecked
                                onPolicyChecked(isChecked)
                            }
                            .padding(Padding.XX_SMALL)
                    ) {
                        Checkbox(
                            checked = uistate.signUpForm.policyChecked,
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
                    Button(
                        modifier = modifier,
                        enabled = uistate.signUpForm.registerEnabled,
                        onClick = onSignUpClicked,
                    ) {
                        Text(stringResource(Res.string.sign_up_screen_text_sign_up))
                    }
                },
            )
        }
        LoadingAnimationOverlay(uistate.isLoading)
    }
}
