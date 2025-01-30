package com.cramsan.edifikana.client.lib.features.root.account

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
import com.cramsan.edifikana.client.lib.features.root.account.account.AccountScreen
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import org.koin.compose.koinInject

/**
 * Account activity screen. It is an activity as it manages internal navigation.
 */
@OptIn(RouteSafePath::class)
@Composable
fun AccountActivityScreen(
    viewModel: AccountActivityViewModel = koinInject(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val navController = rememberNavController()

    val viewModelEvent by viewModel.event.collectAsState(AccountActivityEvent.Noop)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            AccountActivityEvent.Noop -> Unit
            is AccountActivityEvent.Navigate -> {
                navController.navigate(event.destination.route)
            }
            is AccountActivityEvent.TriggerEdifikanaApplicationEvent -> {
                applicationViewModel.executeEvent(event.edifikanaApplicationEvent)
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
        startDestination = AccountActivityRoute.Account.route,
        modifier = modifier,
    ) {
        AccountActivityRoute.entries.forEach {
            when (it) {
                AccountActivityRoute.Account -> composable(it.route) {
                    AccountScreen()
                }
            }
        }
    }
}
