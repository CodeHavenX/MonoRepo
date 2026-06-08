package com.cramsan.architecture.client.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

/** No-op JVM Desktop implementation — the desktop app does not use URL routing. */
actual class BrowserNavigator actual constructor() {
    /** No-op on JVM Desktop. */
    actual fun attach(navController: NavHostController, destinationToPath: (NavBackStackEntry) -> String?) = Unit

    /** Always returns null on JVM Desktop. */
    actual fun getInitialPath(): String? = null
}
