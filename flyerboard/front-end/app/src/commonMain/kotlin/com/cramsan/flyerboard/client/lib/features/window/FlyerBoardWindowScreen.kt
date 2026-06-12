package com.cramsan.flyerboard.client.lib.features.window

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cramsan.architecture.client.features.debugsettings.debugSettingsNavGraph
import com.cramsan.architecture.client.navigation.BrowserNavigator
import com.cramsan.flyerboard.client.lib.features.application.FlyerBoardApplicationMainScreenEventHandler
import com.cramsan.flyerboard.client.lib.features.auth.authNavGraphNavigation
import com.cramsan.flyerboard.client.lib.features.main.MainDestination
import com.cramsan.flyerboard.client.lib.features.main.mainNavGraphNavigation
import com.cramsan.flyerboard.client.lib.features.splash.SplashScreen
import com.cramsan.flyerboard.client.lib.navigation.flyerBoardEntryToPath
import com.cramsan.flyerboard.client.lib.navigation.pathToDestination
import com.cramsan.flyerboard.client.ui.components.FlyerBoardFooter
import com.cramsan.flyerboard.client.ui.components.FlyerBoardMainTopBar
import com.cramsan.flyerboard.client.ui.components.FlyerBoardTopBarTab
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.framework.core.compose.navigation.Destination
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import flyerboard_lib.Res
import flyerboard_lib.nav_archive
import flyerboard_lib.nav_browse
import flyerboard_lib.nav_moderation
import flyerboard_lib.nav_my_flyers
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * FlyerBoard window screen.
 */
