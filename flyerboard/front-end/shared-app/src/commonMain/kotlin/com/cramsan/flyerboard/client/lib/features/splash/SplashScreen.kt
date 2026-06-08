package com.cramsan.flyerboard.client.lib.features.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.framework.core.compose.navigation.Destination
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import flyerboard_lib.Res
import flyerboard_lib.splash_screen_tagline
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Splash screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun SplashScreen(
    initialDestination: Destination? = null,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // For other possible lifecycle events, see the [Lifecycle.Event] documentation.
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.navigateToMainScreen(initialDestination)
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            SplashEvent.Noop -> Unit
        }
    }

    // Render the screen
    SplashContent(
        content = uiState,
        modifier = modifier,
    )
}

/**
 * Content of the Splash screen.
 */
@Composable
internal fun SplashContent(
    content: SplashUIState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "FLYERBOARD",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = stringResource(Res.string.splash_screen_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = TAGLINE_ALPHA),
            )
            if (content.isLoading) {
                Spacer(modifier = Modifier.height(Padding.LARGE))
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = SPINNER_ALPHA),
                )
            }
        }
    }
}

private const val TAGLINE_ALPHA = 0.75f
private const val SPINNER_ALPHA = 0.8f
