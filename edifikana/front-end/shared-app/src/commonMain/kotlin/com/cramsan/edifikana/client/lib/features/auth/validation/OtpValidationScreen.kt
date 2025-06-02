package com.cramsan.edifikana.client.lib.features.auth.validation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.alpacaIcon
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Validation screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun OtpValidationScreen(
    viewModel: OtpValidationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val screenScope = rememberCoroutineScope()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.verifyAccount()
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
        onBackClicked = {
            viewModel.navigateBack()
        }
    )
}

/**
 * Content of the OTP Validation screen.
 */
@Composable
internal fun OtpValidationContent(
    uiState: OtpValidationUIState,
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit,
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            ScreenLayout(
                sectionContent = { sectionModifier ->
                    AnimatedContent(
                        uiState.errorMessage,
                    ) {
                        val showErrorMessage = it.isNullOrBlank().not()
                        if (showErrorMessage) {
                            // Render the error message
                            Text(
                                it.orEmpty(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error,
                                modifier = sectionModifier
                                    .wrapContentWidth(),
                            )
                        }
                    }
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
                        "We've sent an OTP code to your email. Please enter it below to complete your signup.",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = sectionModifier
                            .wrapContentWidth(),
                        )
                    // OTP input fields
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = sectionModifier
                            .padding(top = 16.dp)
                            .fillMaxSize()
                    ) {
                        repeat(6) {
                            // val otpChar = uiState.otpCode.getOrNull(it)?.toString() ?: ""
                            var otpChar by remember { mutableStateOf("") }
                            TextField(
                                value = otpChar,
                                onValueChange = { tokenChar ->
                                    if (tokenChar.length <= 1 && tokenChar.all { it.isLetterOrDigit() }) {
                                        otpChar = tokenChar
                                    }
                                    if (tokenChar.isNotEmpty())
                                    otpChar = tokenChar
                                },
                                singleLine = true,
                                modifier = Modifier.width(40.dp),
                                shape = RoundedCornerShape(40)
                            )
                        }
                    }
                    // Submit button
                    Button(
                        onClick = {
                            // Handle OTP submission
                        },
                        enabled = uiState.otpCode.length == 6 && uiState.otpCode.all { it.isLetterOrDigit() },
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
}
