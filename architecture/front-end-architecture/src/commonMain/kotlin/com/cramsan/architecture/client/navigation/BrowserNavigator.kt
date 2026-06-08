package com.cramsan.architecture.client.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

/**
 * Platform-specific adapter that syncs Compose Navigation state with the browser address bar.
 *
 * On wasmJs, this pushes canonical URL paths into the browser History API on every navigation
 * event and handles browser back/forward buttons. On JVM and Android, this is a no-op.
 */
expect class BrowserNavigator() {
    /**
     * Attaches this navigator to [navController], registering destination-change and popstate
     * listeners. The [destinationToPath] callback converts a [NavBackStackEntry] to a canonical
     * URL path string, or returns null for destinations that should not update the browser URL.
     * The [pathToNavAction] callback is invoked with the target URL path when the browser fires a
     * forward-navigation popstate event; the caller should navigate to the corresponding destination.
     * Must be called once after the NavController is ready (e.g. inside a LaunchedEffect).
     */
    fun attach(
        navController: NavHostController,
        destinationToPath: (NavBackStackEntry) -> String?,
        pathToNavAction: (String) -> Unit,
    )

    /**
     * Returns the current browser URL path when the app is loaded via a direct URL or bookmark,
     * or null when the app is opened at the root (no deep-link).
     */
    fun getInitialPath(): String?
}
