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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlin.toString

/**
 * Validation screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun ValidationScreen(
    viewModel: ValidationViewModel = koinViewModel(),
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
                    ValidationEvent.Noop -> Unit
                }
            }
        }
    }

    // Render the screen
    ValidationContent(
        uiState = uiState,
        onBackClicked = {
            viewModel.navigateBack()
        }
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun ValidationContent(
    uiState: ValidationUIState,
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
                            TextField(
                                value = uiState.otpCode.getOrNull(it)?.toString() ?: "",
                                onValueChange = { newValue ->
                                    // Handle OTP input change
                                },
                                singleLine = true,
                                modifier = Modifier.width(40.dp),
                                shape = RoundedCornerShape(40)
                            )
                        }
                    }
                }
            )
        }
    }
}
