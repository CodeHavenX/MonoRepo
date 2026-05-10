package com.cramsan.flyerboard.client.lib.navigation

import androidx.navigation.NavHostController

/** No-op Android implementation — Android uses the native intent/deep-link system for URL routing. */
actual class BrowserNavigator actual constructor() {
    /** No-op on Android. */
    actual fun attach(navController: NavHostController) = Unit

    /** Always returns null on Android. */
    actual fun getInitialPath(): String? = null
}
