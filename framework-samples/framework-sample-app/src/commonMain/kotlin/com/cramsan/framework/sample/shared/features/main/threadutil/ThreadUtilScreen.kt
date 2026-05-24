package com.cramsan.framework.sample.shared.features.main.threadutil

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
 * ThreadUtil screen.
 *
 * Demonstrates all [com.cramsan.framework.thread.ThreadUtilInterface] API methods.
 */
@Composable
fun ThreadUtilScreen(
    viewModel: ThreadUtilViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            ThreadUtilEvent.Noop -> Unit
        }
    }

    ThreadUtilContent(
        uiState = uiState,
        onCheckIsUIThread = { viewModel.checkIsUIThread() },
        onCheckIsBackgroundThread = { viewModel.checkIsBackgroundThread() },
        onAssertIsUIThread = { viewModel.assertIsUIThread() },
        onAssertIsBackgroundThread = { viewModel.assertIsBackgroundThread() },
        onBack = { viewModel.navigateBack() },
    )
}

/**
 * Content of the ThreadUtil screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThreadUtilContent(
    uiState: ThreadUtilUIState,
    onCheckIsUIThread: () -> Unit,
    onCheckIsBackgroundThread: () -> Unit,
    onAssertIsUIThread: () -> Unit,
    onAssertIsBackgroundThread: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thread Util") },
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
                Text("isUIThread: ${uiState.isUIThread ?: "(not checked)"}")
                Text("isBackgroundThread: ${uiState.isBackgroundThread ?: "(not checked)"}")
                Text("Last action: ${uiState.lastAction}")
                Button(onClick = onCheckIsUIThread, modifier = modifier) { Text("isUIThread()") }
                Button(onClick = onCheckIsBackgroundThread, modifier = modifier) { Text("isBackgroundThread()") }
                Button(onClick = onAssertIsUIThread, modifier = modifier) { Text("assertIsUIThread()") }
                Button(onClick = onAssertIsBackgroundThread, modifier = modifier) { Text("assertIsBackgroundThread()") }
            },
        )
    }
}
