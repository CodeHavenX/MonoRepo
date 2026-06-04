package com.cramsan.templatereplaceme.client.lib.features.window

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.templatereplaceme.client.lib.app.TemplateReplaceMeApplicationMainScreenEventHandler
import com.cramsan.templatereplaceme.client.lib.features.splash.splashNavGraphNavigation
import com.cramsan.templatereplaceme.client.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * TemplateReplaceMe window screen.
 *
 * @param initialDeepLink Optional URL or hash string received at startup (e.g. from
 * [kotlinx.browser.window.location.hash] on WASM). Passed into [WindowsContent] so it is
 * processed after the event observer is collecting, avoiding the lost-event race with
 * [MutableSharedFlow].
 */
@Composable
fun TemplateReplaceMeWindowScreen(
    eventHandler: TemplateReplaceMeApplicationMainScreenEventHandler,
    viewModel: TemplateReplaceMeWindowViewModel = koinViewModel(),
    startDestination: TemplateReplaceMeWindowNavGraphDestination =
        TemplateReplaceMeWindowNavGraphDestination.SplashNavGraphDestination,
    initialDeepLink: String? = null,
) {
    WindowsContent(
        eventHandler = eventHandler,
        viewModel = viewModel,
        startDestination = startDestination,
        initialDeepLink = initialDeepLink,
    )
}

@Composable
private fun WindowsContent(
    startDestination: TemplateReplaceMeWindowNavGraphDestination,
    viewModel: TemplateReplaceMeWindowViewModel,
    eventHandler: TemplateReplaceMeApplicationMainScreenEventHandler,
    initialDeepLink: String? = null,
) {
    val navController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }

    // Guards against calling navController.navigate() before NavHost calls setGraph().
    // NavHost's internal LaunchedEffect sets the graph during composition, but
    // LaunchedEffect(initialDeepLink) below can fire first because it is higher in the tree.
    // Any navigation action that arrives before the graph is ready is stored here and
    // drained once currentBackStackEntry becomes non-null (i.e. the graph is set).
    var pendingNavAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    LaunchedEffect(navController.currentBackStackEntry, pendingNavAction) {
        if (navController.currentBackStackEntry != null && pendingNavAction != null) {
            pendingNavAction?.invoke()
            pendingNavAction = null
        }
    }

    val navigate: (() -> Unit) -> Unit = { action ->
        if (navController.currentBackStackEntry != null) {
            action()
        } else {
            pendingNavAction = action
        }
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            is TemplateReplaceMeWindowViewModelEvent.TemplateReplaceMeWindowEventWrapper -> {
                handleWindowEvent(
                    eventHandler = eventHandler,
                    navController = navController,
                    scope = this,
                    snackbarHostState = snackbarHostState,
                    viewModel = viewModel,
                    navigate = navigate,
                    windowEvent = event.event,
                )
            }
        }
    }

    // Intentionally placed after ObserveViewModelEvents. Compose launches effects in
    // composition order, so the event collector above is guaranteed to reach its collect
    // suspend point before this effect calls handleDeepLink and emits a navigation event.
    LaunchedEffect(initialDeepLink) {
        if (initialDeepLink != null) {
            viewModel.handleDeepLink(initialDeepLink)
        }
    }

    AppTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) {
            WindowNavigationHost(
                navHostController = navController,
                startDestination = startDestination,
            )
        }
    }
}

private fun handleWindowEvent(
    eventHandler: TemplateReplaceMeApplicationMainScreenEventHandler,
    navController: NavHostController,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    viewModel: TemplateReplaceMeWindowViewModel,
    navigate: (() -> Unit) -> Unit,
    windowEvent: TemplateReplaceMeWindowsEvent,
) {
    when (val event = windowEvent) {
        is TemplateReplaceMeWindowsEvent.ShareContent -> {
            eventHandler.shareContent(event)
        }

        is TemplateReplaceMeWindowsEvent.NavigateToNavGraph -> {
            navigate {
                handleNavigationEvent(
                    navController = navController,
                    event = event,
                )
                navController.navigate(event.destination)
            }
        }

        is TemplateReplaceMeWindowsEvent.NavigateToScreen -> {
            navigate {
                handleNavigationEvent(
                    navController = navController,
                    event = event,
                )
                navController.navigate(event.destination)
            }
        }

        is TemplateReplaceMeWindowsEvent.NavigateBack -> {
            navigate { navController.popBackStack() }
        }

        is TemplateReplaceMeWindowsEvent.CloseNavGraph -> {
            navigate {
                val currentNavGraph =
                    navController.currentBackStack.value.reversed().find {
                        it.destination.navigatorName == "navigation"
                    }
                currentNavGraph?.destination?.route?.let {
                    navController.popBackStack(it, inclusive = true)
                }
            }
        }

        is TemplateReplaceMeWindowsEvent.ShowSnackbar -> {
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

private fun handleNavigationEvent(
    navController: NavHostController,
    event: NavigationEvent,
) {
    if (event.clearStack) {
        while (navController.currentBackStack.value.isNotEmpty()) {
            navController.popBackStack()
        }
    } else if (event.clearTop) {
        navController.popBackStack()
    }
}

/**
 * Handle the [event] and use it to display a snackbar from the [snackbarHostState].
 * The [onResult] callback is called with the result of the snackbar.
 */
private suspend fun handleSnackbarEvent(
    snackbarHostState: SnackbarHostState,
    event: TemplateReplaceMeWindowsEvent.ShowSnackbar,
    onResult: (SnackbarResult) -> Unit,
) {
    snackbarHostState.currentSnackbarData?.dismiss()
    val result =
        snackbarHostState
            .showSnackbar(
                message = event.message,
                duration = SnackbarDuration.Short,
            )
    onResult(result)
}

@Composable
private fun WindowNavigationHost(
    navHostController: NavHostController,
    startDestination: TemplateReplaceMeWindowNavGraphDestination,
) {
    val typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> =
        remember {
            mapOf()
        }
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        typeMap = typeMap,
        enterTransition = { fadeIn(animationSpec = tween(TRANSITION_ANIMATION_DURATION_MS)) },
        exitTransition = { fadeOut(animationSpec = tween(TRANSITION_ANIMATION_DURATION_MS)) },
    ) {
        splashNavGraphNavigation(typeMap)
    }
}

private const val TRANSITION_ANIMATION_DURATION_MS = 400

/**
 * Entry point for the application. This will configure the koin context.
 */
@Composable
expect fun ComposableKoinContext(content: @Composable () -> Unit)
