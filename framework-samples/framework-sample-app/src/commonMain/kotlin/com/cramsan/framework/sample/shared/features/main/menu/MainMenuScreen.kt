package com.cramsan.framework.sample.shared.features.main.menu

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.framework.sample.shared.features.ApplicationViewModel
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main Menu screen
 */
@Composable
fun MainMenuScreen(
    viewModel: MainMenuViewModel = koinViewModel(),
    applicationViewModel: ApplicationViewModel = koinInject(),
) {
    val event by viewModel.events.collectAsState(MainMenuEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
    }

    LaunchedEffect(event) {
        when (val localEvent = event) {
            is MainMenuEvent.Noop -> { }
            is MainMenuEvent.TriggerApplicationEvent -> {
                applicationViewModel.executeEvent(localEvent.applicationEvent)
            }
        }
    }

    MainMenuContent(
        modifier = Modifier,
        onHaltUtilSelected = { viewModel.navigateToHaltUtil() },
    )
}

@Composable
internal fun MainMenuContent(
    modifier: Modifier = Modifier,
    onHaltUtilSelected: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        // Render the screen
        ScreenLayout(
            modifier = Modifier
                .padding(innerPadding),
            sectionContent = { modifier ->
                Button(
                    onClick = onHaltUtilSelected,
                    modifier = modifier,
                ) {
                    Text("Halt Util")
                }
            },
            buttonContent = { modifier ->
            },
        )
    }
}
