package com.cramsan.framework.sample.shared.features.main.menu

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main Menu screen
 */
@Composable
fun MainMenuScreen(
    viewModel: MainMenuViewModel = koinViewModel(),
) {
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                MainMenuEvent.Noop -> Unit
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
