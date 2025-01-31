package com.cramsan.edifikana.client.lib.features.root.admin

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
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.root.RouteSafePath
import com.cramsan.edifikana.client.lib.features.root.admin.properties.PropertyManagerScreen
import com.cramsan.edifikana.client.lib.features.root.admin.property.PropertyScreen
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import org.koin.compose.koinInject

/**
 * Admin activity screen.
 */
@OptIn(RouteSafePath::class)
@Composable
fun AdminActivityScreen(
    viewModel: AdminActivityViewModel = koinInject(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val navController = rememberNavController()

    val viewModelEvent by viewModel.event.collectAsState(AdminActivityEvent.Noop)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            AdminActivityEvent.Noop -> Unit
            is AdminActivityEvent.Navigate -> {
                navController.navigate(event.destination.path)
            }

            is AdminActivityEvent.CloseActivity -> {
                viewModel.closeActivity()
            }

            is AdminActivityEvent.TriggerApplicationEvent -> {
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    Scaffold(
        topBar = {
            EdifikanaTopBar(
                title = "",
                navHostController = navController,
                onCloseClicked = { viewModel.closeActivity() },
            )
        },
    ) { innerPadding ->
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
        startDestination = AdminActivityRoute.Properties.route,
        modifier = modifier,
    ) {
        AdminActivityRoute.entries.forEach {
            when (it) {
                AdminActivityRoute.Properties -> composable(it.route) {
                    PropertyManagerScreen()
                }
                AdminActivityRoute.Property -> composable(it.route) {
                    PropertyScreen()
                }
            }
        }
    }
}
