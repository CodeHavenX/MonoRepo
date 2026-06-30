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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cramsan.architecture.client.features.debugsettings.debugSettingsNavGraph
import com.cramsan.architecture.client.navigation.BrowserNavigator
import com.cramsan.edifikana.client.lib.features.account.accountNavGraph
import com.cramsan.edifikana.client.lib.features.application.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.auth.authNavGraphNavigation
import com.cramsan.edifikana.client.lib.features.debug.debugNavGraphNavigation
import com.cramsan.edifikana.client.lib.features.home.homeNavGraphNavigation
import com.cramsan.edifikana.client.lib.features.settings.settingsNavGraphNavigation
import com.cramsan.edifikana.client.lib.features.splash.SplashScreen
import com.cramsan.edifikana.client.lib.navigation.EdifikanaPathNavigation
import com.cramsan.edifikana.client.lib.navigation.EmployeeIdNavType
import com.cramsan.edifikana.client.lib.navigation.EventLogEntryIdNavType
import com.cramsan.edifikana.client.lib.navigation.OrganizationIdNavType
import com.cramsan.edifikana.client.lib.navigation.PropertyIdNavType
import com.cramsan.edifikana.client.lib.navigation.TimeCardEventIdNavType
import com.cramsan.edifikana.client.lib.navigation.UserIdNavType
import com.cramsan.edifikana.client.lib.ui.di.Coil3Provider
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.eventLog.EventLogEntryId
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.timeCard.TimeCardEventId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.core.compose.navigation.Destination
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.themetoggle.SelectedTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.reflect.typeOf

/**
 * Edifikana window screen.
 */
@Composable
fun EdifikanaWindowScreen(
    eventHandler: EdifikanaMainScreenEventHandler,
    viewModel: EdifikanaWindowViewModel,
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
    val browserNavigator = remember { BrowserNavigator() }

    // Resolve the initial deep-link destination once at composition time. This is passed to
    // SplashScreen so that enforceAuth() can navigate directly there after a successful
    // auth check, avoiding the race where Splash's navigation overwrites the target page.
    val initialDestination =
        remember {
            browserNavigator.getInitialPath()?.let { EdifikanaPathNavigation.pathToDestination(it) }
        }

    val snackbarHostState = remember { SnackbarHostState() }

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
            EdifikanaPathNavigation::entryToPath,
        ) { path ->
            EdifikanaPathNavigation.pathToDestination(path)?.let { destination ->
                navigate { navController.navigate(destination) }
            }
        }
    }

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
                    navigate = navigate,
                    windowEvent = event.event,
                )
            }
        }
    }

    val darkTheme =
        when (applicationUIState.theme) {
            SelectedTheme.LIGHT -> false
            SelectedTheme.DARK -> true
            SelectedTheme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
        }
    AppTheme(
        coil3 = koinInject<Coil3Provider>().coil3Integration,
        darkTheme = darkTheme,
    ) {
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
    eventHandler: EdifikanaMainScreenEventHandler,
    navController: NavHostController,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    viewModel: EdifikanaWindowViewModel,
    navigate: (() -> Unit) -> Unit,
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
            navigate {
                handleNavigationEvent(
                    navController = navController,
                    event = event,
                )
                navController.navigate(event.destination)
            }
        }

        is EdifikanaWindowsEvent.NavigateToScreen -> {
            navigate {
                handleNavigationEvent(
                    navController = navController,
                    event = event,
                )
                navController.navigate(event.destination)
            }
        }

        is EdifikanaWindowsEvent.NavigateBack -> {
            navigate { navController.popBackStack() }
        }

        is EdifikanaWindowsEvent.NavigateBackWithResult -> {
            navigate {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(event.resultKey, event.resultValue)
                navController.popBackStack()
            }
        }

        is EdifikanaWindowsEvent.CloseNavGraph -> {
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
    startDestination: EdifikanaNavGraphDestination,
    initialDestination: Destination? = null,
) {
    val typeMap =
        remember {
            mapOf(
                typeOf<EventLogEntryId>() to EventLogEntryIdNavType(),
                typeOf<PropertyId>() to PropertyIdNavType(),
                typeOf<TimeCardEventId>() to TimeCardEventIdNavType(),
                typeOf<UserId>() to UserIdNavType(),
                typeOf<EmployeeId>() to EmployeeIdNavType(),
                typeOf<OrganizationId>() to OrganizationIdNavType(),
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
            SplashScreen(initialDestination = initialDestination)
        }
        authNavGraphNavigation(typeMap)
        accountNavGraph(typeMap)
        debugNavGraphNavigation(typeMap)
        debugSettingsNavGraph(
            graphDestination = EdifikanaNavGraphDestination.DebugSettingsNavGraphDestination::class,
            onBack = { navHostController.popBackStack() },
        )
        homeNavGraphNavigation(typeMap)
        settingsNavGraphNavigation(typeMap)
    }
}

private const val TRANSITION_ANIMATION_DURATION_MS = 400

/**
 * Entry point for the application. This will configure the koin context.
 */
@Composable
expect fun ComposableKoinContext(content: @Composable () -> Unit)
