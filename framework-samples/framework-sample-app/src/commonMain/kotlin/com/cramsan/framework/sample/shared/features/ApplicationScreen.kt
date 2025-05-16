package com.cramsan.framework.sample.shared.features

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.cramsan.framework.core.compose.RouteSafePath
import com.cramsan.framework.sample.shared.features.main.mainActivityNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * Application screen.
 */
@Composable
fun ApplicationScreen() {
    ComposableKoinContext {
        ApplicationContent()
    }
}

@Composable
private fun ApplicationContent(
    viewModel: ApplicationViewModel = koinViewModel(),
) {
    val navController = rememberNavController()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(scope) {
        scope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    is SampleApplicationViewModelEvent.SampleApplicationEventWrapper -> {
                        handleApplicationEvent(
                            navController = navController,
                            scope = scope,
                            snackbarHostState = snackbarHostState,
                            applicationEvent = event.event,
                        )
                    }
                }
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
@OptIn(RouteSafePath::class)
private fun handleApplicationEvent(
    navController: NavHostController,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    applicationEvent: SampleApplicationEvent,
) {
    when (val event = applicationEvent) {
        is SampleApplicationEvent.NavigateToActivity -> {
            if (event.clearStack) {
                while (navController.currentBackStack.value.isNotEmpty()) {
                    navController.popBackStack()
                }
            } else if (event.clearTop) {
                navController.popBackStack()
            }
            navController.navigate(event.destination.path)
        }
        is SampleApplicationEvent.NavigateToScreen -> {
            navController.navigate(event.destination.path)
        }
        is SampleApplicationEvent.NavigateBack -> {
            navController.popBackStack()
        }
        is SampleApplicationEvent.CloseActivity -> {
            val currentActivity = navController.currentBackStack.value.reversed().find {
                ApplicationRoute.fromRoute(it.destination.route) != null
            }
            currentActivity?.destination?.route?.let {
                navController.popBackStack(it, inclusive = true)
            }
        }
        is SampleApplicationEvent.ShowSnackbar -> {
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
    event: SampleApplicationEvent.ShowSnackbar,
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

@OptIn(RouteSafePath::class)
@Composable
private fun ApplicationNavigationHost(
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = ApplicationRoute.MAIN.route,
    ) {
        ApplicationRoute.entries.forEach { route ->
            when (route) {
                ApplicationRoute.MAIN -> mainActivityNavigation(route.route)
            }
        }
    }
}

/**
 * Application content with DI context.
 */
@Composable
expect fun ComposableKoinContext(content: @Composable () -> Unit)