@Composable
fun FlyerBoardWindowScreen(
    eventHandler: FlyerBoardApplicationMainScreenEventHandler,
    viewModel: FlyerBoardWindowViewModel = koinViewModel(),
    startDestination: FlyerBoardWindowNavGraphDestination =
        FlyerBoardWindowNavGraphDestination.SplashNavGraphDestination,
) {
    val navController = rememberNavController()
    val browserNavigator = remember { BrowserNavigator() }

    // Resolve the initial deep-link destination once at composition time so that SplashScreen
    // can navigate directly there after the main graph loads, avoiding the race where Splash's
    // clearStack navigation overwrites the target.
    val initialDestination: Destination? =
        remember {
            browserNavigator.getInitialPath()?.let { pathToDestination(it) }
        }

    LaunchedEffect(Unit) {
        browserNavigator.attach(
            navController,
            ::flyerBoardEntryToPath,
        ) { path ->
            navController.navigate(pathToDestination(path))
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isSplashDestination =
        currentDestination?.hasRoute(FlyerBoardWindowNavGraphDestination.SplashNavGraphDestination::class)
            ?: true

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            is FlyerBoardWindowViewModelEvent.FlyerBoardWindowEventWrapper -> {
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

    val onSignIn = {
        navController.navigate(FlyerBoardWindowNavGraphDestination.AuthNavGraphDestination)
    }
    val onSignOut = { viewModel.signOut() }

    val isSignedIn = uiState.authState is AuthState.Authenticated
    val isAdmin = (uiState.authState as? AuthState.Authenticated)?.isAdmin == true

    val tabs =
        buildList {
            add(
                FlyerBoardTopBarTab(
                    label = stringResource(Res.string.nav_browse),
                    selected = currentDestination?.hasRoute(MainDestination.FlyerListDestination::class) == true,
                    onClick = {
                        navController.navigate(MainDestination.FlyerListDestination) {
                            launchSingleTop = true
                        }
                    },
                ),
            )
            if (isSignedIn) {
                add(
                    FlyerBoardTopBarTab(
                        label = stringResource(Res.string.nav_my_flyers),
                        selected = currentDestination?.hasRoute(MainDestination.MyFlyersDestination::class) == true,
                        onClick = {
                            if (uiState.authState != AuthState.Unauthenticated) {
                                navController.navigate(MainDestination.MyFlyersDestination) {
                                    launchSingleTop = true
                                }
                            } else {
                                onSignIn()
                            }
                        },
                    ),
                )
            }
            add(
                FlyerBoardTopBarTab(
                    label = stringResource(Res.string.nav_archive),
                    selected = currentDestination?.hasRoute(MainDestination.ArchiveDestination::class) == true,
                    onClick = {
                        navController.navigate(MainDestination.ArchiveDestination) {
                            launchSingleTop = true
                        }
                    },
                ),
            )
            if (isAdmin) {
                add(
                    FlyerBoardTopBarTab(
                        label = stringResource(Res.string.nav_moderation),
                        selected =
                        currentDestination?.hasRoute(
                            MainDestination.ModerationQueueDestination::class,
                        ) == true,
                        onClick = {
                            navController.navigate(MainDestination.ModerationQueueDestination) {
                                launchSingleTop = true
                            }
                        },
                    ),
                )
            }
        }

    WindowsContent(
        navController = navController,
        snackbarHostState = snackbarHostState,
        startDestination = startDestination,
        initialDestination = initialDestination,
        showTopBar = !isSplashDestination,
        authState = uiState.authState,
        tabs = tabs,
        onSignIn = onSignIn,
        onSignOut = onSignOut,
        onCloseIconClicked = { viewModel.close() }
    )
}

@Composable
private fun WindowsContent(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    startDestination: FlyerBoardWindowNavGraphDestination,
    initialDestination: Destination?,
    showTopBar: Boolean,
    authState: AuthState,
    tabs: List<FlyerBoardTopBarTab>,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onCloseIconClicked: () -> Unit,
) {
    AppTheme {
        FlyerBoardWindowChrome(
            showTopBar = showTopBar,
            authState = authState,
            tabs = tabs,
            onSignIn = onSignIn,
            onSignOut = onSignOut,
            snackbarHostState = snackbarHostState,
        ) { paddingValues ->
            WindowNavigationHost(
                modifier = Modifier.padding(paddingValues),
                navHostController = navController,
                startDestination = startDestination,
                authState = authState,
                initialDestination = initialDestination,
            )
        }
    }
}

/**
 * Scaffold chrome shared by all FlyerBoard window destinations: the main top bar (hidden on
 * [showTopBar] == false), the footer, and the snackbar host.
 */
@Composable
internal fun FlyerBoardWindowChrome(
    showTopBar: Boolean,
    authState: AuthState,
    tabs: List<FlyerBoardTopBarTab>,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    snackbarHostState: SnackbarHostState,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            if (showTopBar) {
                FlyerBoardMainTopBar(
                    tabs = tabs,
                    isAuthenticated = authState is AuthState.Authenticated,
                    onSignIn = onSignIn,
                    onSignOut = onSignOut,
                )
            }
        },
        bottomBar = {
            FlyerBoardFooter()
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { paddingValues ->
        content(paddingValues)
    }
}

private fun handleWindowEvent(
    eventHandler: FlyerBoardApplicationMainScreenEventHandler,
    navController: NavHostController,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    viewModel: FlyerBoardWindowViewModel,
    windowEvent: FlyerBoardWindowsEvent,
) {
    when (val event = windowEvent) {
        is FlyerBoardWindowsEvent.ShareContent -> {
            eventHandler.shareContent(event)
        }

        is FlyerBoardWindowsEvent.NavigateToNavGraph -> {
            handleNavigationEvent(
                navController = navController,
                event = event,
            )
            navController.navigate(event.destination)
        }

        is FlyerBoardWindowsEvent.NavigateToScreen -> {
            handleNavigationEvent(
                navController = navController,
                event = event,
            )
            navController.navigate(event.destination)
        }

        is FlyerBoardWindowsEvent.NavigateBack -> {
            navController.popBackStack()
        }

        is FlyerBoardWindowsEvent.CloseNavGraph -> {
            val currentNavGraph =
                navController.currentBackStack.value.reversed().find {
                    it.destination.navigatorName == "navigation"
                }
            currentNavGraph?.destination?.route?.let {
                navController.popBackStack(it, inclusive = true)
            }
        }

        is FlyerBoardWindowsEvent.ShowSnackbar -> {
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
    event: FlyerBoardWindowsEvent.ShowSnackbar,
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
    startDestination: FlyerBoardWindowNavGraphDestination,
    authState: AuthState,
    modifier: Modifier = Modifier,
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
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(TRANSITION_ANIMATION_DURATION_MS)) },
        exitTransition = { fadeOut(animationSpec = tween(TRANSITION_ANIMATION_DURATION_MS)) },
    ) {
        composable(FlyerBoardWindowNavGraphDestination.SplashNavGraphDestination::class) {
            SplashScreen(initialDestination = initialDestination)
        }
        authNavGraphNavigation(typeMap)
        mainNavGraphNavigation(
            typeMap = typeMap,
            authState = authState,
        )
        debugSettingsNavGraph(
            graphDestination = FlyerBoardWindowNavGraphDestination.DebugSettingsNavGraphDestination::class,
            onBack = { navHostController.popBackStack() },
        )
    }
}

private const val TRANSITION_ANIMATION_DURATION_MS = 400

/**
 * Entry point for the application. This will configure the koin context.
 */
@Composable
expect fun ComposableKoinContext(content: @Composable () -> Unit)
