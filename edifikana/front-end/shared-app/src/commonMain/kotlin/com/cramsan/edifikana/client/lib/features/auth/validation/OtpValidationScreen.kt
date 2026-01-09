package com.cramsan.edifikana.client.lib.features.auth.validation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.ui.components.ScreenLayout
import com.cramsan.ui.components.otpfield.OtpSection
import edifikana_lib.Res
import edifikana_lib.alpacaIcon
import edifikana_lib.otp_validation_screen_text
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Validation screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun OtpValidationScreen(
    destination: AuthDestination.ValidationDestination,
    viewModel: OtpValidationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val screenScope = rememberCoroutineScope()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initializeOTPValidationScreen(destination.userEmail, destination.accountCreationFlow)
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    LaunchedEffect(screenScope) {
        screenScope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    OtpValidationEvent.Noop -> Unit
                }
            }
        }
    }

    // Render the screen
    OtpValidationContent(
        uiState = uiState,
        onLoginClicked = {
            viewModel.signInWithOtp()
        },
        onBackClicked = {
            viewModel.navigateBack()
        },
        onValueChanged = { newValue ->
            viewModel.updateOtpCode(newValue)
        },
    )
}

/**
 * Content of the OTP Validation screen.
 */
@Composable
internal fun OtpValidationContent(
    uiState: OtpValidationUIState,
    modifier: Modifier = Modifier,
    onLoginClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onValueChanged: (String) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                onNavigationIconSelected = onBackClicked,
            )
        },
    ) { innerPadding ->
        // Render the screen
        ScreenLayout(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            sectionContent = { sectionModifier ->
                // Image above the text
                Image(
                    painter = painterResource(Res.drawable.alpacaIcon),
                    contentDescription = "Validation Image",
                    modifier = sectionModifier.size(
                        width = 150.dp,
                        height = 150.dp
                    ),
                )
                // Display text message
                Text(
                    text = stringResource(Res.string.otp_validation_screen_text),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = sectionModifier
                        .wrapContentWidth(),
                )

                // Error message
                AnimatedContent(
                    uiState.errorMessage,
                ) {
                    val showErrorMessage = it.isNullOrBlank().not()
                    if (showErrorMessage) {
                        // Render the error message
                        Text(
                            it,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = sectionModifier
                                .wrapContentWidth(),
                        )
                    }
                }

                OtpSection(
                    uiState.otpCode,
                    onValueChanged,
                    modifier = sectionModifier
                        .wrapContentWidth(),
                )

                // Submit button
                ElevatedButton(
                    onClick = {
                        onLoginClicked()
                    },
                    enabled = uiState.enabledContinueButton,
                    modifier = sectionModifier
                        .padding(top = 16.dp)
                        .wrapContentWidth()
                ) {
                    Text("Login")
                }
            }
        )
    }
}
