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
import com.cramsan.architecture.client.navigation.BrowserNavigator
import com.cramsan.framework.core.compose.navigation.Destination
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.templatereplaceme.client.lib.app.TemplateReplaceMeApplicationMainScreenEventHandler
import com.cramsan.templatereplaceme.client.lib.features.splash.splashNavGraphNavigation
import com.cramsan.templatereplaceme.client.lib.navigation.TemplateReplaceMePathNavigation
import com.cramsan.templatereplaceme.client.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * TemplateReplaceMe window screen.
 */
@Composable
fun TemplateReplaceMeWindowScreen(
    eventHandler: TemplateReplaceMeApplicationMainScreenEventHandler,
    viewModel: TemplateReplaceMeWindowViewModel = koinViewModel(),
    startDestination: TemplateReplaceMeWindowNavGraphDestination =
        TemplateReplaceMeWindowNavGraphDestination.SplashNavGraphDestination,
) {
    WindowsContent(
        eventHandler = eventHandler,
        viewModel = viewModel,
        startDestination = startDestination,
    )
}

@Composable
private fun WindowsContent(
    startDestination: TemplateReplaceMeWindowNavGraphDestination,
    viewModel: TemplateReplaceMeWindowViewModel,
    eventHandler: TemplateReplaceMeApplicationMainScreenEventHandler,
) {
    val navController = rememberNavController()
    val browserNavigator = remember { BrowserNavigator() }

    // Resolve the initial deep-link destination once at composition time so SplashScreen
    // can navigate there after the main graph loads, avoiding the race where Splash's
    // clearStack navigation overwrites the target.
    val initialDestination: Destination? = remember {
        browserNavigator.getInitialPath()?.let { TemplateReplaceMePathNavigation.pathToDestination(it) }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    // Guards against calling navController.navigate() before NavHost calls setGraph().
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

    LaunchedEffect(Unit) {
        browserNavigator.attach(
            navController,
            TemplateReplaceMePathNavigation::entryToPath,
        ) { path ->
            TemplateReplaceMePathNavigation.pathToDestination(path)?.let { destination ->
                navigate { navController.navigate(destination) }
            }
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

    AppTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) {
            WindowNavigationHost(
                navHostController = navController,
                startDestination = startDestination,
                initialDestination = initialDestination,
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
    initialDestination: Destination? = null,
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
        splashNavGraphNavigation(typeMap, initialDestination)
    }
}

private const val TRANSITION_ANIMATION_DURATION_MS = 400

/**
 * Entry point for the application. This will configure the koin context.
 */
@Composable
expect fun ComposableKoinContext(content: @Composable () -> Unit)
