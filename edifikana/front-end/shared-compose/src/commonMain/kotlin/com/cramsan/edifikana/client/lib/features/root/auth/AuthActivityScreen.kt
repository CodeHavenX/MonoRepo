package com.cramsan.edifikana.client.lib.features.root.auth

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cramsan.edifikana.client.lib.features.root.RouteSafePath
import com.cramsan.edifikana.client.lib.features.root.auth.signinv2.SignInV2Screen
import com.cramsan.edifikana.client.lib.features.root.auth.signup.SignUpScreen
import com.cramsan.edifikana.client.lib.ui.components.EdifikanaTopBar
import org.koin.compose.koinInject

/**
 * Auth activity screen.
 */
@OptIn(RouteSafePath::class)
@Composable
fun AuthActivityScreen(
    viewModel: AuthActivityViewModel = koinInject(),
) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStack.collectAsState()

    val event by viewModel.events.collectAsState(AuthActivityEvent.Noop)
    LaunchedEffect(event) {
        when (val event = event) {
            AuthActivityEvent.Noop -> Unit
            is AuthActivityEvent.Navigate -> {
                navController.navigate(event.destination.path)
            }
        }
    }

    Scaffold(
        topBar = {
            EdifikanaTopBar(
                title = "",
                showUpArrow = (backStack.size > 2),
                onUpArrowClicked = { navController.navigateUp() },
                onAccountClicked = null,
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
        startDestination = AuthRoute.SignIn.route,
        modifier = modifier,
    ) {
        AuthRoute.entries.forEach {
            when (it) {
                AuthRoute.SignIn -> composable(it.route) {
                    SignInV2Screen()
                }
                AuthRoute.SignUp -> composable(it.route) {
                    SignUpScreen()
                }
            }
        }
    }
}
