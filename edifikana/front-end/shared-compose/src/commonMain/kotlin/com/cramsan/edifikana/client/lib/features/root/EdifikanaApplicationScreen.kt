package com.cramsan.edifikana.client.lib.features.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cramsan.edifikana.client.lib.features.root.account.AccountActivityScreen
import com.cramsan.edifikana.client.lib.features.root.auth.AuthActivityScreen
import com.cramsan.edifikana.client.lib.features.root.main.MainActivityScreen
import com.cramsan.edifikana.client.lib.features.root.splash.SplashActivityScreen
import com.cramsan.edifikana.client.lib.ui.theme.AppTheme
import org.koin.compose.koinInject

/**
 * Edifikana application screen.
 */
@OptIn(RouteSafePath::class)
@Composable
fun EdifikanaApplicationScreen(
    viewModel: EdifikanaApplicationViewModel = koinInject(),
    eventHandler: EdifikanaMainScreenEventHandler,
) {
    val event by viewModel.events.collectAsState(EdifikanaApplicationEvent.Noop)
    val navController = rememberNavController()

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.enforceAuth()
    }

    LaunchedEffect(event) {
        when (val mainActivityEvent = event) {
            EdifikanaApplicationEvent.Noop -> Unit
            is EdifikanaApplicationEvent.OpenCamera -> {
                eventHandler.openCamera(mainActivityEvent)
            }
            is EdifikanaApplicationEvent.OpenImageExternally -> {
                eventHandler.openImageExternally(mainActivityEvent)
            }
            is EdifikanaApplicationEvent.OpenPhotoPicker -> {
                eventHandler.openPhotoPicker(mainActivityEvent)
            }
            is EdifikanaApplicationEvent.ShareContent -> {
                eventHandler.shareContent(mainActivityEvent)
            }
            is EdifikanaApplicationEvent.ShowSnackbar -> {
                eventHandler.showSnackbar(mainActivityEvent)
            }
            is EdifikanaApplicationEvent.NavigateToActivity -> {
                navController.navigate(mainActivityEvent.destination.path)
            }
            is EdifikanaApplicationEvent.CloseActivity -> {
                navController.popBackStack()
            }
        }
    }

    ComposableKoinContext {
        AppTheme {
            ApplicationNavigationHost(
                navHostController = navController,
            )
        }
    }
}

@OptIn(RouteSafePath::class)
@Composable
private fun ApplicationNavigationHost(
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = ApplicationRoute.Splash.route,
    ) {
        ApplicationRoute.entries.forEach { route ->
            when (route) {
                ApplicationRoute.Splash -> composable(route.route) {
                    SplashActivityScreen()
                }
                ApplicationRoute.Auth -> composable(route.route) {
                    AuthActivityScreen()
                }
                ApplicationRoute.Main -> composable(route.route) {
                    MainActivityScreen()
                }
                ApplicationRoute.Account -> composable(route.route) {
                    AccountActivityScreen()
                }
            }
        }
    }
}

/**
 * Edifikana main screen event handler.
 */
@Composable
expect fun ComposableKoinContext(content: @Composable () -> Unit)
