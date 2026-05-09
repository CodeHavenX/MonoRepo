package com.cramsan.flyerboard.client.lib.navigation

import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.cramsan.flyerboard.client.lib.features.auth.AuthDestination
import com.cramsan.flyerboard.client.lib.features.main.MainDestination
import kotlinx.browser.window

/** Browser implementation — syncs Compose Navigation with the browser History API. */
actual class BrowserNavigator actual constructor() {
    actual fun attach(navController: NavHostController) {
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            val entry = controller.currentBackStackEntry ?: return@addOnDestinationChangedListener
            val path =
                when {
                    destination.hasRoute<MainDestination.FlyerListDestination>() -> "/"
                    destination.hasRoute<MainDestination.FlyerDetailDestination>() ->
                        "/flyer/${entry.toRoute<MainDestination.FlyerDetailDestination>().flyerId}"
                    destination.hasRoute<MainDestination.ArchiveDestination>() -> "/archive"
                    destination.hasRoute<MainDestination.MyFlyersDestination>() -> "/my-flyers"
                    destination.hasRoute<MainDestination.FlyerEditDestination>() ->
                        "/my-flyers/edit/${entry.toRoute<MainDestination.FlyerEditDestination>().flyerId}"
                    destination.hasRoute<MainDestination.ModerationQueueDestination>() -> "/moderation"
                    destination.hasRoute<AuthDestination.SignInDestination>() -> "/sign-in"
                    destination.hasRoute<AuthDestination.SignUpDestination>() -> "/sign-up"
                    else -> return@addOnDestinationChangedListener
                }
            window.history.pushState(null, "", path)
        }
        jsAddPopstateListener { navController.popBackStack() }
    }

    actual fun getInitialPath(): String? {
        val path = window.location.pathname
        return if (path.isBlank() || path == "/") null else path
    }
}

@JsFun("(handler) => window.addEventListener('popstate', handler)")
private external fun jsAddPopstateListener(handler: () -> Unit)
