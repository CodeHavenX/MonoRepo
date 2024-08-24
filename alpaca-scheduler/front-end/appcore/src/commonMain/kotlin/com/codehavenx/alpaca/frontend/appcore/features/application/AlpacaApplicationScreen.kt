package com.codehavenx.alpaca.frontend.appcore.features.application

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.codehavenx.alpaca.frontend.appcore.features.main.MainMenuScreen
import com.codehavenx.alpaca.frontend.appcore.ui.theme.AlpacaTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import shared_compose.Res
import shared_compose.string_back_navigation

/**
 * Alpaca application main entry point.
 *
 * @param viewModel The application view model.
 * @param eventHandler The platform event handler.
 */
@Composable
fun AlpacaApplicationScreen(
    viewModel: ApplicationViewModel = koinInject(),
    @Suppress("UnusedParameter")
    eventHandler: PlatformEventHandler,
) {
    val event by viewModel.events.collectAsState(ApplicationEvent.Noop)
    val delegatedEvent by viewModel.delegatedEvents.collectAsState(ApplicationDelegatedEvent.Noop)
    val navController = rememberNavController()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        // Do something on create
    }

    LaunchedEffect(event) {
        when (val applicationEvent = event) {
            ApplicationEvent.Noop -> Unit
            is ApplicationEvent.Navigate -> {
                navController.navigate(applicationEvent.route) {
                    launchSingleTop = true
                }
            }
            is ApplicationEvent.NavigateBack -> {
                navController.popBackStack()
            }
            is ApplicationEvent.NavigateFromRootPage -> {
                navController.navigate(applicationEvent.route) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(navController.graph.findStartDestination().route!!) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            }
        }
    }
    ComposableKoinContext {
        AlpacaTheme {
            NavigationHost(
                navController = navController,
                delegatedEvent = delegatedEvent,
                onApplicationEventInvoke = { viewModel.executeApplicationEvent(it) },
            )
        }
    }
}

@Composable
internal expect fun ComposableKoinContext(content: @Composable () -> Unit)

@OptIn(ExperimentalMaterial3Api::class, RouteUnsafePath::class)
@Composable
private fun NavigationHost(
    navController: NavHostController,
    delegatedEvent: ApplicationDelegatedEvent,
    onApplicationEventInvoke: (ApplicationEvent) -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val backStack by navController.currentBackStack.collectAsState()

    @Suppress("UnusedPrivateProperty")
    val currentDestination = navBackStackEntry?.destination
    val startDestination = Route.mainMenu()
    var title by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedContent(title) {
                        Text(it)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                navigationIcon = {
                    AnimatedVisibility(
                        visible = (backStack.size > 2),
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.string_back_navigation)
                            )
                        }
                    }
                },
            )
        },
        bottomBar = { },
    ) { innerPadding ->
        NavigationRoutes(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            activityDelegatedEvent = delegatedEvent,
            onApplicationEventInvoke = onApplicationEventInvoke,
        )
    }
}

@OptIn(RouteUnsafePath::class)
@Composable
private fun NavigationRoutes(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    activityDelegatedEvent: ApplicationDelegatedEvent,
    onApplicationEventInvoke: (ApplicationEvent) -> Unit,
) {
    NavHost(
        navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Route.MAIN_MENU.route) { _ ->
            MainMenuScreen(
                activityDelegatedEvent,
                onApplicationEventInvoke,
            )
        }
    }
}
