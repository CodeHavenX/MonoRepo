package com.cramsan.edifikana.client.lib.features

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cramsan.edifikana.client.lib.features.account.accountActivityNavigation
import com.cramsan.edifikana.client.lib.features.admin.adminActivityNavigation
import com.cramsan.edifikana.client.lib.features.auth.authActivityNavigation
import com.cramsan.edifikana.client.lib.features.debug.debugActivityNavigation
import com.cramsan.edifikana.client.lib.features.main.mainActivityNavigation
import com.cramsan.edifikana.client.lib.features.splash.SplashActivityScreen
import com.cramsan.edifikana.client.lib.ui.di.Coil3Provider
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.framework.core.compose.RouteSafePath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Edifikana application screen.
 */
@Composable
fun EdifikanaApplicationScreen(
    eventHandler: EdifikanaMainScreenEventHandler,
) {
    ComposableKoinContext {
        ApplicationContent(eventHandler = eventHandler)
    }
}

@Composable
private fun ApplicationContent(
    viewModel: EdifikanaApplicationViewModel = koinViewModel(),
    eventHandler: EdifikanaMainScreenEventHandler,
) {
    val event by viewModel.events.collectAsState(EdifikanaApplicationEvent.Noop)
    val navController = rememberNavController()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.enforceAuth()
    }

    LaunchedEffect(event) {
        handleApplicationEvent(
            eventHandler = eventHandler,
            navController = navController,
            scope = scope,
            snackbarHostState = snackbarHostState,
            viewModel = viewModel,
            applicationEvent = event,
        )
    }

    AppTheme(
        coil3 = koinInject<Coil3Provider>().coil3Integration,
    ) {
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
    eventHandler: EdifikanaMainScreenEventHandler,
    navController: NavHostController,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    viewModel: EdifikanaApplicationViewModel,
    applicationEvent: EdifikanaApplicationEvent,
) {
    when (val event = applicationEvent) {
        EdifikanaApplicationEvent.Noop -> Unit
        is EdifikanaApplicationEvent.OpenCamera -> {
            eventHandler.openCamera(event)
        }
        is EdifikanaApplicationEvent.OpenImageExternally -> {
            eventHandler.openImageExternally(event)
        }
        is EdifikanaApplicationEvent.OpenPhotoPicker -> {
            eventHandler.openPhotoPicker(event)
        }
        is EdifikanaApplicationEvent.ShareContent -> {
            eventHandler.shareContent(event)
        }
        is EdifikanaApplicationEvent.NavigateToActivity -> {
            if (event.clearStack) {
                while (navController.currentBackStack.value.isNotEmpty()) {
                    navController.popBackStack()
                }
            } else if (event.clearTop) {
                navController.popBackStack()
            }
            navController.navigate(event.destination.path)
        }
        is EdifikanaApplicationEvent.NavigateToScreem -> {
            navController.navigate(event.destination.rawRoute)
        }
        is EdifikanaApplicationEvent.NavigateBack -> {
            navController.popBackStack()
        }
        is EdifikanaApplicationEvent.CloseActivity -> {
            val currentActivity = navController.currentBackStack.value.reversed().find {
                ApplicationRoute.fromRoute(it.destination.route) != null
            }
            currentActivity?.destination?.route?.let {
                navController.popBackStack(it, inclusive = true)
            }
        }
        is EdifikanaApplicationEvent.ShowSnackbar -> {
            scope.launch {
                handleSnackbarEvent(
                    snackbarHostState = snackbarHostState,
                    event = event,
                ) { result ->
                    viewModel.handleSnackbarResult(result)
                }
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
    event: EdifikanaApplicationEvent.ShowSnackbar,
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
        startDestination = ApplicationRoute.Splash.route,
    ) {
        ApplicationRoute.entries.forEach { route ->
            when (route) {
                ApplicationRoute.Splash -> composable(route.route) {
                    SplashActivityScreen()
                }
                ApplicationRoute.Auth -> authActivityNavigation(route.route)
                ApplicationRoute.Main -> mainActivityNavigation(route.route)
                ApplicationRoute.Account -> accountActivityNavigation(route.route)
                ApplicationRoute.Admin -> adminActivityNavigation(route.route)
                ApplicationRoute.Debug -> debugActivityNavigation(route.route)
            }
        }
    }
}

/**
 * Edifikana main screen event handler.
 */
@Composable
expect fun ComposableKoinContext(content: @Composable () -> Unit)
