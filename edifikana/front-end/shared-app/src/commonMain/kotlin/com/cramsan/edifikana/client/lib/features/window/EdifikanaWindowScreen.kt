package com.cramsan.edifikana.client.lib.features.window

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cramsan.edifikana.client.lib.features.account.accountNavGraph
import com.cramsan.edifikana.client.lib.features.application.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.auth.authNavGraphNavigation
import com.cramsan.edifikana.client.lib.features.debug.debugNavGraphNavigation
import com.cramsan.edifikana.client.lib.features.home.homeNavGraphNavigation
import com.cramsan.edifikana.client.lib.features.splash.SplashScreen
import com.cramsan.edifikana.client.lib.navigation.EmployeeIdNavType
import com.cramsan.edifikana.client.lib.navigation.EventLogEntryIdNavType
import com.cramsan.edifikana.client.lib.navigation.PropertyIdNavType
import com.cramsan.edifikana.client.lib.navigation.TimeCardEventIdNavType
import com.cramsan.edifikana.client.lib.navigation.UserIdNavType
import com.cramsan.edifikana.client.lib.ui.di.Coil3Provider
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.themetoggle.SelectedTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.typeOf

/**
 * Edifikana window screen.
 */
@Composable
fun EdifikanaWindowScreen(
    eventHandler: EdifikanaMainScreenEventHandler,
    viewModel: EdifikanaWindowViewModel = koinViewModel(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
    startDestination: EdifikanaNavGraphDestination = EdifikanaNavGraphDestination.SplashNavGraphDestination,
) {
    WindowsContent(
        eventHandler = eventHandler,
        viewModel = viewModel,
        applicationViewModel = applicationViewModel,
        startDestination = startDestination,
    )
}

@Composable
private fun WindowsContent(
    startDestination: EdifikanaNavGraphDestination,
    viewModel: EdifikanaWindowViewModel,
    applicationViewModel: EdifikanaApplicationViewModel,
    eventHandler: EdifikanaMainScreenEventHandler,
) {
    val navController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }

    val applicationUIState by applicationViewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize()
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            is EdifikanaWindowViewModelEvent.EdifikanaWindowEventWrapper -> {
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

    val darkTheme = when (applicationUIState.theme) {
        SelectedTheme.LIGHT -> false
        SelectedTheme.DARK -> true
        SelectedTheme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }
    AppTheme(
        coil3 = koinInject<Coil3Provider>().coil3Integration,
        darkTheme = darkTheme
    ) {
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
    eventHandler: EdifikanaMainScreenEventHandler,
    navController: NavHostController,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    viewModel: EdifikanaWindowViewModel,
    windowEvent: EdifikanaWindowsEvent,
) {
    when (val event = windowEvent) {
        is EdifikanaWindowsEvent.OpenCamera -> {
            eventHandler.openCamera(event)
        }
        is EdifikanaWindowsEvent.OpenImageExternally -> {
            eventHandler.openImageExternally(event)
        }
        is EdifikanaWindowsEvent.OpenPhotoPicker -> {
            eventHandler.openPhotoPicker(event)
        }
        is EdifikanaWindowsEvent.ShareContent -> {
            eventHandler.shareContent(event)
        }
        is EdifikanaWindowsEvent.NavigateToNavGraph -> {
            handleNavigationEvent(
                navController = navController,
                event = event,
            )
            navController.navigate(event.destination)
        }
        is EdifikanaWindowsEvent.NavigateToScreen -> {
            handleNavigationEvent(
                navController = navController,
                event = event,
            )
            navController.navigate(event.destination)
        }
        is EdifikanaWindowsEvent.NavigateBack -> {
            navController.popBackStack()
        }
        is EdifikanaWindowsEvent.CloseNavGraph -> {
            val currentNavGraph = navController.currentBackStack.value.reversed().find {
                it.destination.navigatorName == "navigation"
            }
            currentNavGraph?.destination?.route?.let {
                navController.popBackStack(it, inclusive = true)
            }
        }
        is EdifikanaWindowsEvent.ShowSnackbar -> {
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
    event: EdifikanaWindowsEvent.ShowSnackbar,
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
    startDestination: EdifikanaNavGraphDestination,
) {
    val typeMap = remember {
        mapOf(
            typeOf<EventLogEntryId>() to EventLogEntryIdNavType(),
            typeOf<PropertyId>() to PropertyIdNavType(),
            typeOf<TimeCardEventId>() to TimeCardEventIdNavType(),
            typeOf<UserId>() to UserIdNavType(),
            typeOf<EmployeeId>() to EmployeeIdNavType(),
        )
    }
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        typeMap = typeMap,
        enterTransition = { fadeIn(animationSpec = tween(TRANSITION_ANIMATION_DURATION_MS)) },
        exitTransition = { fadeOut(animationSpec = tween(TRANSITION_ANIMATION_DURATION_MS)) },
    ) {
        composable(EdifikanaNavGraphDestination.SplashNavGraphDestination::class) {
            SplashScreen()
        }
        authNavGraphNavigation(typeMap)
        accountNavGraph(typeMap)
        debugNavGraphNavigation(typeMap)
        homeNavGraphNavigation(typeMap)
    }
}

private const val TRANSITION_ANIMATION_DURATION_MS = 400

/**
 * Entry point for the application. This will configure the koin context.
 */
@Composable
expect fun ComposableKoinContext(content: @Composable () -> Unit)
