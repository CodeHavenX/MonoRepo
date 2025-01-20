package com.cramsan.edifikana.client.lib.features.root.debug

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.root.RouteSafePath
import com.cramsan.edifikana.client.lib.features.root.debug.main.DebugScreen
import com.cramsan.edifikana.client.lib.ui.components.EdifikanaTopBar
import org.koin.compose.koinInject

/**
 * Debug activity screen.
 */
@OptIn(RouteSafePath::class)
@Composable
fun DebugActivityScreen(
    viewModel: DebugActivityViewModel = koinInject(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val navController = rememberNavController()

    val viewModelEvent by viewModel.event.collectAsState(DebugActivityEvent.Noop)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            DebugActivityEvent.Noop -> Unit
            is DebugActivityEvent.Navigate -> {
                navController.navigate(event.destination.path)
            }

            is DebugActivityEvent.CloseActivity -> {
                viewModel.closeActivity()
            }

            is DebugActivityEvent.TriggerApplicationEvent -> {
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    Scaffold(
        topBar = {
            EdifikanaTopBar(
                title = "Debug Menu",
                navHostController = navController,
                onCloseClicked = { viewModel.closeActivity() },
            )
        },
    ) { innerPadding ->
        NavigationHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@OptIn(RouteSafePath::class)
@Composable
private fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController,
        startDestination = DebugActivityRoute.Debug.route,
        modifier = modifier,
    ) {
        DebugActivityRoute.entries.forEach {
            when (it) {
                DebugActivityRoute.Debug -> composable(it.route) {
                    DebugScreen()
                }
            }
        }
    }
}
