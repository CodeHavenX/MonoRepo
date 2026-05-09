package com.cramsan.flyerboard.client.lib.navigation

import androidx.navigation.NavHostController

/**
 * Platform-specific adapter that syncs Compose Navigation state with the browser address bar.
 *
 * The wasmJs actual pushes canonical URL paths into the browser History API on every navigation
 * event and handles browser back/forward buttons. The JVM and Android actuals are no-ops.
 */
expect class BrowserNavigator() {
    /**
     * Attaches this navigator to [navController], registering destination-change and popstate listeners.
     * Must be called once after the NavController is ready (e.g. inside a LaunchedEffect).
     */
    fun attach(navController: NavHostController)

    /**
     * Returns the current browser URL path when the app is loaded via a direct URL or bookmark,
     * or null when the app is opened at the root (no deep-link).
     */
    fun getInitialPath(): String?
}
