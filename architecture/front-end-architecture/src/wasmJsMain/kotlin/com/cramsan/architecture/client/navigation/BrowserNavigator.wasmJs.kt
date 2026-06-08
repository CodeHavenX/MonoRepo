package com.cramsan.architecture.client.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import kotlinx.browser.window

/** Browser implementation — syncs Compose Navigation with the browser History API. */
actual class BrowserNavigator actual constructor() {
    /** Registers a destination-change listener that pushes URLs to browser history and handles popstate. */
    actual fun attach(navController: NavHostController, destinationToPath: (NavBackStackEntry) -> String?) {
        navController.addOnDestinationChangedListener { controller, _, _ ->
            val entry = controller.currentBackStackEntry ?: return@addOnDestinationChangedListener
            val path = destinationToPath(entry) ?: return@addOnDestinationChangedListener
            window.history.pushState(null, "", path)
        }
        jsAddPopstateListener { navController.popBackStack() }
    }

    /** Returns the current browser URL path if it represents a deep-link, or null for the root path. */
    actual fun getInitialPath(): String? {
        val path = window.location.pathname
        return if (path.isBlank() || path == "/") null else path
    }
}

@JsFun("(handler) => window.addEventListener('popstate', handler)")
private external fun jsAddPopstateListener(handler: () -> Unit)
