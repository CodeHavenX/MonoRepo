package com.cramsan.framework.sample.shared.features.main.userevents

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.viewmodel.koinViewModel

/**
 * UserEvents screen.
 *
 * Demonstrates all [com.cramsan.framework.userevents.UserEventsInterface] API methods.
 */
@Composable
fun UserEventsScreen(
    viewModel: UserEventsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            UserEventsEvent.Noop -> Unit
        }
    }

    UserEventsContent(
        uiState = uiState,
        onInitialize = { viewModel.initialize() },
        onLogEvent = { viewModel.logEvent() },
        onLogEventWithMetadata = { viewModel.logEventWithMetadata() },
        onBack = { viewModel.navigateBack() },
    )
}

/**
 * Content of the UserEvents screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UserEventsContent(
    uiState: UserEventsUIState,
    onInitialize: () -> Unit,
    onLogEvent: () -> Unit,
    onLogEventWithMetadata: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Events") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            sectionContent = { modifier ->
                Text("Last action: ${uiState.lastAction}")
                Button(onClick = onInitialize, modifier = modifier) { Text("initialize()") }
                Button(onClick = onLogEvent, modifier = modifier) { Text("log(tag, event)") }
                Button(onClick = onLogEventWithMetadata, modifier = modifier) { Text("log(tag, event, metadata)") }
            },
        )
    }
}
