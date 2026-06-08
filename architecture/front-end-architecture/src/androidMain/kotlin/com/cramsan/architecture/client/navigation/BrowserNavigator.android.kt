package com.cramsan.architecture.client.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

/** No-op Android implementation — Android uses the native intent/deep-link system for URL routing. */
actual class BrowserNavigator actual constructor() {
    /** No-op on Android. */
    actual fun attach(navController: NavHostController, destinationToPath: (NavBackStackEntry) -> String?) = Unit

    /** Always returns null on Android. */
    actual fun getInitialPath(): String? = null
}
