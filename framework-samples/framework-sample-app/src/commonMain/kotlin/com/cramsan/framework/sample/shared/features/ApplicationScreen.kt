package com.cramsan.framework.sample.shared.features

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.framework.sample.shared.features.main.mainNavGraphNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * Application screen.
 */
@Composable
fun ApplicationScreen(
    viewModel: ApplicationViewModel = koinViewModel(),
) {
    val navController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            is SampleApplicationViewModelEvent.SampleApplicationEventWrapper -> {
                handleApplicationEvent(
                    navController = navController,
                    scope = this,
                    snackbarHostState = snackbarHostState,
                    applicationEvent = event.event,
                )
            }
        }
    }

    MaterialTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) {
            ApplicationNavigationHost(
                navHostController = navController,
            )
        }
    }
}

@Suppress("CyclomaticComplexMethod")
private fun handleApplicationEvent(
    navController: NavHostController,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    applicationEvent: SampleWindowEvent,
) {
    when (val event = applicationEvent) {
        is SampleWindowEvent.NavigateToNavGraph -> {
            if (event.clearStack) {
                while (navController.currentBackStack.value.isNotEmpty()) {
                    navController.popBackStack()
                }
            } else if (event.clearTop) {
                navController.popBackStack()
            }
            navController.navigate(event.destination)
        }
        is SampleWindowEvent.NavigateToScreen -> {
            navController.navigate(event.destination)
        }
        is SampleWindowEvent.NavigateBack -> {
            navController.popBackStack()
        }
        is SampleWindowEvent.CloseNavGraph -> {
            val currentNavGraph = navController.currentBackStack.value.reversed().find {
                it.destination.navigatorName == "navigation"
            }
            currentNavGraph?.destination?.route?.let {
                navController.popBackStack(it, inclusive = true)
            }
        }
        is SampleWindowEvent.ShowSnackbar -> {
            scope.launch {
                handleSnackbarEvent(
                    snackbarHostState = snackbarHostState,
                    event = event,
                ) { }
            }
        }
    }
}

/**
 * Handle the [event] and use it to display a snackbar from the [snackbarHostState].
 * The [onResult] callback is called with the result of the snackbar.
 */
private suspend fun handleSnackbarEvent(
    snackbarHostState: SnackbarHostState,
    event: SampleWindowEvent.ShowSnackbar,
    onResult: (SnackbarResult) -> Unit,
) {
    snackbarHostState.currentSnackbarData?.dismiss()
    val result = snackbarHostState
        .showSnackbar(
            message = event.message,
            duration = SnackbarDuration.Short,
        )
    onResult(result)
}

@Composable
private fun ApplicationNavigationHost(
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = ApplicationNavGraphDestination.MainDestination,
    ) {
        mainNavGraphNavigation()
    }
}

/**
 * Application content with DI context.
 */
@Composable
expect fun ComposableKoinContext(content: @Composable () -> Unit)
