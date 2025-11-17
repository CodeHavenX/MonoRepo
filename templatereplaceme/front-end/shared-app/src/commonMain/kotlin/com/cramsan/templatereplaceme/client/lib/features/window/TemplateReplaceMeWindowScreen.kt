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
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.templatereplaceme.client.lib.features.application.TemplateReplaceMeApplicationMainScreenEventHandler
import com.cramsan.templatereplaceme.client.lib.features.main.mainNavGraphNavigation
import com.cramsan.templatereplaceme.client.lib.features.splash.SplashScreen
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

    val snackbarHostState = remember { SnackbarHostState() }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            is TemplateReplaceMeWindowViewModelEvent.TemplateReplaceMeWindowEventWrapper -> {
                handleWindowEvent(
                    eventHandler = eventHandler,
                    navController = navController,
                    scope = this,
                    snackbarHostState = snackbarHostState,
                    viewModel = viewModel,
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
    windowEvent: TemplateReplaceMeWindowsEvent,
) {
    when (val event = windowEvent) {
        is TemplateReplaceMeWindowsEvent.ShareContent -> {
            eventHandler.shareContent(event)
        }
        is TemplateReplaceMeWindowsEvent.NavigateToNavGraph -> {
            handleNavigationEvent(
                navController = navController,
                event = event,
            )
            navController.navigate(event.destination)
        }
        is TemplateReplaceMeWindowsEvent.NavigateToScreen -> {
            handleNavigationEvent(
                navController = navController,
                event = event,
            )
            navController.navigate(event.destination)
        }
        is TemplateReplaceMeWindowsEvent.NavigateBack -> {
            navController.popBackStack()
        }
        is TemplateReplaceMeWindowsEvent.CloseNavGraph -> {
            val currentNavGraph = navController.currentBackStack.value.reversed().find {
                it.destination.navigatorName == "navigation"
            }
            currentNavGraph?.destination?.route?.let {
                navController.popBackStack(it, inclusive = true)
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
    val result = snackbarHostState
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
    val typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = remember {
        mapOf()
    }
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        typeMap = typeMap,
        enterTransition = { fadeIn(animationSpec = tween(TRANSITION_ANIMATION_DURATION_MS)) },
        exitTransition = { fadeOut(animationSpec = tween(TRANSITION_ANIMATION_DURATION_MS)) },
    ) {
        composable(TemplateReplaceMeWindowNavGraphDestination.SplashNavGraphDestination::class) {
            SplashScreen()
        }
        mainNavGraphNavigation(typeMap)
    }
}

private const val TRANSITION_ANIMATION_DURATION_MS = 400

/**
 * Entry point for the application. This will configure the koin context.
 */
@Composable
expect fun ComposableKoinContext(content: @Composable () -> Unit)
