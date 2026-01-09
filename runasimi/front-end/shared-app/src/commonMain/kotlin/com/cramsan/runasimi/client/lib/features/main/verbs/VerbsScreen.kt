package com.cramsan.runasimi.client.lib.features.main.verbs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.runasimi.client.lib.features.main.menu.MenuViewModel
import com.cramsan.runasimi.client.ui.components.RunasimiTopBar
import com.cramsan.runasimi.client.ui.components.card.Card
import com.cramsan.ui.components.ScreenLayout
import com.cramsan.ui.theme.Size
import org.koin.compose.viewmodel.koinViewModel

/**
 * Verbs screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun VerbsScreen(
    modifier: Modifier = Modifier,
    viewModel: VerbsViewModel = koinViewModel(),
    mainMenuViewModel: MenuViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.generateNewConjugation()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            VerbsEvent.Noop -> Unit
        }
    }

    // Render the screen
    VerbsContent(
        content = uiState,
        onNewConjugationRequested = { viewModel.generateNewConjugation() },
        modifier = modifier,
        toggleDrawer = {
            mainMenuViewModel.toggleDrawer()
        },
    )
}

/**
 * Content of the Verbs screen.
 */
@Composable
internal fun VerbsContent(
    content: VerbsUIState,
    modifier: Modifier = Modifier,
    onNewConjugationRequested: () -> Unit = { },
    toggleDrawer: () -> Unit = { },
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            RunasimiTopBar(
                navigationIcon = Icons.Default.Menu,
                onNavigationIconSelected = toggleDrawer,
            )
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            sectionContent = { sectionModifier ->
                // Toggle button to select which direction to start with
                var startInFront by remember { mutableStateOf(true) }
                Row(
                    sectionModifier,
                    horizontalArrangement = Arrangement.End,
                ) {
                    Switch(
                        startInFront,
                        { startInFront = it }
                    )
                }

                // Wrap the Card in a non-clipping layer so animated children can overflow
                Box(
                    modifier = sectionModifier
                        .graphicsLayer { clip = false }
                        .zIndex(1f) // optional: ensure it draws above other siblings
                ) {
                    Card(
                        content.content?.original,
                        content.content?.translated,
                        startInFront = startInFront,
                    )
                }
            },
            buttonContent = { buttonModifier ->
                Row(
                    buttonModifier,
                    horizontalArrangement = Arrangement.spacedBy(Size.x_small)
                ) {
                    Button(
                        onNewConjugationRequested,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Next")
                    }
                }
            }
        )
    }
}
