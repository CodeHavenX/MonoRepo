package com.cramsan.framework.sample.shared.features.main.assertutil

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
import androidx.compose.ui.Modifier
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.viewmodel.koinViewModel

/**
 * AssertUtil screen.
 *
 * Demonstrates all [com.cramsan.framework.assertlib.AssertUtilInterface] API methods.
 * Results are fire-and-forget — check Logcat for assertion outcomes.
 */
@Composable
fun AssertUtilScreen(
    viewModel: AssertUtilViewModel = koinViewModel(),
) {
    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            AssertUtilEvent.Noop -> Unit
        }
    }

    AssertUtilContent(
        onAssertTrue = { viewModel.assertTrue() },
        onAssertFalse = { viewModel.assertFalse() },
        onAssertFalsePasses = { viewModel.assertFalsePasses() },
        onAssertFalseFails = { viewModel.assertFalseFails() },
        onAssertNullPasses = { viewModel.assertNullPasses() },
        onAssertNotNullPasses = { viewModel.assertNotNullPasses() },
        onAssertFailure = { viewModel.assertFailure() },
        onBack = { viewModel.navigateBack() },
    )
}

/**
 * Content of the AssertUtil screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AssertUtilContent(
    onAssertTrue: () -> Unit,
    onAssertFalse: () -> Unit,
    onAssertFalsePasses: () -> Unit,
    onAssertFalseFails: () -> Unit,
    onAssertNullPasses: () -> Unit,
    onAssertNotNullPasses: () -> Unit,
    onAssertFailure: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assert Util") },
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
                Text("Results are logged — check Logcat")
                Button(onClick = onAssertTrue, modifier = modifier) { Text("assert(true) — passes") }
                Button(onClick = onAssertFalse, modifier = modifier) { Text("assert(false) — logs error") }
                Button(onClick = onAssertFalsePasses, modifier = modifier) { Text("assertFalse(false) — passes") }
                Button(onClick = onAssertFalseFails, modifier = modifier) { Text("assertFalse(true) — logs error") }
                Button(onClick = onAssertNullPasses, modifier = modifier) { Text("assertNull(null) — passes") }
                Button(onClick = onAssertNotNullPasses, modifier = modifier) { Text("assertNotNull(value) — passes") }
                Button(onClick = onAssertFailure, modifier = modifier) { Text("assertFailure() — logs error") }
            },
        )
    }
}
