package ${PACKAGE_NAME}

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject

/**
 * ${NAME} activity screen.
 */
@OptIn(RouteSafePath::class)
@Composable
fun ${NAME}ActivityScreen(
    viewModel: ${NAME}ActivityViewModel = koinInject(),
    applicationViewModel: ApplicationViewModel = koinInject(),
) {
    val navController = rememberNavController()

    val viewModelEvent by viewModel.event.collectAsState(${NAME}ActivityEvent.Noop)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            ${NAME}ActivityEvent.Noop -> Unit
            is ${NAME}ActivityEvent.Navigate -> {
                navController.navigate(event.destination.route)
            }
            is ${NAME}ActivityEvent.CloseActivity -> {
                viewModel.closeActivity()
            }
            is ${NAME}ActivityEvent.TriggerApplicationEvent -> {
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    Scaffold { innerPadding ->
        NavigationHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@OptIn(RouteSafePath::class)
@Composable
private fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController,
        startDestination = ${NAME}ActivityRoute.Example.route,
        modifier = modifier,
    ) {
        ${NAME}ActivityRoute.entries.forEach {
            when (it) {
                ${NAME}ActivityRoute.Example -> composable(it.route) {
                    // ExampleScreen()
                }
            }
        }
    }
}
