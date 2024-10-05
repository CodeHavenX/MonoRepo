package com.cramsan.edifikana.client.lib.features.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.cramsan.edifikana.client.lib.ui.theme.AppTheme
import org.koin.compose.koinInject

/**
 * Edifikana application screen.
 */
@Composable
fun EdifikanaApplicationScreen(
    viewModel: MainActivityViewModel = koinInject(),
    eventHandler: EdifikanaMainScreenEventHandler,
) {
    val event by viewModel.events.collectAsState(MainActivityEvent.Noop)
    val delegatedEvent by viewModel.delegatedEvents.collectAsState(MainActivityDelegatedEvent.Noop)
    val navController = rememberNavController()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.enforceAuth()
    }

    LaunchedEffect(event) {
        when (val mainActivityEvent = event) {
            MainActivityEvent.Noop -> Unit
            is MainActivityEvent.OpenCamera -> {
                eventHandler.openCamera(mainActivityEvent)
            }
            is MainActivityEvent.OpenImageExternally -> {
                eventHandler.openImageExternally(mainActivityEvent)
            }
            is MainActivityEvent.OpenPhotoPicker -> {
                eventHandler.openPhotoPicker(mainActivityEvent)
            }
            is MainActivityEvent.ShareContent -> {
                eventHandler.shareContent(mainActivityEvent)
            }
            is MainActivityEvent.ShowSnackbar -> {
                eventHandler.showSnackbar(mainActivityEvent)
            }
            is MainActivityEvent.Navigate -> {
                navController.navigate(mainActivityEvent.route) {
                    launchSingleTop = true
                }
            }
            is MainActivityEvent.NavigateBack -> {
                navController.popBackStack()
            }
            is MainActivityEvent.NavigateToRootPage -> {
                navController.navigate(mainActivityEvent.route) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(navController.graph.findStartDestination().route!!) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            }
        }
    }
    ComposableKoinContext {
        AppTheme {
            MainActivityScreen(
                navController = navController,
                mainActivityDelegatedEvent = delegatedEvent,
                onMainActivityEventInvoke = { viewModel.executeMainActivityEvent(it) },
            )
        }
    }
}

/**
 * Edifikana main screen event handler.
 */
@Composable
expect fun ComposableKoinContext(content: @Composable () -> Unit)
