package com.cramsan.architecture.client.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import kotlinx.browser.window

/** Browser implementation — syncs Compose Navigation with the browser History API. */
actual class BrowserNavigator actual constructor() {
    /**
     * Registers a destination-change listener that pushes URLs to browser history and handles
     * popstate. Distinguishes browser back (destination still in Compose back stack → popBackStack)
     * from browser forward (destination was popped → pathToNavAction).
     * The URL-comparison guard in the destination-change listener prevents double-pushing to
     * browser history when a popstate event triggers a Compose navigation.
     */
    actual fun attach(
        navController: NavHostController,
        destinationToPath: (NavBackStackEntry) -> String?,
        pathToNavAction: (String) -> Unit,
    ) {
        navController.addOnDestinationChangedListener { controller, _, _ ->
            val entry = controller.currentBackStackEntry ?: return@addOnDestinationChangedListener
            val path = destinationToPath(entry) ?: return@addOnDestinationChangedListener
            if (window.location.pathname + window.location.search != path) {
                window.history.pushState(null, "", path)
            }
        }
        jsAddPopstateListener {
            val targetPath = window.location.pathname + window.location.search
            val isInBackStack =
                navController.currentBackStack.value.any { entry ->
                    destinationToPath(entry) == targetPath
                }
            if (isInBackStack) {
                navController.popBackStack()
            } else {
                pathToNavAction(targetPath)
            }
        }
    }

    /** Returns the current browser URL path+query+fragment if it represents a deep-link, or null for the root path. */
    actual fun getInitialPath(): String? {
        val path = window.location.pathname + window.location.search + window.location.hash
        return if (path.isBlank() || path == "/") null else path
    }
}

@JsFun("(handler) => window.addEventListener('popstate', handler)")
private external fun jsAddPopstateListener(handler: () -> Unit)
