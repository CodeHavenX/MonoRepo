package com.codehavenx.alpaca.frontend.appcore.features.application

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codehavenx.alpaca.frontend.appcore.features.clients.addclient.AddClientScreen
import com.codehavenx.alpaca.frontend.appcore.features.clients.listclients.ListClientsScreen
import com.codehavenx.alpaca.frontend.appcore.features.clients.updateclient.UpdateClientScreen
import com.codehavenx.alpaca.frontend.appcore.features.clients.viewclient.ViewClientScreen
import com.codehavenx.alpaca.frontend.appcore.features.createaccount.CreateAccountScreen
import com.codehavenx.alpaca.frontend.appcore.features.home.HomeScreen
import com.codehavenx.alpaca.frontend.appcore.features.staff.addstaff.AddStaffScreen
import com.codehavenx.alpaca.frontend.appcore.features.staff.liststaff.ListStaffsScreen
import com.codehavenx.alpaca.frontend.appcore.features.staff.updatestaff.UpdateStaffScreen
import com.codehavenx.alpaca.frontend.appcore.features.staff.viewstaff.ViewStaffScreen
import com.codehavenx.alpaca.frontend.appcore.ui.components.NavigationBar
import com.codehavenx.alpaca.frontend.appcore.ui.components.TopBar
import com.codehavenx.alpaca.frontend.appcore.ui.theme.AlpacaTheme
import org.koin.compose.koinInject

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
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        // Do something on create
    }

    LaunchedEffect(Unit) {
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
            is ApplicationEvent.SignInStatusChange -> {
                viewModel.setSignInStatus(applicationEvent.isSignedIn)
            }
        }
    }
    ComposableKoinContext {
        AlpacaTheme {
            val state = uiState
            AnimatedContent(
                targetState = state,
                label = "animated content"
            ) {
                NavigationHost(
                    navController = navController,
                    delegatedEvent = delegatedEvent,
                    onApplicationEventInvoke = { viewModel.executeApplicationEvent(it) },
                    onSignOutClicked = { viewModel.signOut() },
                    navBar = state.navBar,
                )
            }
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
    navBar: List<NavBarSegment>,
    onSignOutClicked: () -> Unit,
) {
    val startDestination = Route.createAccount()
    var showNavigationBar by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopBar(
                navController,
                { showNavigationBar = !showNavigationBar },
                { onSignOutClicked() },
            )
        },
        bottomBar = { },
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            NavigationBar(navBar, navController, showNavigationBar)
            NavigationRoutes(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, MaterialTheme.colorScheme.primaryContainer),
                activityDelegatedEvent = delegatedEvent,
                onApplicationEventInvoke = onApplicationEventInvoke,
            )
        }
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
        composable(Route.HOME.route) { _ ->
            HomeScreen(
                activityDelegatedEvent,
                onApplicationEventInvoke,
            )
        }
        composable(Route.CLIENTS_LIST.route) { _ ->
            ListClientsScreen(
                activityDelegatedEvent,
                onApplicationEventInvoke,
            )
        }

        composable(Route.CLIENTS_ADD.route) { _ ->
            AddClientScreen(
                activityDelegatedEvent,
                onApplicationEventInvoke,
            )
        }

        composable(Route.CLIENTS_UPDATE.route) { backStackEntry ->
            UpdateClientScreen(
                backStackEntry.arguments?.getString("clientId")!!,
                activityDelegatedEvent,
                onApplicationEventInvoke,
            )
        }

        composable(Route.CLIENTS_VIEW.route) { backStackEntry ->
            ViewClientScreen(
                backStackEntry.arguments?.getString("clientId")!!,
                activityDelegatedEvent,
                onApplicationEventInvoke,
            )
        }

        composable(Route.STAFF_LIST.route) { _ ->
            ListStaffsScreen(
                activityDelegatedEvent,
                onApplicationEventInvoke,
            )
        }

        composable(Route.STAFF_UPDATE.route) { backStackEntry ->
            UpdateStaffScreen(
                backStackEntry.arguments?.getString("staffId")!!,
                activityDelegatedEvent,
                onApplicationEventInvoke,
            )
        }

        composable(Route.STAFF_VIEW.route) { backStackEntry ->
            ViewStaffScreen(
                backStackEntry.arguments?.getString("staffId")!!,
                activityDelegatedEvent,
                onApplicationEventInvoke,
            )
        }

        composable(Route.STAFF_ADD.route) { _ ->
            AddStaffScreen(
                activityDelegatedEvent,
                onApplicationEventInvoke,
            )
        }

        composable(Route.APPOINTMENTS.route) { _ ->
        }

        composable(Route.COURSES_AND_CLASSES.route) { _ ->
        }

        composable(Route.CREATE_ACCOUNT.route) { _ ->
            CreateAccountScreen(
                activityDelegatedEvent,
                onApplicationEventInvoke,
            )
        }
    }
}
