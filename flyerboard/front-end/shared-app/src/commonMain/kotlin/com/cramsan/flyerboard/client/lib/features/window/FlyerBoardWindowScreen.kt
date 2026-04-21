package com.cramsan.flyerboard.client.lib.features.window

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.flyerboard.client.lib.features.application.FlyerBoardApplicationMainScreenEventHandler
import com.cramsan.flyerboard.client.lib.features.auth.authNavGraphNavigation
import com.cramsan.flyerboard.client.lib.features.main.MainDestination
import com.cramsan.flyerboard.client.lib.features.main.mainNavGraphNavigation
import com.cramsan.flyerboard.client.lib.features.splash.SplashScreen
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import flyerboard_lib.Res
import flyerboard_lib.nav_archive
import flyerboard_lib.nav_browse
import flyerboard_lib.nav_moderation
import flyerboard_lib.nav_my_flyers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

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
    WindowsContent(
        eventHandler = eventHandler,
        viewModel = viewModel,
        startDestination = startDestination,
    )
}

@Composable
private fun WindowsContent(
    startDestination: FlyerBoardWindowNavGraphDestination,
    viewModel: FlyerBoardWindowViewModel,
    eventHandler: FlyerBoardApplicationMainScreenEventHandler,
) {
    val navController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isTopLevelMainDestination = currentDestination?.let { dest ->
        dest.hasRoute(MainDestination.FlyerListDestination::class) ||
            dest.hasRoute(MainDestination.ArchiveDestination::class) ||
            dest.hasRoute(MainDestination.MyFlyersDestination::class) ||
            dest.hasRoute(MainDestination.ModerationQueueDestination::class)
    } ?: false

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

    AppTheme {
        Scaffold(
            bottomBar = {
                if (isTopLevelMainDestination) {
                    FlyerBoardBottomNavBar(
                        isAuthenticated = uiState.isAuthenticated,
                        currentDestination = currentDestination,
                        onBrowse = {
                            navController.navigate(MainDestination.FlyerListDestination) {
                                launchSingleTop = true
                            }
                        },
                        onArchive = {
                            navController.navigate(MainDestination.ArchiveDestination) {
                                launchSingleTop = true
                            }
                        },
                        onMyFlyers = {
                            if (uiState.isAuthenticated) {
                                navController.navigate(MainDestination.MyFlyersDestination) {
                                    launchSingleTop = true
                                }
                            } else {
                                navController.navigate(
                                    FlyerBoardWindowNavGraphDestination.AuthNavGraphDestination
                                )
                            }
                        },
                        onModeration = {
                            if (uiState.isAuthenticated) {
                                navController.navigate(MainDestination.ModerationQueueDestination) {
                                    launchSingleTop = true
                                }
                            } else {
                                navController.navigate(
                                    FlyerBoardWindowNavGraphDestination.AuthNavGraphDestination
                                )
                            }
                        },
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { paddingValues ->
            WindowNavigationHost(
                modifier = Modifier.padding(paddingValues),
                navHostController = navController,
                startDestination = startDestination,
                isAuthenticated = uiState.isAuthenticated,
                onSignIn = {
                    navController.navigate(FlyerBoardWindowNavGraphDestination.AuthNavGraphDestination)
                },
                onSignOut = { viewModel.signOut() },
            )
        }
    }
}

@Composable
private fun FlyerBoardBottomNavBar(
    isAuthenticated: Boolean,
    currentDestination: NavDestination?,
    onBrowse: () -> Unit,
    onArchive: () -> Unit,
    onMyFlyers: () -> Unit,
    onModeration: () -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentDestination?.hasRoute(MainDestination.FlyerListDestination::class) == true,
            onClick = onBrowse,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_browse)) },
        )
        NavigationBarItem(
            selected = currentDestination?.hasRoute(MainDestination.ArchiveDestination::class) == true,
            onClick = onArchive,
            icon = { Icon(Icons.Default.Archive, contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_archive)) },
        )
        NavigationBarItem(
            selected = currentDestination?.hasRoute(MainDestination.MyFlyersDestination::class) == true,
            onClick = onMyFlyers,
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_my_flyers)) },
        )
        if (isAuthenticated) {
            NavigationBarItem(
                selected = currentDestination?.hasRoute(
                    MainDestination.ModerationQueueDestination::class
                ) == true,
                onClick = onModeration,
                icon = { Icon(Icons.Default.Gavel, contentDescription = null) },
                label = { Text(stringResource(Res.string.nav_moderation)) },
            )
        }
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
            val currentNavGraph = navController.currentBackStack.value.reversed().find {
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
    startDestination: FlyerBoardWindowNavGraphDestination,
    isAuthenticated: Boolean,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = remember {
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
            SplashScreen()
        }
        authNavGraphNavigation(typeMap)
        mainNavGraphNavigation(
            typeMap = typeMap,
            isAuthenticated = isAuthenticated,
            onSignIn = onSignIn,
            onSignOut = onSignOut,
        )
    }
}

private const val TRANSITION_ANIMATION_DURATION_MS = 400

/**
 * Entry point for the application. This will configure the koin context.
 */
@Composable
expect fun ComposableKoinContext(content: @Composable () -> Unit)
